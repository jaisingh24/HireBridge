package com.jai.Hirebridge.service;



import com.jai.Hirebridge.model.CandidateProfile;
import com.jai.Hirebridge.model.User;
import com.jai.Hirebridge.repository.CandidateProfileRepository;
import com.jai.Hirebridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CandidateProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public CandidateProfile getCurrentProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return profileRepository.findByUser(user)
                .orElseGet(() -> profileRepository.save(CandidateProfile.builder().user(user).build()));
    }

    public String uploadResume(MultipartFile file) {
        String url = fileStorageService.uploadResume(file);
        CandidateProfile profile = getCurrentProfile();
        profile.setResumeUrl(url);
        profile.setProfileComplete(true);
        profileRepository.save(profile);
        return url;
    }
}