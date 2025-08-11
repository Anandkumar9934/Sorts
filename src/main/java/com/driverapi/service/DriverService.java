package com.driverapi.service;

import com.driverapi.dto.DriverRequest;
import com.driverapi.dto.DriverResponse;
import com.driverapi.entity.Driver;
import com.driverapi.entity.DriverStatus;
import com.driverapi.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EncryptionService encryptionService;

    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Create a new driver
     */
    public DriverResponse createDriver(DriverRequest driverRequest) {
        // Validate unique constraints
        validateUniqueConstraints(driverRequest);

        // Create driver entity
        Driver driver = new Driver();
        updateDriverFromRequest(driver, driverRequest);

        // Encrypt sensitive data
        encryptSensitiveData(driver);

        // Save driver
        Driver savedDriver = driverRepository.save(driver);

        return new DriverResponse(savedDriver);
    }

    /**
     * Get driver by ID
     */
    public DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        // Decrypt sensitive data
        decryptSensitiveData(driver);

        return new DriverResponse(driver);
    }

    /**
     * Get all drivers with pagination
     */
    public Page<DriverResponse> getAllDrivers(Pageable pageable) {
        Page<Driver> drivers = driverRepository.findAll(pageable);
        return drivers.map(driver -> {
            decryptSensitiveData(driver);
            return new DriverResponse(driver);
        });
    }

    /**
     * Update driver
     */
    public DriverResponse updateDriver(Long id, DriverRequest driverRequest) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        // Validate unique constraints for update
        validateUniqueConstraintsForUpdate(driverRequest, id);

        // Update driver data
        updateDriverFromRequest(driver, driverRequest);

        // Encrypt sensitive data
        encryptSensitiveData(driver);

        // Save updated driver
        Driver updatedDriver = driverRepository.save(driver);

        return new DriverResponse(updatedDriver);
    }

    /**
     * Delete driver
     */
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        // Delete associated files
        deleteDriverFiles(driver);

        // Delete driver
        driverRepository.delete(driver);
    }

    /**
     * Upload profile photo
     */
    public String uploadProfilePhoto(Long driverId, MultipartFile file) throws IOException {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Validate file
        if (!fileStorageService.isValidFile(file, MAX_FILE_SIZE, ALLOWED_IMAGE_EXTENSIONS)) {
            throw new IllegalArgumentException("Invalid file format or size");
        }

        // Store file
        String filePath = fileStorageService.storeFile(file, driverId, "profile");

        // Update driver with file path
        driver.setProfilePhotoPath(filePath);
        driverRepository.save(driver);

        return filePath;
    }

    /**
     * Upload license photo
     */
    public String uploadLicensePhoto(Long driverId, MultipartFile file) throws IOException {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Validate file
        if (!fileStorageService.isValidFile(file, MAX_FILE_SIZE, ALLOWED_IMAGE_EXTENSIONS)) {
            throw new IllegalArgumentException("Invalid file format or size");
        }

        // Store file
        String filePath = fileStorageService.storeFile(file, driverId, "license");

        // Update driver with file path
        driver.setLicensePhotoPath(filePath);
        driverRepository.save(driver);

        return filePath;
    }

    /**
     * Get profile photo
     */
    public byte[] getProfilePhoto(Long driverId) throws IOException {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        if (driver.getProfilePhotoPath() == null) {
            throw new RuntimeException("Profile photo not found");
        }

        return fileStorageService.retrieveFile(driver.getProfilePhotoPath());
    }

    /**
     * Get license photo
     */
    public byte[] getLicensePhoto(Long driverId) throws IOException {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        if (driver.getLicensePhotoPath() == null) {
            throw new RuntimeException("License photo not found");
        }

        return fileStorageService.retrieveFile(driver.getLicensePhotoPath());
    }

    /**
     * Search drivers by name
     */
    public List<DriverResponse> searchDriversByName(String name) {
        List<Driver> drivers = driverRepository.findByNameContainingIgnoreCase(name);
        return drivers.stream()
                .map(driver -> {
                    decryptSensitiveData(driver);
                    return new DriverResponse(driver);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get drivers by status
     */
    public List<DriverResponse> getDriversByStatus(DriverStatus status) {
        List<Driver> drivers = driverRepository.findByStatus(status);
        return drivers.stream()
                .map(driver -> {
                    decryptSensitiveData(driver);
                    return new DriverResponse(driver);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get drivers with expired licenses
     */
    public List<DriverResponse> getDriversWithExpiredLicenses() {
        List<Driver> drivers = driverRepository.findDriversWithExpiredLicenses(LocalDate.now());
        return drivers.stream()
                .map(driver -> {
                    decryptSensitiveData(driver);
                    return new DriverResponse(driver);
                })
                .collect(Collectors.toList());
    }

    /**
     * Update driver status
     */
    public DriverResponse updateDriverStatus(Long id, DriverStatus status) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        driver.setStatus(status);
        Driver updatedDriver = driverRepository.save(driver);

        decryptSensitiveData(updatedDriver);
        return new DriverResponse(updatedDriver);
    }

    /**
     * Verify driver
     */
    public DriverResponse verifyDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        driver.setIsVerified(true);
        driver.setStatus(DriverStatus.ACTIVE);
        Driver updatedDriver = driverRepository.save(driver);

        decryptSensitiveData(updatedDriver);
        return new DriverResponse(updatedDriver);
    }

    // Private helper methods

    private void validateUniqueConstraints(DriverRequest request) {
        if (driverRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        if (driverRepository.existsByDrivingLicenseNumber(request.getDrivingLicenseNumber())) {
            throw new RuntimeException("Driving license number already exists: " + request.getDrivingLicenseNumber());
        }
        if (driverRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + request.getPhoneNumber());
        }
    }

    private void validateUniqueConstraintsForUpdate(DriverRequest request, Long driverId) {
        Optional<Driver> existingDriver = driverRepository.findById(driverId);
        if (existingDriver.isEmpty()) {
            throw new RuntimeException("Driver not found with id: " + driverId);
        }

        Driver currentDriver = existingDriver.get();

        // Check email uniqueness
        if (!currentDriver.getEmail().equals(request.getEmail()) && 
            driverRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Check driving license number uniqueness
        if (!currentDriver.getDrivingLicenseNumber().equals(request.getDrivingLicenseNumber()) && 
            driverRepository.existsByDrivingLicenseNumber(request.getDrivingLicenseNumber())) {
            throw new RuntimeException("Driving license number already exists: " + request.getDrivingLicenseNumber());
        }

        // Check phone number uniqueness
        if (!currentDriver.getPhoneNumber().equals(request.getPhoneNumber()) && 
            driverRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists: " + request.getPhoneNumber());
        }
    }

    private void updateDriverFromRequest(Driver driver, DriverRequest request) {
        driver.setFirstName(request.getFirstName());
        driver.setLastName(request.getLastName());
        driver.setEmail(request.getEmail());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setDateOfBirth(request.getDateOfBirth());
        driver.setDrivingLicenseNumber(request.getDrivingLicenseNumber());
        driver.setLicenseExpiryDate(request.getLicenseExpiryDate());
        driver.setLicenseIssuingAuthority(request.getLicenseIssuingAuthority());
        driver.setAddress(request.getAddress());
        driver.setCity(request.getCity());
        driver.setState(request.getState());
        driver.setCountry(request.getCountry());
        driver.setPostalCode(request.getPostalCode());
    }

    private void encryptSensitiveData(Driver driver) {
        // Encrypt sensitive fields
        driver.setPhoneNumber(encryptionService.encrypt(driver.getPhoneNumber()));
        driver.setDrivingLicenseNumber(encryptionService.encrypt(driver.getDrivingLicenseNumber()));
        driver.setAddress(encryptionService.encrypt(driver.getAddress()));
    }

    private void decryptSensitiveData(Driver driver) {
        // Decrypt sensitive fields
        driver.setPhoneNumber(encryptionService.decrypt(driver.getPhoneNumber()));
        driver.setDrivingLicenseNumber(encryptionService.decrypt(driver.getDrivingLicenseNumber()));
        driver.setAddress(encryptionService.decrypt(driver.getAddress()));
    }

    private void deleteDriverFiles(Driver driver) {
        try {
            if (driver.getProfilePhotoPath() != null) {
                fileStorageService.deleteFile(driver.getProfilePhotoPath());
            }
            if (driver.getLicensePhotoPath() != null) {
                fileStorageService.deleteFile(driver.getLicensePhotoPath());
            }
        } catch (IOException e) {
            // Log error but don't fail the deletion
            System.err.println("Error deleting driver files: " + e.getMessage());
        }
    }
}