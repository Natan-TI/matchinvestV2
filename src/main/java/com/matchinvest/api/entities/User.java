package com.matchinvest.api.entities;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name="users")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class User {
  @Id 
  @GeneratedValue
  private UUID id;

  @Column(nullable=false) 
  private String name;
  
  @Column(nullable=false, unique=true) 
  private String email;
  
  @Column(nullable=false) 
  private String password;
  
  @Column(nullable=false) 
  private boolean enabled = true;
  
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name="users_roles",
     joinColumns = @JoinColumn(name="user_id"),
     inverseJoinColumns = @JoinColumn(name="role_id"))
  private Set<Role> roles;

  
  public boolean hasRole(String roleName) {
	    if (roles == null) return false;
	    return roles.stream()
	            .anyMatch(r -> r.getName().equalsIgnoreCase(roleName) ||
	                           r.getName().equalsIgnoreCase("ROLE_" + roleName));
  }	
}
