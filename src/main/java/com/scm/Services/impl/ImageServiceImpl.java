package com.scm.Services.impl;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.Services.ImageService;
import com.scm.helpers.AppConstants;

@Service
public class ImageServiceImpl implements ImageService {

    private Cloudinary cloudinary;

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile contactImage, String filename) {

        // code likhna jo image ko server pe upload kr rha hoga

        //////// ye bahaqr ka h

        // ✅ Safety check
        if (contactImage == null || contactImage.isEmpty()) {
            return null;
        }

        //// yaha tak hatana ho to

        try {
            byte[] data = new byte[contactImage.getInputStream().available()];
            contactImage.getInputStream().read(data);
            cloudinary.uploader().upload(data, ObjectUtils.asMap("public_id", filename));
            return this.getURLFromPublicId(filename);

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }

        // return kr rha hoga : URL

    }

    @Override
    public String getURLFromPublicId(String publicId) {

        return cloudinary.url()
                .transformation(
                        new Transformation<>().width(AppConstants.CONTACT_IMAGE_WIDTH)
                                .height(AppConstants.CONTACT_IMAGE_HEIGHT).crop(AppConstants.CONTACT_IMAGE_CROP)
                                .gravity("face"))
                .generate(publicId);
    }

}
