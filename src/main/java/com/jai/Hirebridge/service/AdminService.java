package com.jai.Hirebridge.service;



import com.jai.Hirebridge.model.Company;
import com.jai.Hirebridge.model.User;
import com.jai.Hirebridge.repository.CompanyRepository;
import com.jai.Hirebridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public void approveCompany(Long id) {
        Company company = companyRepository.findById(id).orElseThrow();
        company.setApproved(true);
        companyRepository.save(company);
    }

    public void banUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActive(false);
        userRepository.save(user);
    }
}