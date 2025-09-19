package com.matchinvest.api.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matchinvest.api.entities.Advisor;
import com.matchinvest.api.entities.Investor;
import com.matchinvest.api.entities.Match;
import com.matchinvest.api.enums.MatchStatus;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID>{
	List<Match> findByInvestor(Investor investor);
    List<Match> findByAdvisor(Advisor advisor);
    List<Match> findByStatus(MatchStatus status);
}
