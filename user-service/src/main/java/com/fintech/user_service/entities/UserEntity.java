package com.fintech.user_service.entities;


import com.fintech.user_service.dto.enums.AccountType;
import com.fintech.user_service.dto.enums.KycStatus;
import com.fintech.user_service.dto.enums.Role;
import com.fintech.user_service.dto.enums.UserStatus;
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
                @Index(name = "idx_status", columnList = "user_status")
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


    @Builder.Default
    @Column(name="user_status", nullable = false, length = 20)
    private UserStatus userStatus=UserStatus.ACTIVE; // ACTIVE, SUSPENDED, CLOSED

    @Builder.Default
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType=AccountType.PERSONAL; // PERSONAL, BUSINESS, MERCHANT

    @Builder.Default
    @Column(name = "kyc_status", length = 20)
    private KycStatus kycStatus=KycStatus.PENDING; // PENDING, VERIFIED, REJECTED


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
