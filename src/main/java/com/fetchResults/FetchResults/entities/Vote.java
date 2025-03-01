package com.fetchResults.FetchResults.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Integer id;  // Primary Key (AUTO_INCREMENT)

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private EpollUsers user;

    @ManyToOne
    @JoinColumn(name = "election_id", referencedColumnName = "election_id", nullable = false)
    private Election election;

    @ManyToOne
    @JoinColumn(name = "candidate_id", referencedColumnName = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "voting_time", nullable = false)
    private LocalDateTime votingTime;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "ip_address", columnDefinition = "TEXT")
    private String ipAddress;

    // ✅ Default Constructor
    public Vote() {}

    // ✅ Constructor
    public Vote(EpollUsers user, Election election, Candidate candidate, LocalDateTime votingTime, Boolean verified, String ipAddress) {
        this.user = user;
        this.election = election;
        this.candidate = candidate;
        this.votingTime = votingTime;
        this.verified = verified;
        this.ipAddress = ipAddress;
    }

    // ✅ Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public EpollUsers getUser() { return user; }
    public void setUser(EpollUsers user) { this.user = user; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public LocalDateTime getVotingTime() { return votingTime; }
    public void setVotingTime(LocalDateTime votingTime) { this.votingTime = votingTime; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
