package com.jai.Hirebridge.dto.response;



import com.jai.Hirebridge.model.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String candidateName;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private String recruiterNotes;
}