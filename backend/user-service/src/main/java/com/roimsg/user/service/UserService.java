package com.roimsg.user.service;

import com.roimsg.user.entity.User;
import com.roimsg.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> list(String q) {
        if (q != null && !q.isBlank()) {
            return userRepository.findByEmailContainingIgnoreCase(q);
        }
        return userRepository.findAll();
    }

    public Optional<User> get(UUID id) { return userRepository.findById(id); }

    public User create(User user) { return userRepository.save(user); }

    public Optional<User> update(UUID id, User data) {
        return userRepository.findById(id).map(u -> {
            u.setName(data.getName());
            u.setEmail(data.getEmail());
            u.setPhoneNumber(data.getPhoneNumber());
            u.setAddress(data.getAddress());
            u.setProfileImageUrl(data.getProfileImageUrl());
            u.setCustomProfileImageUrl(data.getCustomProfileImageUrl());
            u.setIsActive(data.getIsActive());
            return userRepository.save(u);
        });
    }

    public void delete(UUID id) { userRepository.deleteById(id); }
}