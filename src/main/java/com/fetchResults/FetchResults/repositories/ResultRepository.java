package com.fetchResults.FetchResults.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fetchResults.FetchResults.entities.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {
    List<Result> findByElectionId(Integer electionId);
}