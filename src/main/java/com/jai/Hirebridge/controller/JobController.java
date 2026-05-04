package com.jai.Hirebridge.controller;



import com.jai.Hirebridge.dto.request.JobCreateRequest;
import com.jai.Hirebridge.dto.response.JobResponse;
import com.jai.Hirebridge.model.Job;
import com.jai.Hirebridge.model.enums.JobType;
import com.jai.Hirebridge.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<Page<Job>> searchJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) Long salaryMin,
            @RequestParam(required = false) Integer experienceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(jobService.searchJobs(location, skills, jobType, salaryMin, experienceMax, PageRequest.of(page, size, sort)));
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Job> createJob(@Valid @RequestBody JobCreateRequest request) {
        return ResponseEntity.ok(jobService.createJob(request));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Void> closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return ResponseEntity.noContent().build();
    }
}