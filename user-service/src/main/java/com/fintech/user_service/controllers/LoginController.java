package com.fintech.user_service.controllers;


import com.fintech.user_service.dto.EmailVerificationRequest;
import com.fintech.user_service.dto.LoginDto;
import com.fintech.user_service.services.LoginServiceImpl;
import com.fintech.user_service.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final  LoginServiceImpl loginService;
    private JwtUtil jwtUtil;


@PostMapping
public ResponseEntity<?> login(@RequestBody LoginDto user) {
    System.out.println("Login start");

    // Call the service to check the user
    return loginService.velidateUser(user);

}






@PostMapping("/forgotPassword")
public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request){
    String email = request.get("email");
    System.out.println(email);
    return loginService.sendCodeForForgotPassword(email);
}

    @PostMapping("/email_verification")
    public Object verifyEmail(@Valid @RequestBody EmailVerificationRequest emailVerificationCode, BindingResult result) {

        return loginService.verifyEmailForForgotPassword(emailVerificationCode,result);


    }

    @PostMapping(value = "/newPassword")
    public ResponseEntity<?> resetPassword(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> request)
            {
                String password = request.get("NewPassword");

        String email=jwtUtil.extractUsername(token.substring(7).trim());
        return loginService.updatePassword(email,password);



    }


}
