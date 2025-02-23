package com.fetchResults.FetchResults.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fetchResults.FetchResults.repositories.ResultRepository;
import com.fetchResults.FetchResults.DTOs.ElectionResultDTO;
import com.fetchResults.FetchResults.entities.Result;

@Service
public class ElectionResultService {
    @Autowired
    private ResultRepository resultRepository;

    public List<ElectionResultDTO> getElectionResults(Integer electionId) {
        List<Result> results = resultRepository.findByElectionId(electionId); // Fetch only relevant results
        Map<Integer, List<Result>> groupedResults = new HashMap<>();

        for (Result result : results) {
            groupedResults
                .computeIfAbsent(result.getPositionId(), k -> new ArrayList<>())
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

            Integer winnerId = (winner != null) ? winner.getCandidate().getCandidateId() : null;

            // Prepare votes list for DTO
            List<Integer[]> votesList = new ArrayList<>();
            for (Result r : votes) {
                votesList.add(new Integer[]{r.getCandidate().getCandidateId(), r.getVotesCount()});
            }

            finalResults.add(new ElectionResultDTO(electionId, positionId, votesList, winnerId));
        }

        return finalResults;
    }
}
