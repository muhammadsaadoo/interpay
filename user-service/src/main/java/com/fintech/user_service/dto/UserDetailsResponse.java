package com.fintech.user_service.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailsResponse {
    private UserDTO user;
    private UserProfileDTO profile;
    private List<KycDocumentDTO> kycDocuments;
}

