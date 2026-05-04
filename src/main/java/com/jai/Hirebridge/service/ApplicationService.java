package com.jai.Hirebridge.service;



import com.jai.Hirebridge.dto.request.ApplicationStatusRequest;
import com.jai.Hirebridge.dto.response.ApplicationResponse;
import com.jai.Hirebridge.model.Application;
import com.jai.Hirebridge.model.Job;
import com.jai.Hirebridge.model.User;
import com.jai.Hirebridge.model.enums.ApplicationStatus;
import com.jai.Hirebridge.repository.ApplicationRepository;
import com.jai.Hirebridge.repository.JobRepository;
import com.jai.Hirebridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Transactional
    public Application applyToJob(Long jobId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User candidate = userRepository.findByEmail(email).orElseThrow();
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByCandidateAndJob(candidate, job)) {
            throw new RuntimeException("You have already applied for this position");
        }

        Application application = Application.builder()
                .candidate(candidate)
                .job(job)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();

        return applicationRepository.save(application);
    }

    public List<ApplicationResponse> getCandidateApplications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User candidate = userRepository.findByEmail(email).orElseThrow();

        return applicationRepository.findByCandidate(candidate).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStatus(Long appId, ApplicationStatusRequest request) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setStatus(request.getStatus());
        app.setRecruiterNotes(request.getRemarks());
        app.setUpdatedAt(LocalDateTime.now());

        applicationRepository.save(app);
    }

    @Transactional
    public void withdrawApplication(Long appId) {
        Application app = applicationRepository.findById(appId).orElseThrow();
        if (app.getStatus() != ApplicationStatus.APPLIED) {
            throw new RuntimeException("Can only withdraw during the initial APPLIED stage");
        }
        app.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(app);
    }

    private ApplicationResponse mapToResponse(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .companyName(app.getJob().getCompany().getName())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .recruiterNotes(app.getRecruiterNotes())
                .build();
    }
}