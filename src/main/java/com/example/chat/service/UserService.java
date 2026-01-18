 package com.example.chat.service;

import com.example.chat.domain.User;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepo;


    @Override
    public User register(User user) {
        return userRepo.save(user);
    }



    @Override
    public String deleteUser(String email) {
        userRepo.deleteById(email);
        return "Account Deleted Successsfully";
    }

    @Override
    public List<User> getaByFirstName(String firstName) {
        return userRepo.findByFirstName(firstName);
    }

    @Override
    public List<User> getaByLastName(String lastName) {
        return userRepo.findByLastName(lastName);
    }

    @Override
    public Optional<User> getDataByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        return null;
    }



    public User getByEmailAndPassword(String email, String passWord) {
        return userRepo.findByEmailAndPassWord(email ,passWord);
    }
    
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}
