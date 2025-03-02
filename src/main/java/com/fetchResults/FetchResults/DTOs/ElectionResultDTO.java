package com.fetchResults.FetchResults.DTOs;

import java.util.List;

public class ElectionResultDTO {
    private Integer electionId;
    private Integer positionId;
    private List<Integer[]> votes; // Array format [candidateId, voteCount]
    private Integer winner;

    public ElectionResultDTO(Integer electionId, Integer positionId, List<Integer[]> votes, Integer winner) {
        this.electionId = electionId;
        this.positionId = positionId;
        this.votes = votes;
        this.winner = winner;
    }

    public Object getElectionId() {
        return electionId;
    }

    public Object getPositionId() {
        return positionId;
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
