package com.jai.Hirebridge.controller;



import com.jai.Hirebridge.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/companies/{id}/approve")
    public ResponseEntity<Void> approveCompany(@PathVariable Long id) {
        adminService.approveCompany(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long id) {
        adminService.banUser(id);
        return ResponseEntity.ok().build();
    }
}