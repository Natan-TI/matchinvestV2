package com.matchinvest.api.services;

import com.matchinvest.api.entities.User;

public interface UserService {
    User registerUser(String name, String email, String rawPassword);
    User findByEmailOrThrow(String email);
}
