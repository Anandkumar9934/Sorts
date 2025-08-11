package com.driverapi.controller;

import com.driverapi.dto.DriverRequest;
import com.driverapi.dto.DriverResponse;
import com.driverapi.entity.DriverStatus;
import com.driverapi.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    @Autowired
    private DriverService driverService;

    /**
     * Create a new driver
     */
    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(@Valid @RequestBody DriverRequest driverRequest) {
        try {
            DriverResponse response = driverService.createDriver(driverRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get driver by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable Long id) {
        try {
            DriverResponse response = driverService.getDriverById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all drivers with pagination
     */
    @GetMapping
    public ResponseEntity<Page<DriverResponse>> getAllDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DriverResponse> drivers = driverService.getAllDrivers(pageable);
        
        return ResponseEntity.ok(drivers);
    }

    /**
     * Update driver
     */
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateDriver(
            @PathVariable Long id, 
            @Valid @RequestBody DriverRequest driverRequest) {
        try {
            DriverResponse response = driverService.updateDriver(id, driverRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete driver
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        try {
            driverService.deleteDriver(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Upload profile photo
     */
    @PostMapping("/{id}/profile-photo")
    public ResponseEntity<String> uploadProfilePhoto(
            @PathVariable Long id, 
            @RequestParam("file") MultipartFile file) {
        try {
            String filePath = driverService.uploadProfilePhoto(id, file);
            return ResponseEntity.ok("Profile photo uploaded successfully: " + filePath);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    /**
     * Upload license photo
     */
    @PostMapping("/{id}/license-photo")
    public ResponseEntity<String> uploadLicensePhoto(
            @PathVariable Long id, 
            @RequestParam("file") MultipartFile file) {
        try {
            String filePath = driverService.uploadLicensePhoto(id, file);
            return ResponseEntity.ok("License photo uploaded successfully: " + filePath);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        }
    }

    /**
     * Get profile photo
     */
    @GetMapping("/{id}/profile-photo")
    public ResponseEntity<ByteArrayResource> getProfilePhoto(@PathVariable Long id) {
        try {
            byte[] photoData = driverService.getProfilePhoto(id);
            ByteArrayResource resource = new ByteArrayResource(photoData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"profile-photo.jpg\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get license photo
     */
    @GetMapping("/{id}/license-photo")
    public ResponseEntity<ByteArrayResource> getLicensePhoto(@PathVariable Long id) {
        try {
            byte[] photoData = driverService.getLicensePhoto(id);
            ByteArrayResource resource = new ByteArrayResource(photoData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"license-photo.jpg\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search drivers by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<DriverResponse>> searchDriversByName(@RequestParam String name) {
        try {
            List<DriverResponse> drivers = driverService.searchDriversByName(name);
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get drivers by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DriverResponse>> getDriversByStatus(@PathVariable DriverStatus status) {
        try {
            List<DriverResponse> drivers = driverService.getDriversByStatus(status);
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get drivers with expired licenses
     */
    @GetMapping("/expired-licenses")
    public ResponseEntity<List<DriverResponse>> getDriversWithExpiredLicenses() {
        try {
            List<DriverResponse> drivers = driverService.getDriversWithExpiredLicenses();
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update driver status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverResponse> updateDriverStatus(
            @PathVariable Long id, 
            @RequestParam DriverStatus status) {
        try {
            DriverResponse response = driverService.updateDriverStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verify driver
     */
    @PatchMapping("/{id}/verify")
    public ResponseEntity<DriverResponse> verifyDriver(@PathVariable Long id) {
        try {
            DriverResponse response = driverService.verifyDriver(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Driver Management API is running!");
    }
}