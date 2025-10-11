package com.fintech.user_service.services;

import com.fintech.user_service.dto.AuthResponse;
import com.fintech.user_service.dto.EmailVerificationRequest;
import com.fintech.user_service.dto.LoginDto;
import com.fintech.user_service.dto.LoginResponse;
import com.fintech.user_service.entities.UserEntity;
import com.fintech.user_service.repositories.AuthRepo;
import com.fintech.user_service.repositories.UserRepository;
import com.fintech.user_service.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;



@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl  {
    private static final long EXPIRATION_TIME = 5;


    private final UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    private final AuthenticationManager authenticationManager;


    private final UserDetailServiceImpl userDetailService;


    private final  JwtUtil jwtUtil;


    private final  EmailServiceImpl emailService;


    private final RedisTemplate<String, String> redisTemplate;


    String jwtToken;
    UserEntity user;


    public ResponseEntity<?> velidateUser(LoginDto loginUser){
        try {
            // Authenticate the user
            System.out.println("Authenticate....................................");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword())
            );
            System.out.println("ssssssssssssss");



            // Get UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Extract roles from authorities
            String roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            // Update the user status to Active
            // Instead of calling the repository twice, combine the status update and user retrieval
            Optional<UserEntity> optionalUser= userRepository.findByEmail(userDetails.getUsername());

            if(optionalUser.isPresent()){
                user=optionalUser.get();
                user.setLastLoginAt(LocalDateTime.now());// Set the user status to Active
                userRepository.save(user); // Save the updated user entity with Active status
            }

            // Generate JWT with username and roles
            String jwtToken=jwtUtil.generateToken(userDetails.getUsername(), roles);
            return ResponseEntity.ok(new AuthResponse(jwtToken, user));



        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Bad credentials");
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
        }

    }

    public ResponseEntity<?> sendCodeForForgotPassword(String email){
        try {
//            Optional<BanUserEntity> isban=banUserRepo.findByEmail(email);
//            if(isban.isPresent()){
////                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();    main
//                return ResponseEntity
//                        .status(HttpStatus.UNAUTHORIZED)
//                        .body("the Email " + email + " is BAN");
//            }
            Optional<UserEntity> user_in_db=userRepository.findByEmail(email);


                if(user_in_db.isPresent()){
//
            // You can add custom logic if needed

                //save verification code in redis cloud
                Random random = new Random();
                String verificationCode = String.valueOf(1000 + random.nextInt(9000));
                System.out.println(verificationCode);
//                if(emailService.sendEnail(email,"HairCare Ai","Your verification code is "+verificationCode)){
//
//                    try {//store verification code in redis cloud
//                        redisTemplate.opsForValue().set(email, verificationCode, EXPIRATION_TIME, TimeUnit.MINUTES);
//                        return ResponseEntity
//                                .status(HttpStatus.CREATED)
//                                .body( email +"  verification code is  "+  verificationCode);
//                    } catch (Exception e) {
//                        System.out.println(e);
//                        return ResponseEntity.internalServerError().build();
//                    }
//                }
                return ResponseEntity.internalServerError().build();
            } else {
                // Handle the case where the user could not be added
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }


    }

    public ResponseEntity<?> verifyEmailForForgotPassword(@Valid EmailVerificationRequest email_verification, BindingResult result) {
        // Check for validation errors
        try {
            if (result.hasErrors()) {
                // Return validation errors (you can customize this)
                return (ResponseEntity<?>) result.getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
            }

            Optional<UserEntity> user=userRepository.findByEmail(email_verification.getEmail());
            if(user.isEmpty()){
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(email_verification.getEmail()+"    user not found");
            }
            //verify using code
            String storedCode = redisTemplate.opsForValue().get(email_verification.getEmail());
            if (storedCode != null && storedCode.equals(email_verification.getVerificationCode())) {
                // If verified, remove the code from Redis
                redisTemplate.delete(email_verification.getEmail());
                UserEntity dbuser=user.get();
//

                // Generate JWT with username and roles
//                jwtToken= jwtUtil.generateToken(dbuser.getEmail(), dbuser.getRole().toString());
                Map<String, String> response = new HashMap<>();
                response.put("token", jwtToken);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("verification code is incorrect");



        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occured");
        }
    }

    public ResponseEntity<?> updatePassword(String email,String password){
       try {
           Optional<UserEntity> optionalUser=userRepository.findByEmail(email);
           if(optionalUser.isPresent()){
               UserEntity user=optionalUser.get();
//               user.setPassword(passwordEncoder.encode(password));
               userRepository.save(user);
               return ResponseEntity.ok(user);
           }
           return ResponseEntity.internalServerError().build();

       } catch (Exception e) {
           return ResponseEntity.internalServerError().build();
       }

    }
}


