package com.otushomework.userservice.service;

import com.otushomework.userservice.entity.User;

import java.util.Optional;

public interface UserService {

    User saveUser(User user);
    Optional<User> getUserById(Long userId);
    void updateUserById(User updatedUser);
    void deleteUserById(Long userId);
    Optional<User> findUserByUsername(String username);
    boolean existsByUsername(String username);

}
