package com.matchinvest.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchinvest.api.dto.MatchCreateDTO;
import com.matchinvest.api.dto.MatchResponseDTO;
import com.matchinvest.api.services.MatchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

	private final MatchService service;
	
	
	@PostMapping
	public ResponseEntity<MatchResponseDTO> create(@Valid @RequestBody MatchCreateDTO dto) {
		MatchResponseDTO match = service.create(dto);
		return ResponseEntity.created(URI.create("/api/matches/" + match.id())).body(match);
	}
	
	@GetMapping
	public ResponseEntity<List<MatchResponseDTO>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<MatchResponseDTO> findById(@PathVariable("id") UUID id) {
		return ResponseEntity.ok(service.findById(id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {
		service.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/investor/{investorId}")
	public ResponseEntity<List<MatchResponseDTO>> findByInvestor(@PathVariable("investorId") UUID investorId) {
		return ResponseEntity.ok(service.findByInvestor(investorId));
	}
	
	@GetMapping("/advisor/{advisorId}")
	public ResponseEntity<List<MatchResponseDTO>> findByAdvisor(@PathVariable("advisorId") UUID advisorId) {
		return ResponseEntity.ok(service.findByAdvisor(advisorId));
	}
	
	@PatchMapping("/{id}/accept")
	public ResponseEntity<MatchResponseDTO> accept(@PathVariable("id") UUID id) {
		return ResponseEntity.ok(service.accept(id));
	}
	
	@PatchMapping("/{id}/reject")
	public ResponseEntity<MatchResponseDTO> reject(@PathVariable("id") UUID id) {
		return ResponseEntity.ok(service.reject(id));
	}
	
}
