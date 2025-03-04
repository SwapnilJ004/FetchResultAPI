package com.fetchResults.FetchResults.services;

import com.fetchResults.FetchResults.DTOs.ElectionResultDTO;
import com.fetchResults.FetchResults.entities.Candidate;
import com.fetchResults.FetchResults.entities.Election;
import com.fetchResults.FetchResults.entities.Position;
import com.fetchResults.FetchResults.entities.Result;
import com.fetchResults.FetchResults.repositories.CandidateRepository;
import com.fetchResults.FetchResults.repositories.ElectionRepository;
import com.fetchResults.FetchResults.repositories.PositionRepository;
import com.fetchResults.FetchResults.repositories.ResultRepository;
import com.fetchResults.FetchResults.repositories.VoteRepository;

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
            
            // Find the candidate with max votes
            Result winner = votes.stream()
                .max(Comparator.comparingInt(Result::getVotesCount))
                .orElse(null);

            Integer winnerId = (winner != null) ? winner.getWinner().getCandidateId() : null;

            // Prepare votes list for DTO
            List<Integer[]> votesList = new ArrayList<>();
            for (Result r : votes) {
                votesList.add(new Integer[]{r.getWinner().getCandidateId(), r.getVotesCount()});
            }

            finalResults.add(new ElectionResultDTO(electionId, positionId, votesList, winnerId));
        }

        return finalResults;
    }

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

    @Transactional
    public List<ElectionResultDTO> compileElectionResults(Integer electionId) {
        //Check if 'results' table exists
        if (!doesResultsTableExist()) {
            System.out.println("Results table does not exist. Creating...");
            createResultsTable();
        }

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        //Check if results already exist for this election
        List<Result> existingResults = resultRepository.findByElection_ElectionId(electionId);
        if (!existingResults.isEmpty()) {
            System.out.println("Results already exist for election ID: " + electionId);
            return Collections.emptyList(); // Exit without making changes
        }

        System.out.println("Election :" + election.getTitle());

        List<Position> positions = positionRepository.findByElection_ElectionId(electionId);
        if (positions.isEmpty()) {
            throw new RuntimeException("No positions found for election ID: " + electionId);
        }

        List<ElectionResultDTO> compiledResults = new ArrayList<>();

        System.out.println("Election id is: " + electionId);
        System.out.print("Position array is:");
        for (Position position : positions) {
            System.out.print(position.getPositionId() + " ");

            List<Candidate> candidates = candidateRepository.findByPosition_PositionId(position.getPositionId());
            if (candidates.isEmpty()) {
                System.out.println("Skipping position " + position.getPositionId() + " as no candidates are found.");
                continue;
            }

            //Store votes per candidate
            Map<Integer, Integer> candidateVotes = new HashMap<>();
            for (Candidate candidate : candidates) {
                Integer totalVotes = voteRepository.getTotalVotesByCandidateId(candidate.getCandidateId());
                candidateVotes.put(candidate.getCandidateId(), totalVotes);
            }

            //Find the winner
            Integer winnerId = Collections.max(candidateVotes.entrySet(), Map.Entry.comparingByValue()).getKey();
            if (winnerId == null || !candidateVotes.containsKey(winnerId)) {
                throw new RuntimeException("Winner candidate not found in votes mapping.");
            }

            // Prepare votes list
            List<Integer[]> votesList = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : candidateVotes.entrySet()) {
                votesList.add(new Integer[]{entry.getKey(), entry.getValue()});
            }

            // Create DTO
            ElectionResultDTO resultDTO = new ElectionResultDTO(electionId, position.getPositionId(), votesList, winnerId);
            compiledResults.add(resultDTO);

            System.out.println("Added result: Position ID " + position.getPositionId() + " | Winner ID " + winnerId);
        }
        
        finalCompiledResults.put(electionId, compiledResults);
        System.out.println("Final compiled results: " + compiledResults.size());

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
            result.setPosition(positionRepository.findById((Integer) resultDTO.getPositionId()).orElseThrow());
            result.setWinner(candidateRepository.findById((Integer) resultDTO.getWinner()).orElseThrow());

            List<Integer[]> votesList = resultDTO.getVotes_Integers();
            int maxVotes = votesList.stream().mapToInt(v -> v[1]).max().orElse(0);
            result.setVotesCount(maxVotes);
            
            resultRepository.save(result);
            System.out.println("Result is:" + result.getResultId());
            System.out.println("Saved Result is:" + result.getResultId());
        }

        // Remove from temporary storage after publishing
        finalCompiledResults.remove(electionId);
    }
}