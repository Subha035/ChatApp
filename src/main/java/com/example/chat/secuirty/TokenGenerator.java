package com.example.chat.secuirty;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import com.example.chat.domain.User;

@Service
public class TokenGenerator {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    // This generates a secure random 512-bit key

    public Map<String, String> userToken(User user){
        Map<String, String> result = new HashMap<>();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        userData.put("address", user.getAddress());

        String mytoken = Jwts.builder()
                .setClaims(userData)
                .signWith(key)   // use the secure key
                .compact();

        result.put("token", mytoken);
        return result;
    }
}
