package com.fetchResults.FetchResults.entities;

public class ElectionId {
    private Integer electionId;

    public Integer getElectionId() {
        return electionId;
    }

    public void setElectionId(Integer electionId) {
        this.electionId = electionId;
    }

    @Override
    public String toString() {
        return "ElectionId{" +
                "electionId=" + electionId +
                '}';
    }
}
