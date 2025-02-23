package com.fetchResults.FetchResults.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fetchResults.FetchResults.entities.Election;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Integer> {

}