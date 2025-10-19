package com.matchinvest.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchinvest.api.dto.AdvisorCreateDTO;
import com.matchinvest.api.dto.AdvisorResponseDTO;
import com.matchinvest.api.dto.AdvisorUpdateDTO;
import com.matchinvest.api.services.AdvisorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/advisors")
@RequiredArgsConstructor
public class AdvisorController {
	
	private final AdvisorService service;
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<AdvisorResponseDTO> create(@Valid @RequestBody AdvisorCreateDTO dto) {
		AdvisorResponseDTO advisor = service.create(dto);
		return ResponseEntity.created(URI.create("api/advisors/" + advisor.id())).body(advisor);
	}
	
	@GetMapping
	public ResponseEntity<List<AdvisorResponseDTO>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}
	
	@GetMapping("/{advisorId}")
	public ResponseEntity<AdvisorResponseDTO> findById(@PathVariable("advisorId") UUID advisorId) {
		return ResponseEntity.ok(service.findById(advisorId));
	}
	
	@PreAuthorize("hasAnyRole('INVESTOR', 'ADVISOR')")
	@PutMapping("/{advisorId}")
	public ResponseEntity<AdvisorResponseDTO> update(@PathVariable("advisorId") UUID advisorId, @Valid @RequestBody AdvisorUpdateDTO dto) {
		return ResponseEntity.ok(service.update(advisorId, dto));
	}
	
	@PreAuthorize("hasAnyRole('INVESTOR', 'ADVISOR')")
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteOwnAccount() {
        service.deleteOwnAccount();
        return ResponseEntity.noContent().build();
    }
}
