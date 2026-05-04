package com.jai.Hirebridge.dto.request;



import com.jai.Hirebridge.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationStatusRequest {
    @NotNull
    private ApplicationStatus status;

    private String remarks;
}