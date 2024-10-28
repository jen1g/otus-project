package com.otushomework.userservice.service.impl;

import com.otushomework.userservice.entity.User;
import com.otushomework.userservice.repository.UserRepository;
import com.otushomework.userservice.service.BillingServiceClient;
import com.otushomework.userservice.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final BillingServiceClient billingServiceClient;

    @Autowired
    public UserServiceImpl(UserRepository repository, BillingServiceClient billingServiceClient) {
        this.repository = repository;
        this.billingServiceClient = billingServiceClient;
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder().encode(user.getPassword()));
        User savedUser = repository.save(user);
        createBillingAccount(savedUser);
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void updateUserById(User updatedUser) {
        repository.save(updatedUser);
    }

    @Override
    public void deleteUserById(Long userId) {
        repository.deleteById(userId);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return repository.findUserByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }


    private void createBillingAccount(User user) {
        try {
            String response = billingServiceClient.createBillingAccount(user.getId());
            System.out.println("Аккаунт создан с id: " + user.getId());
        } catch (Exception e) {
            System.out.println("Не удалось создать аккаунт пользователя с id: " + user.getId());
            e.printStackTrace();
        }
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
