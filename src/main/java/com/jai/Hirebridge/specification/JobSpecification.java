package com.jai.Hirebridge.specification;

import com.jai.Hirebridge.model.Job;
import com.jai.Hirebridge.model.enums.JobStatus;
import com.jai.Hirebridge.model.enums.JobType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    // Must be PUBLIC and STATIC so JobService can call it without an instance
    public static Specification<Job> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isEmpty()) return null;
            return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }

    public static Specification<Job> hasSkills(String skills) {
        return (root, query, cb) -> {
            if (skills == null || skills.isEmpty()) return null;
            return cb.like(cb.lower(root.get("skillsRequired")), "%" + skills.toLowerCase() + "%");
        };
    }

    public static Specification<Job> hasJobType(JobType jobType) {
        return (root, query, cb) -> {
            if (jobType == null) return null;
            return cb.equal(root.get("jobType"), jobType);
        };
    }

    public static Specification<Job> hasSalaryMin(Long salaryMin) {
        return (root, query, cb) -> {
            if (salaryMin == null) return null;
            return cb.greaterThanOrEqualTo(root.get("salaryMin"), salaryMin);
        };
    }

    public static Specification<Job> experienceLessEqual(Integer expMax) {
        return (root, query, cb) -> {
            if (expMax == null) return null;
            return cb.lessThanOrEqualTo(root.get("experienceMax"), expMax);
        };
    }
}