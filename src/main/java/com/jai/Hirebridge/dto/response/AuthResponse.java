package com.jai.Hirebridge.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;

    private String email;

    private String role; // CANDIDATE, RECRUITER, or ADMIN

    @Builder.Default
    private String tokenType = "Bearer";
}