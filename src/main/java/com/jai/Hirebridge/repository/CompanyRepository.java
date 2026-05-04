package com.jai.Hirebridge.repository;



import com.jai.Hirebridge.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Used for Admin Dashboard
    List<Company> findByIsApprovedFalse();
}