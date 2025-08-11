package com.driverapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.storage.upload-dir}")
    private String uploadDir;

    @Value("${file.storage.encrypted-dir}")
    private String encryptedDir;

    @Autowired
    private EncryptionService encryptionService;

    /**
     * Stores a file securely with encryption
     * @param file The file to store
     * @param driverId The driver ID for organizing files
     * @param fileType The type of file (profile, license, etc.)
     * @return The file path where the encrypted file is stored
     */
    public String storeFile(MultipartFile file, Long driverId, String fileType) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Create directories if they don't exist
        Path uploadPath = Paths.get(uploadDir, driverId.toString());
        Path encryptedPath = Paths.get(encryptedDir, driverId.toString());
        
        Files.createDirectories(uploadPath);
        Files.createDirectories(encryptedPath);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Store original file temporarily
        Path originalFilePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), originalFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Encrypt the file
        byte[] fileBytes = Files.readAllBytes(originalFilePath);
        byte[] encryptedBytes = encryptionService.encryptFile(fileBytes);

        // Store encrypted file
        String encryptedFilename = "encrypted_" + uniqueFilename;
        Path encryptedFilePath = encryptedPath.resolve(encryptedFilename);
        Files.write(encryptedFilePath, encryptedBytes);

        // Delete original unencrypted file
        Files.deleteIfExists(originalFilePath);

        return encryptedFilePath.toString();
    }

    /**
     * Retrieves and decrypts a file
     * @param filePath The path to the encrypted file
     * @return The decrypted file bytes
     */
    public byte[] retrieveFile(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path is required");
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }

        // Read encrypted file
        byte[] encryptedBytes = Files.readAllBytes(path);
        
        // Decrypt the file
        return encryptionService.decryptFile(encryptedBytes);
    }

    /**
     * Deletes a file from storage
     * @param filePath The path to the file to delete
     */
    public void deleteFile(String filePath) throws IOException {
        if (filePath != null && !filePath.isEmpty()) {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        }
    }

    /**
     * Validates file type and size
     * @param file The file to validate
     * @param maxSize Maximum file size in bytes
     * @param allowedExtensions Array of allowed file extensions
     * @return true if file is valid
     */
    public boolean isValidFile(MultipartFile file, long maxSize, String[] allowedExtensions) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // Check file size
        if (file.getSize() > maxSize) {
            return false;
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        for (String allowedExtension : allowedExtensions) {
            if (allowedExtension.toLowerCase().equals(fileExtension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the file extension from a filename
     * @param filename The filename
     * @return The file extension including the dot
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex);
    }

    /**
     * Creates a thumbnail for image files (placeholder for future implementation)
     * @param file The image file
     * @param driverId The driver ID
     * @return The thumbnail file path
     */
    public String createThumbnail(MultipartFile file, Long driverId) throws IOException {
        // This is a placeholder for thumbnail creation
        // In a real implementation, you would use a library like ImageIO or Thumbnailator
        // to create a smaller version of the image
        
        String thumbnailPath = Paths.get(uploadDir, driverId.toString(), "thumbnails").toString();
        Files.createDirectories(Paths.get(thumbnailPath));
        
        // For now, just return the original file path
        return storeFile(file, driverId, "thumbnail");
    }

    /**
     * Validates if the file is an image
     * @param file The file to validate
     * @return true if the file is an image
     */
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * Gets the file size in a human-readable format
     * @param bytes The file size in bytes
     * @return Human-readable file size
     */
    public String getHumanReadableFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}