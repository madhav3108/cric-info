package com.example.cric_info.Repository;

import com.example.cric_info.Entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepo extends JpaRepository<Match, Integer> {
    // MATCH FETCHING by team name
    Optional<Match> findByTeamHeading(String teamHeading);
}
