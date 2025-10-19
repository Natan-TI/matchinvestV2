package com.matchinvest.api.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="roles")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Role {
	
  @Id 
  @GeneratedValue
  private UUID id;

  @Column(nullable=false, unique=true)
  private String name;
}
