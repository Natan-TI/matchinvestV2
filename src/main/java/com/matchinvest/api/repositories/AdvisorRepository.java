package com.matchinvest.api.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matchinvest.api.entities.Advisor;
import com.matchinvest.api.enums.InvestmentFocus;

@Repository
public interface AdvisorRepository extends JpaRepository<Advisor, UUID>{
	List<Advisor> findByInvestmentFocus(InvestmentFocus focus);
}
