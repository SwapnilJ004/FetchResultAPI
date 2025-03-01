package com.fetchResults.FetchResults.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fetchResults.FetchResults.entities.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    
    // Get all candidates contesting for a given position
    List<Candidate> findByPosition_PositionId(Integer positionId);
}
