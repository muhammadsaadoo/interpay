package com.fintech.user_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/login")
public class AuthController {


   @GetMapping("/test")
    public ResponseEntity<?> testRequest(){
       System.out.println("request accepted...........");
       return ResponseEntity.ok("success");

   }

    }

