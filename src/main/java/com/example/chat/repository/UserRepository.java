package com.example.chat.repository;

import com.example.chat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User , String> {
    List<User> findByFirstName (String firstname );
    List<User> findByLastName (String lastName );
    User findByEmailAndPassWord (String email , String passWord );
    Optional<User> findByEmail(String email);
    
}
