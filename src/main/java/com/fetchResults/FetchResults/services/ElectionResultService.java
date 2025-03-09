package com.fetchResults.FetchResults.services;

import com.fetchResults.FetchResults.DTOs.ElectionResultDTO;
import com.fetchResults.FetchResults.entities.*;
import com.fetchResults.FetchResults.repositories.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ElectionResultService {
    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private VoteRepository voteRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Map<Integer, List<ElectionResultDTO>> finalCompiledResults = new HashMap<>();

    private boolean doesResultsTableExist() {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'results'";
        Query query = entityManager.createNativeQuery(sql);
        Long count = (Long) query.getSingleResult();
        return count > 0;
        
    }

    private void createResultsTable() {
        String sql = """
            CREATE TABLE results (
                result_id INT AUTO_INCREMENT PRIMARY KEY,
                votes_count INT,
                election_id INT NOT NULL,
                position_id INT NOT NULL,
                winner_id INT NOT NULL,
                FOREIGN KEY (election_id) REFERENCES elections(election_id),
                FOREIGN KEY (position_id) REFERENCES positions(position_id),
                FOREIGN KEY (winner_id) REFERENCES candidates(candidate_id)
            );
        """;
        entityManager.createNativeQuery(sql).executeUpdate();
        System.out.println("✅ Results table created successfully.");
    }

    public List<ElectionResultDTO> getElectionResults(Integer electionId) {
        List<Result> results = resultRepository.findByElection_ElectionId(electionId);
        Map<Integer, List<Result>> groupedResults = new HashMap<>();

        for (Result result : results) {
            groupedResults
                .computeIfAbsent(result.getPosition().getPositionId(), k -> new ArrayList<>())
                .add(result);
        }

        List<ElectionResultDTO> finalResults = new ArrayList<>();
        for (var positionEntry : groupedResults.entrySet()) {
            Integer positionId = positionEntry.getKey();
            List<Result> votes = positionEntry.getValue();

            Result winner = votes.stream()
                .max(Comparator.comparingInt(Result::getVotesCount))
                .orElse(null);

            Integer winnerId = (winner != null) ? winner.getWinner().getCandidateId() : null;
            String winnerName = (winner != null) ? winner.getWinner().getUser().getFullName() : "N/A";

            List<Object[]> votesList = new ArrayList<>();
            for (Result r : votes) {
                votesList.add(new Object[]{r.getWinner().getCandidateId(), r.getWinner().getUser().getFullName(), r.getVotesCount()});
            }

            Election election = winner != null ? winner.getElection() : electionRepository.findById(electionId).orElse(null);
            Position position = winner != null ? winner.getPosition() : positionRepository.findById(positionId).orElse(null);

            finalResults.add(new ElectionResultDTO(
                electionId, 
                (election != null) ? election.getTitle() : "Unknown Election",
                positionId, 
                (position != null) ? position.getName() : "Unknown Position",
                votesList,
                winnerId, 
                winnerName
            ));
        }

        return finalResults;
    }

    @Transactional
    public List<ElectionResultDTO> compileElectionResults(Integer electionId) {
        //Check if 'results' table exists
        if (!doesResultsTableExist()) {
            System.out.println("Results table does not exist. Creating...");
            createResultsTable();
        }

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        List<Position> positions = positionRepository.findByElection_ElectionId(electionId);
        if (positions.isEmpty()) {
            throw new RuntimeException("No positions found for election ID: " + electionId);
        }

        List<ElectionResultDTO> compiledResults = new ArrayList<>();

        for (Position position : positions) {
            List<Candidate> candidates = candidateRepository.findByPosition_PositionId(position.getPositionId());
            if (candidates.isEmpty()) {
                System.out.println("Skipping position " + position.getPositionId() + " as no candidates are found.");
                continue;
            }

            // Store votes per candidate
            Map<Integer, Integer> candidateVotes = new HashMap<>();
            for (Candidate candidate : candidates) {
                Integer totalVotes = voteRepository.getTotalVotesByCandidateId(candidate.getCandidateId());
                candidateVotes.put(candidate.getCandidateId(), totalVotes);
            }

            // Find the winner
            Integer winnerId = Collections.max(candidateVotes.entrySet(), Map.Entry.comparingByValue()).getKey();

            Candidate winner = candidateRepository.findById(winnerId)
                    .orElseThrow(() -> new RuntimeException("Winner candidate not found"));

            // Prepare votes list
            List<Object[]> votesList = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : candidateVotes.entrySet()) {
                Candidate c = candidateRepository.findById(entry.getKey()).orElse(null);
                String candidateName = (c != null) ? c.getUser().getFullName() : "Unknown";
                votesList.add(new Object[]{entry.getKey(), candidateName, entry.getValue()});
            }

            // Create DTO with election and position names
            ElectionResultDTO resultDTO = new ElectionResultDTO(
                electionId, 
                election.getTitle(), 
                position.getPositionId(), 
                position.getName(), 
                votesList, 
                winnerId, 
                winner.getUser().getFullName()
            );
            compiledResults.add(resultDTO);
        }

        finalCompiledResults.put(electionId, compiledResults);
        return compiledResults;
    }

    @Transactional
    public void publishElectionResults(Integer electionId) {
        List<ElectionResultDTO> compiledResults = finalCompiledResults.get(electionId);
        if (compiledResults == null || compiledResults.isEmpty()) {
            throw new RuntimeException("No compiled results found for election ID: " + electionId);
        }

        for (ElectionResultDTO resultDTO : compiledResults) {
            Result result = new Result();
            result.setElection(electionRepository.findById(electionId).orElseThrow());
            result.setPosition(positionRepository.findById((Integer)resultDTO.getPositionId()).orElseThrow());
            result.setWinner(candidateRepository.findById((Integer)resultDTO.getWinnerId()).orElseThrow());


            // Convert vote list correctly
            List<Integer[]> votesList = (List<Integer[]>) resultDTO.getVotes();
            int maxVotes = votesList.stream().mapToInt(v -> v[1]).max().orElse(0);
            result.setVotesCount(maxVotes);
            
            resultRepository.save(result);
        }

        // Remove from temporary storage after publishing
        finalCompiledResults.remove(electionId);
    }
}