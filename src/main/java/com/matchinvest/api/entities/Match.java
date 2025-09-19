package com.matchinvest.api.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.matchinvest.api.enums.MatchStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "matches")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Match {
	
	@Id
	@GeneratedValue
	private UUID id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "advisor_id")
	private Advisor advisor;

	@ManyToOne(optional = false)
	@JoinColumn(name = "investor_id")
	private Investor investor;
	
	@Column(nullable = false)
	private Double score;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
	private MatchStatus status;
	
	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}
