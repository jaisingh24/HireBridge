package com.jai.Hirebridge.dto.request;



import com.jai.Hirebridge.model.enums.JobType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Job Type is required")
    private JobType jobType;

    @Min(0)
    private Integer experienceMin;

    @Min(0)
    private Integer experienceMax;

    @Positive
    private Long salaryMin;

    @Positive
    private Long salaryMax;

    @NotBlank(message = "Skills are required")
    private String skillsRequired;

    private LocalDateTime expiresAt;
}
