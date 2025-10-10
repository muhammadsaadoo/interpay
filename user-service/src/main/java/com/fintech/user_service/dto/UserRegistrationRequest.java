package com.fintech.user_service.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequest {

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    // simple phone pattern, adjust if you want a different format
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9+\\- ]{7,20}$", message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Account type is required")
    private String accountType; // PERSONAL, BUSINESS, MERCHANT
}

