package com.fintech.user_service.controllers;

import com.fintech.user_service.dto.ApiResponse;
import com.fintech.user_service.dto.UserDTO;
import com.fintech.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<Page<UserDTO>> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<UserDTO> users = userService.getAllUsers(PageRequest.of(page, size));
        return ApiResponse.success("Users fetched successfully", users);
    }

}
