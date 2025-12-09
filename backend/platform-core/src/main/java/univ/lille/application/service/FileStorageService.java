package univ.lille.application.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String saveUserAvatar(long userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        String rawName = file.getOriginalFilename();
        if (rawName == null || rawName.isBlank()) {
            rawName = "file";
        }
        String originalFilename = StringUtils.cleanPath(rawName);
        String ext = "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot >= 0 && dot < originalFilename.length() - 1) {
            ext = originalFilename.substring(dot);
        }

        String newFilename = UUID.randomUUID().toString() + ext;
        Path userDir = Path.of(uploadDir, "avatars", String.valueOf(userId));
        Files.createDirectories(userDir);
        Path target = userDir.resolve(newFilename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/static/avatars/" + userId + "/" + newFilename;
    }
}
