package com.ayman.distributed.authy.features.identity.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfilePictureService {
    String storeProfilePicture(MultipartFile file) throws IOException;
}
