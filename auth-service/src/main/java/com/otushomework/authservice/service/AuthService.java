package com.otushomework.authservice.service;

import com.otushomework.authservice.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class AuthService {

    @Value("${user.service.url}")
    private String userServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<UserDTO> getUserByUsername(String username) {
        String endpoint = String.format("%s/user?username=%s", userServiceUrl, username);
        System.out.println(endpoint);
        try {
            UserDTO user = restTemplate.getForObject(endpoint, UserDTO.class);
            if (user != null && user.getId() != 0) {
                return Optional.of(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<UserDTO> getUserById(String userId) {
        String endpoint = String.format("%s/user/%s", userServiceUrl, userId);
        try {
            UserDTO user = restTemplate.getForObject(endpoint, UserDTO.class);
            if (user != null && user.getId() != 0) {
                return Optional.of(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}