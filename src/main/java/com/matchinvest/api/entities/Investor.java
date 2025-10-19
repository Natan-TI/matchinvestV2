package com.matchinvest.api.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.matchinvest.api.enums.RiskProfile;
import com.matchinvest.api.vo.Email;
import com.matchinvest.api.vo.Money;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "investors")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Investor {
	
	@Id
	@GeneratedValue
	private UUID id;
	
	@Column(nullable = false, length = 120)
	private String name;
	
	@Embedded
	private Email email;
	
	@OneToOne
	@JoinColumn(name = "user_id", unique = true, nullable = false)
	private User user;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
	private RiskProfile riskProfile;
	
	@Embedded
	private Money avaliableAmount;
	
	@Column(columnDefinition = "TEXT")
	private String goals;
	
	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}
