package com.fetchResults.FetchResults.DTOs;

import java.util.ArrayList;
import java.util.List;

public class ElectionResultDTO {
    private Integer electionId;
    private String electionName;
    private Integer positionId;
    private String positionName;
    private List<Object[]> votes; // Array format [candidateId, voteCount]
    private Integer winnerId;
    private String winnerName;

    public ElectionResultDTO(Integer electionId, String electionName, Integer positionId, String positionName, List<Object[]> votes, Integer winnerId, String winnerName) {
        this.electionId = electionId;
        this.electionName = electionName;
        this.positionId = positionId;
        this.positionName = positionName;
        this.votes = votes;
        this.winnerId = winnerId;
        this.winnerName = winnerName;
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

    public List<Integer[]> getVotes() {
        List<Integer[]> formattedVotes = new ArrayList<>();
        for (Object obj : this.votes) {
            if (obj instanceof Object[] arr && arr.length == 3) {
                formattedVotes.add(new Integer[]{(Integer) arr[0], (Integer) arr[2]});
            }
        }
        return formattedVotes;
    }

    public List<Object[]> getVotes_Integers() {
        return votes;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public Object getWinnerName(){
        return winnerName;
    }
}
