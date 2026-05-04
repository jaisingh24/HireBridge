package com.jai.Hirebridge.controller;



import com.jai.Hirebridge.dto.request.ApplicationStatusRequest;
import com.jai.Hirebridge.dto.response.ApplicationResponse;
import com.jai.Hirebridge.model.Application;
import com.jai.Hirebridge.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/jobs/{jobId}/apply")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Application> apply(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.applyToJob(jobId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        return ResponseEntity.ok(applicationService.getCandidateApplications());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody ApplicationStatusRequest request) {
        applicationService.updateStatus(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Void> withdraw(@PathVariable Long id) {
        applicationService.withdrawApplication(id);
        return ResponseEntity.noContent().build();
    }
}