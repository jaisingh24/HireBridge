package com.jai.Hirebridge.service;



import com.jai.Hirebridge.dto.request.JobCreateRequest;
import com.jai.Hirebridge.model.Job;
import com.jai.Hirebridge.model.enums.JobStatus;
import com.jai.Hirebridge.model.enums.JobType;
import com.jai.Hirebridge.repository.JobRepository;
import com.jai.Hirebridge.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public Job createJob(JobCreateRequest request) {
        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .experienceMin(request.getExperienceMin())
                .experienceMax(request.getExperienceMax())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .skillsRequired(request.getSkillsRequired())
                .status(JobStatus.OPEN)
                .expiresAt(request.getExpiresAt())
                .build();

        return jobRepository.save(job);
    }

    public Page<Job> searchJobs(String location, String skills, JobType jobType,
                                Long salaryMin, Integer expMax, Pageable pageable) {

        Specification<Job> spec = Specification.where(JobSpecification.hasLocation(location))
                .and(JobSpecification.hasSkills(skills))
                .and(JobSpecification.hasJobType(jobType))
                .and(JobSpecification.hasSalaryMin(salaryMin))
                .and(JobSpecification.experienceLessEqual(expMax));

        return jobRepository.findAll(spec, pageable);
    }

    public void closeJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(JobStatus.CLOSED);
        jobRepository.save(job);
    }
}