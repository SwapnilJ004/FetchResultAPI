package com.fetchResults.FetchResults.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resultId;

    @Column(name = "election_id")
    private Integer electionId;

    @Column(name = "position_id")
    private Integer positionId;

    @Column(name = "winner_id")
    private Integer winnerId;

    @Column(name = "votes_count")
    private Integer votesCount;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    public Integer getElectionId() {
        return electionId;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public int getVotesCount() {
        return votesCount;
    }
}
