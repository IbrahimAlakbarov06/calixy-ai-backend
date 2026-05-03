package calixy.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            String publicId = "calixy/profiles/" + UUID.randomUUID();

            Map result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "overwrite", true,
                            "resource_type", "image",
                            "transformation", new com.cloudinary.Transformation()
                                    .width(300).height(300).crop("fill").gravity("face")
                    )
            );

            return (String) result.get("secure_url");

        } catch (IOException e) {
            log.error("Image upload failed: {}", e.getMessage());
            throw new RuntimeException("Image upload failed");
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || !imageUrl.contains("cloudinary")) return;

            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        } catch (IOException e) {
            log.error("Image delete failed: {}", e.getMessage());
        }
    }

    private String extractPublicId(String url) {
        String[] parts = url.split("/upload/");
        if (parts.length < 2) return "";
        String path = parts[1];
        if (path.startsWith("v") && path.contains("/")) {
            path = path.substring(path.indexOf("/") + 1);
        }
        if (path.contains(".")) {
            path = path.substring(0, path.lastIndexOf("."));
        }
        return path;
    }
}