package com.fintech.user_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@NoArgsConstructor  // Required for JSON mapping
@AllArgsConstructor
public class LoginDto {



    private String email;

    private String password;







}

