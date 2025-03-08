package com.fetchResults.FetchResults.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fetchResults.FetchResults.entities.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {
    List<Position> findByElection_ElectionId(Integer electionId);

    Position findByPositionId(Integer positionId);
}
