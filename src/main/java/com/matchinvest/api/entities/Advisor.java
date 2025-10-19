package com.matchinvest.api.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.matchinvest.api.enums.InvestmentFocus;
import com.matchinvest.api.vo.Email;

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
@Table(name = "advisors")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Advisor {
	
	@Id
	@GeneratedValue
	private UUID id;
	
	@Column(nullable = false, length = 120)
	private String name;
	
	@Embedded
	private Email email;
	
	@Column(columnDefinition = "TEXT")
	private String certifications;
	
	@OneToOne
	@JoinColumn(name = "user_id", unique = true, nullable = false)
	private User user;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
	private InvestmentFocus investmentFocus;
	
	@Column(nullable = false)
	private Integer yearsExperience;
	
	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}
