package com.fetchResults.FetchResults.entities;

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
    @Column(name = "result_id")
    private Integer resultId;
   
    @ManyToOne
    @JoinColumn(name = "election_id", referencedColumnName = "election_id", nullable = false)
    private Election election;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;
    
    @ManyToOne
    @JoinColumn(name = "winner_id", nullable = false)
    private Candidate winner;

    @Column(name = "votes_count")
    private Integer votesCount;

    public Integer getResultId() {
        return resultId;
    }

    public Election getElection() {
        return election;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Candidate getWinner(){
        return winner;
    }

    public void setWinner(Candidate winner){
        this.winner = winner;
    }

    public int getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }
}
