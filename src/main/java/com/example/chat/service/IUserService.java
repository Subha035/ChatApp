package com.example.chat.service;

import com.example.chat.domain.User;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    User register (User user);
    String deleteUser(String email);
    List<User> getaByFirstName(String firstName);
    List<User> getaByLastName(String lastName);
    Optional<User> getDataByEmail(String email);
    User findByEmail(String email);
}