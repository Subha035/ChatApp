package com.example.chat.controller;

import com.example.chat.domain.User;
import com.example.chat.service.UserService;
import com.example.chat.service.OTPService;
import com.example.chat.secuirty.TokenGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userSer;

    @Autowired
    private TokenGenerator TokenGen;

    @Autowired
    private OTPService otpService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return new ResponseEntity<>(userSer.register(user), HttpStatus.CREATED);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        otpService.sendOTP(email);
        return new ResponseEntity<>(Map.of("message", "OTP sent successfully"), HttpStatus.OK);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        
        if (otpService.verifyOTP(email, otp)) {
            return new ResponseEntity<>(Map.of("message", "Email verified successfully"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Map.of("message", "Invalid or expired OTP"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> Userlogin(@RequestBody User user){
        User validUser = userSer.getByEmailAndPassword(user.getEmail(), user.getPassWord());
        if (validUser != null) {
            // Don't return token yet, send OTP first
            return new ResponseEntity<>("Valid credentials", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/send-login-otp")
    public ResponseEntity<?> sendLoginOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        otpService.sendOTP(email);
        return new ResponseEntity<>(Map.of("message", "Login OTP sent successfully"), HttpStatus.OK);
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<?> verifyLoginOTP(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        
        if (otpService.verifyOTP(email, otp)) {
            // OTP verified, now return user data and token
            User user = userSer.getDataByEmail(email)
                    .orElse(null);
            
            if (user != null) {
                return new ResponseEntity<>(TokenGen.userToken(user), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(Map.of("message", "Invalid or expired OTP"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/firstName/{firstName}")
    public ResponseEntity<?> gettingFirstName(@PathVariable String firstName) {
        return new ResponseEntity<>(userSer.getaByFirstName(firstName), HttpStatus.FOUND);
    }

    @GetMapping("/lastName/{lastName}")
    public ResponseEntity<?> gettinglastName(@PathVariable String lastName) {
        return new ResponseEntity<>(userSer.getaByLastName(lastName), HttpStatus.FOUND);
    }

    @DeleteMapping("/deleteUser/{email}")
    public ResponseEntity<?> deleting(@PathVariable String email) {
        return new ResponseEntity<>(userSer.deleteUser(email), HttpStatus.OK);
    }


@GetMapping("/profile/{email}")
public ResponseEntity<?> getByEmail(@PathVariable("email") String email) {

    return userSer.getDataByEmail(email)
            .<ResponseEntity<?>>map(user ->
                    ResponseEntity.ok(user)
            )
            .orElseGet(() ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "User not found"))
            );
            
    }
}