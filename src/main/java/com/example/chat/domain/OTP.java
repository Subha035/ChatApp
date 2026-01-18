package com.example.chat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "otp_store")
public class OTP {
    @Id
    private String email;
    private String otpCode;
    private LocalDateTime expiryTime;
    private int attempts;
}
