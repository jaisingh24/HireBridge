package com.jai.Hirebridge.controller;



import com.jai.Hirebridge.model.CandidateProfile;
import com.jai.Hirebridge.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<CandidateProfile> getMyProfile() {
        return ResponseEntity.ok(profileService.getCurrentProfile());
    }

    @PostMapping("/resume")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file) {
        String url = profileService.uploadResume(file);
        return ResponseEntity.ok(url);
    }
}