package com.fintech.user_service.dto;
import lombok.Data;
import lombok.NonNull;


@Data
public class LoginDto {


   @NonNull
    private String email;
    @NonNull
    private String password;







}

