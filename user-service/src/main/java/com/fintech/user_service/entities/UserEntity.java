package com.fintech.user_service.entities;


import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_email", columnList = "email"),
                @Index(name = "idx_phone", columnList = "phone"),
                @Index(name = "idx_status", columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;


    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String password;

    @Builder.Default
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role=Role.USER;

    // Enum to represent user roles
    public enum Role {
        USER,
        ADMIN,

    }

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, SUSPENDED, CLOSED

    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType; // PERSONAL, BUSINESS, MERCHANT

    @Column(name = "kyc_status", length = 20)
    private String kycStatus = "PENDING"; // PENDING, VERIFIED, REJECTED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt ;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // Relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfileEntity profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KycDocumentEntity> kycDocuments;

    // --- Timestamp Hooks ---
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
