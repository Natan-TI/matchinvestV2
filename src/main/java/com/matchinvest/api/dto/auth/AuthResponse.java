package com.matchinvest.api.dto.auth;

public record AuthResponse(String token, String tokenType, long expiresInMs) {}
