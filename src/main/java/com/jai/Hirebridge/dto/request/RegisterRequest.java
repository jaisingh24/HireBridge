package com.jai.Hirebridge.dto.request;



import com.jai.Hirebridge.model.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(max = 100)
    private String name;

    @NotBlank @Email @Size(max = 100)
    private String email;

    @NotBlank @Size(min = 6)
    private String password;

    @NotNull
    private Role role;

    private String phone;
}
