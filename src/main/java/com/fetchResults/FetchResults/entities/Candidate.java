package com.fetchResults.FetchResults.entities;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name="candidates")
@Entity
public class Candidate {
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "candidate_id")
	private Integer candidateId;

	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "election_id", referencedColumnName = "election_id", nullable = false)
    private Election election;
    
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
	private EpollUsers user;

	@Column(name = "created_at")
	@CreationTimestamp
	private LocalDateTime createdAt;
    
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private CandidateStatus status;

    public Integer getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(Integer candidateId) {
		this.candidateId = candidateId;
	}

	public Election getElection() {
		return election;
	}

	public void setElection(Election election) {
		this.election = election;
	}

	public EpollUsers getUser() {
		return user;
	}

	public void setUser(EpollUsers user) {
		this.user = user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public CandidateStatus getStatus() {
		return status;
	}

	public void setStatus(CandidateStatus status) {
		this.status = status;
	}

	public String getCampaignStatement() {
		return campaignStatement;
	}

	public void setCampaignStatement(String campaignStatement) {
		this.campaignStatement = campaignStatement;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@Column(name="campaign_statement")
    private String campaignStatement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="position_id", referencedColumnName = "position_id", nullable = false)
    private Position position;
}
