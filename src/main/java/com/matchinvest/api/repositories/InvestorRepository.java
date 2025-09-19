package com.matchinvest.api.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matchinvest.api.entities.Investor;
import com.matchinvest.api.enums.RiskProfile;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, UUID>{
	List<Investor> findByRiskProfile(RiskProfile riskProfile);
}
