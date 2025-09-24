package com.fintech.user_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long UserId;

    @Column
    @NonNull
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String first_name;


    @Column
    @NonNull
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String last_name;



    @Column(unique = true)
    @NonNull
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "Email should be valid")
    private String email;


    @Column
    @NonNull
    @NotBlank(message = "Password is mandatory")
    @Size(min = 2, max = 100, message = "hashed password should be between 2 and 100 characters")
    private String password;

    @Column
//    @NonNull
    private String gender;

//    @NonNull
    @Column
    private Integer age;

//    @Column
//    @Enumerated(EnumType.STRING)
//    private IsVerified verify=IsVerified.unverified;
//    public enum IsVerified {
//        verified,
//        unverified
//    }
    @Column
    private Boolean verify=false;






    @Column
    private String country;

    @Column
    @Enumerated(EnumType.STRING)
    @NonNull
    private Role role= Role.USER;

    // Enum to represent user roles
    public enum Role {
        USER,
        ADMIN,
        BAN
    }


    @Column
    @Enumerated(EnumType.STRING)
    @NonNull
    private UserType usertype= UserType.Regular;

    // Enum to represent user roles
    public enum UserType {
        Regular,
        Premium
    }





    @Column
    @Enumerated(EnumType.STRING)
    @NonNull
    private Status status= Status.InActive;

    // Enum to represent user roles
    public enum Status {
        Active,
        InActive
    }

    // New fields for timestamps
    @Column(nullable = true)
    private LocalDateTime lastLogin;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = true)
    private String imagePath;









}
