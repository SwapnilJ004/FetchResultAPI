package com.fetchResults.FetchResults.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
	
@Table(name = "positions")
@Entity
public class Position {
	
	public Integer getPositionId() {
		return positionId;
	}

	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	public Election getElection() {
		return election;
	}

	public Integer getElectionId() {
		return election.getElectionId();
	}

	public void setElection(Election election) {
		this.election = election;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "position_id")
	private Integer positionId;
	
	@ManyToOne
	@JoinColumn(name = "election_id", referencedColumnName = "election_id")
	@JsonBackReference
	private Election election;
	
	@Column(name = "name")
	private String name;
	
//	@Column(name = "description")
//	private String description;
	
	@Column(name = "created_at")
	private LocalDate createdAt;
}
