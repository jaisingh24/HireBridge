package com.jai.Hirebridge.service;



import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final Cloudinary cloudinary;

    public String uploadResume(MultipartFile file) {
        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Only PDF resumes are allowed");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "hirebridge/resumes/"));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload resume to Cloudinary");
        }
    }
}