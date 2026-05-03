package calixy.controller;

import calixy.domain.entity.User;
import calixy.model.dto.request.SetupProfileRequest;
import calixy.model.dto.request.UpdateUserRequest;
import calixy.model.dto.response.MessageResponse;
import calixy.model.dto.response.UserProfileResponse;
import calixy.model.enums.UserRole;
import calixy.model.enums.UserStatus;
import calixy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getMyProfile(user));
    }

    @PostMapping("/me/setup-profile")
    public ResponseEntity<UserProfileResponse> setupProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SetupProfileRequest request) {
        return ResponseEntity.ok(userService.setupProfile(user, request));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateProfile(user, request));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<Page<UserProfileResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PostMapping("/me/profile-image")
    public ResponseEntity<UserProfileResponse> uploadProfileImage(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfileImage(user, file));
    }

    @DeleteMapping("/me/profile-image")
    public ResponseEntity<Void> deleteProfileImage(@AuthenticationPrincipal User user) {
        userService.deleteProfileImage(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/admin/by-email")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<UserProfileResponse> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<UserProfileResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status) {
        return ResponseEntity.ok(userService.updateUserStatus(id, status));
    }

    @PatchMapping("/admin/{id}/role")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<UserProfileResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }
}