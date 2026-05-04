package com.jai.Hirebridge.dto.response;


import com.jai.Hirebridge.model.enums.JobStatus;
import com.jai.Hirebridge.model.enums.JobType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String companyName;
    private String location;
    private JobType jobType;
    private Long salaryMin;
    private Long salaryMax;
    private String skillsRequired;
    private JobStatus status;
    private LocalDateTime createdAt;
}