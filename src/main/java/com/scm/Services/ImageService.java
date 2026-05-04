package com.scm.Services;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String uploadImage(MultipartFile contactImage, String filename);

    String getURLFromPublicId(String publicId);

}