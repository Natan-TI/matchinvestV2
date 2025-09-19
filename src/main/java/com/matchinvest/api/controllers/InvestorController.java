package com.matchinvest.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchinvest.api.dto.InvestorCreateDTO;
import com.matchinvest.api.dto.InvestorResponseDTO;
import com.matchinvest.api.services.InvestorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/investors")
@RequiredArgsConstructor
public class InvestorController {
	
	private final InvestorService service;
	
	@PostMapping
	public ResponseEntity<InvestorResponseDTO> create(@Valid @RequestBody InvestorCreateDTO dto) {
		InvestorResponseDTO investor = service.create(dto);
		return ResponseEntity.created(URI.create("api/investors/" + investor.id())).body(investor);
	}
	
	@GetMapping
	public ResponseEntity<List<InvestorResponseDTO>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}
	
	@GetMapping("/{investorId}")
	public ResponseEntity<InvestorResponseDTO> findById(@PathVariable("investorId")  UUID investorId) {
		return ResponseEntity.ok(service.findById(investorId));
	}
	
	@PutMapping("/{investorId}")
	public ResponseEntity<InvestorResponseDTO> update(@PathVariable("investorId") UUID investorId, @Valid @RequestBody InvestorCreateDTO dto) {
		return ResponseEntity.ok(service.update(investorId, dto));
	}
	
	@DeleteMapping("/{investorId}")
	public ResponseEntity<Void> deleteById(@PathVariable("investorId") UUID investorId) {
		service.delete(investorId);
		return ResponseEntity.noContent().build();
	}
}
