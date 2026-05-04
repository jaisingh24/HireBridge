package com.jai.Hirebridge.repository;



import com.jai.Hirebridge.model.Job;
import com.jai.Hirebridge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    // Find jobs posted by a specific recruiter
    List<Job> findByRecruiter(User recruiter);

    // Find all jobs for a specific company
    List<Job> findByCompanyId(Long companyId);
}