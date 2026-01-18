package com.example.chat.service;

import com.example.chat.domain.OTP;
import com.example.chat.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepo;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendOTP(String email) {
        // Generate random 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Save OTP to database with 5 minutes expiry
        OTP otpRecord = new OTP();
        otpRecord.setEmail(email);
        otpRecord.setOtpCode(otp);
        otpRecord.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otpRecord.setAttempts(0);
        
        otpRepo.save(otpRecord);
        
        // Always print OTP for testing purposes
        System.out.println("=====================================");
        System.out.println("OTP Generated for: " + email);
        System.out.println("OTP Code: " + otp);
        System.out.println("Valid for: 5 minutes");
        System.out.println("=====================================");
        
        // Send email (optional - configure mail properties in application.properties)
        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("Email Verification OTP");
                message.setText("Your OTP is: " + otp + "\n\nValid for 5 minutes.");
                mailSender.send(message);
                System.out.println("Email sent successfully to: " + email);
            } catch (Exception e) {
                System.out.println("Email sending failed: " + e.getMessage());
            }
        } else {
            System.out.println("Mail service not configured. Use the OTP code above for testing.");
        }
    }

    public boolean verifyOTP(String email, String otp) {
        Optional<OTP> otpRecord = otpRepo.findByEmail(email);
        
        // Check if OTP record exists
        if (!otpRecord.isPresent()) {
            System.out.println("OTP not found for email: " + email);
            return false;
        }
        
        OTP record = otpRecord.get();
        
        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(record.getExpiryTime())) {
            System.out.println("OTP expired for email: " + email);
            otpRepo.delete(record);
            return false;
        }
        
        // Check if OTP matches
        if (record.getOtpCode().equals(otp)) {
            System.out.println("OTP verified successfully for email: " + email);
            otpRepo.delete(record);
            return true;
        }
        
        // OTP mismatch - Increment attempts
        record.setAttempts(record.getAttempts() + 1);
        System.out.println("OTP mismatch for email: " + email + ". Attempts: " + record.getAttempts());
        
        // Delete record if max attempts reached
        if (record.getAttempts() >= 5) {
            System.out.println("Max OTP attempts reached for email: " + email);
            otpRepo.delete(record);
            return false;
        }
        
        // Save updated attempt count
        otpRepo.save(record);
        return false;
    }
}

