package com.fetchResults.FetchResults.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fetchResults.FetchResults.entities.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    // Get total votes received by a candidate
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.candidate.id = :candidateId")
    Integer getTotalVotesByCandidateId(@Param("candidateId") Integer candidateId);
}
