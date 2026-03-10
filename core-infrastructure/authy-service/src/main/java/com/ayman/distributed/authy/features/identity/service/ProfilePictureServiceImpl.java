package com.ayman.distributed.authy.features.identity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfilePictureServiceImpl implements ProfilePictureService {

    @Override
    public String storeProfilePicture(MultipartFile file) throws IOException {
        // Save the file
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "http://localhost:8081/uploads/" + fileName;
    }
}
