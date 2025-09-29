package com.fintech.user_service.controllers;


import com.fintech.user_service.dto.EmailVerificationRequest;
import com.fintech.user_service.entities.UserEntity;
import com.fintech.user_service.services.SignupServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/signup")
public class SignupController {
    @Autowired
    private SignupServiceImpl signupService;

    @PostMapping("/email_verification")
    public Object verifyEmail(@Valid @RequestBody EmailVerificationRequest email_verificationCode, BindingResult result) {

          return signupService.verifyEmail(email_verificationCode,result);


    }
    @PostMapping
    public ResponseEntity<?> register(@RequestBody UserEntity user){
        System.out.println(user.toString());
        return signupService.insertUser(user);

    }

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> addProduct(
//            @RequestPart("user") UserEntity user,
//            @RequestPart("imageFile") MultipartFile imageFile
//    ) {
//
//        try {
//            UserEntity newUser = signupService.insertUser(user, imageFile);
//            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
//        } catch (IOException e) {
//            return ResponseEntity.internalServerError().build(); // Added .build()
//        }
//    }

    ////////////////////////////DUMMY?????????????????????????????????????



}
