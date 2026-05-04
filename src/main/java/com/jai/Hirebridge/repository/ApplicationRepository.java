package com.jai.Hirebridge.repository;



import com.jai.Hirebridge.model.Application;
import com.jai.Hirebridge.model.Job;
import com.jai.Hirebridge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidate(User candidate);
    List<Application> findByJob(Job job);

    // Checks if a candidate already applied (prevents duplicates)
    boolean existsByCandidateAndJob(User candidate, Job job);
}
