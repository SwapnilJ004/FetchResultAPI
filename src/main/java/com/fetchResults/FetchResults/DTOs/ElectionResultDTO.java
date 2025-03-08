package com.fetchResults.FetchResults.DTOs;

import java.util.List;

public class ElectionResultDTO {
    private Integer electionId;
    private String electionName;
    private Integer positionId;
    private String positionName;
    private List<Integer[]> votes; // Array format [candidateId, voteCount]
    private Integer winner;

    public ElectionResultDTO(Integer electionId, String electionName, Integer positionId, String positionName, List<Integer[]> votes, Integer winner) {
        this.electionId = electionId;
        this.electionName = electionName;
        this.positionId = positionId;
        this.positionName = positionName;
        this.votes = votes;
        this.winner = winner;
    }

    public Object getElectionId() {
        return electionId;
    }

    public Object getElectionName(){
        return electionName;
    }

    public Object getPositionId() {
        return positionId;
    }

    public Object getPositionName(){
        return positionName;
    }
    public Object getVotes() {
        return votes;
    }

    public List<Integer[]> getVotes_Integers() {
        return votes;
    }

    public Object getWinner() {
        return winner;
    }
}
