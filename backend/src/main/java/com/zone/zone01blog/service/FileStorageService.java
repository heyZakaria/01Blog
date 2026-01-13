package com.zone.zone01blog.service;

import com.zone.zone01blog.exception.FileStorageException;
import com.zone.zone01blog.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif");

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mov", "video/quicktime");

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024; // 50MB

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create upload directory", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        validateFile(file);

        // Generate unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        try {
            // Check for invalid characters
            if (newFilename.contains("..")) {
                throw new InvalidFileException("Filename contains invalid path sequence: " + newFilename);
            }

            // Copy file to storage location
            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + newFilename, ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found: " + filename, ex);
        }
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file: " + filename, ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        String contentType = file.getContentType();
        long fileSize = file.getSize();

        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            if (fileSize > MAX_IMAGE_SIZE) {
                throw new InvalidFileException("Image size exceeds maximum allowed size (10MB)");
            }
        } else if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
            if (fileSize > MAX_VIDEO_SIZE) {
                throw new InvalidFileException("Video size exceeds maximum allowed size (50MB)");
            }
        } else {
            throw new InvalidFileException("File type not allowed. Allowed types: JPG, PNG, GIF, MP4, AVI, MOV");
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    public String getMediaType(String contentType) {
        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return "image";
        } else if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
            return "video";
        }
        return "unknown";
    }
}