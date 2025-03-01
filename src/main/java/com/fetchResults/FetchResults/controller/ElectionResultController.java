package com.fetchResults.FetchResults.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fetchResults.FetchResults.DTOs.ElectionResultDTO;

import com.fetchResults.FetchResults.services.ElectionResultService;

@RestController
@RequestMapping("/api/results")
public class ElectionResultController {
    @Autowired
    private ElectionResultService electionResultService;

    @GetMapping("/{electionId}")
    public ResponseEntity<List<Map<String, Object>>> getElectionResults(@PathVariable Integer electionId) {
        List<ElectionResultDTO> results = electionResultService.getElectionResults(electionId);

        // Convert DTOs to Map<String, Object>
        List<Map<String, Object>> response = results.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("electionId", result.getElectionId());
            map.put("positionId", result.getPositionId());
            map.put("votes", result.getVotes()); // List of [candidateId, votesCount]
            map.put("winner", result.getWinner());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/compile/{electionId}")
    // @PreAuthorize("hasRole('ADMIN')") // Restrict access to admins
    public ResponseEntity<List<Map<String, Object>>> compileElectionResults(@PathVariable Integer electionId) {
        List<ElectionResultDTO> compiledResults = electionResultService.compileElectionResults(electionId);

        List<Map<String, Object>> response = compiledResults.stream().map(result -> {
            Map<String, Object> map = new HashMap<>();
            map.put("electionId", result.getElectionId());
            map.put("positionId", result.getPositionId());
            map.put("votes", result.getVotes());
            map.put("winner", result.getWinner());
            return map;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/publish/{electionId}")
    public ResponseEntity<Map<String, String>> publishElectionResults(@PathVariable Integer electionId) {
        electionResultService.publishElectionResults(electionId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Election results published successfully for electionId: " + electionId);
        return ResponseEntity.ok(response);
    }
}

