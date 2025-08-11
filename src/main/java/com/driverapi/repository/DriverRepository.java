package com.driverapi.repository;

import com.driverapi.entity.Driver;
import com.driverapi.entity.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    /**
     * Find driver by email
     */
    Optional<Driver> findByEmail(String email);

    /**
     * Find driver by driving license number
     */
    Optional<Driver> findByDrivingLicenseNumber(String drivingLicenseNumber);

    /**
     * Find driver by phone number
     */
    Optional<Driver> findByPhoneNumber(String phoneNumber);

    /**
     * Find drivers by status
     */
    List<Driver> findByStatus(DriverStatus status);

    /**
     * Find drivers by status with pagination
     */
    Page<Driver> findByStatus(DriverStatus status, Pageable pageable);

    /**
     * Find drivers by verification status
     */
    List<Driver> findByIsVerified(Boolean isVerified);

    /**
     * Find drivers by verification status with pagination
     */
    Page<Driver> findByIsVerified(Boolean isVerified, Pageable pageable);

    /**
     * Find drivers by city
     */
    List<Driver> findByCity(String city);

    /**
     * Find drivers by state
     */
    List<Driver> findByState(String state);

    /**
     * Find drivers by country
     */
    List<Driver> findByCountry(String country);

    /**
     * Find drivers with expired licenses
     */
    @Query("SELECT d FROM Driver d WHERE d.licenseExpiryDate < :currentDate")
    List<Driver> findDriversWithExpiredLicenses(@Param("currentDate") LocalDate currentDate);

    /**
     * Find drivers with licenses expiring soon (within specified days)
     */
    @Query("SELECT d FROM Driver d WHERE d.licenseExpiryDate BETWEEN :startDate AND :endDate")
    List<Driver> findDriversWithLicensesExpiringSoon(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    /**
     * Find drivers by name (first name or last name contains the search term)
     */
    @Query("SELECT d FROM Driver d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Driver> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find drivers by name with pagination
     */
    @Query("SELECT d FROM Driver d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Driver> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Find drivers by multiple criteria
     */
    @Query("SELECT d FROM Driver d WHERE " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:isVerified IS NULL OR d.isVerified = :isVerified) AND " +
           "(:city IS NULL OR LOWER(d.city) = LOWER(:city)) AND " +
           "(:state IS NULL OR LOWER(d.state) = LOWER(:state))")
    Page<Driver> findByCriteria(
            @Param("status") DriverStatus status,
            @Param("isVerified") Boolean isVerified,
            @Param("city") String city,
            @Param("state") String state,
            Pageable pageable);

    /**
     * Count drivers by status
     */
    long countByStatus(DriverStatus status);

    /**
     * Count drivers by verification status
     */
    long countByIsVerified(Boolean isVerified);

    /**
     * Count drivers with expired licenses
     */
    @Query("SELECT COUNT(d) FROM Driver d WHERE d.licenseExpiryDate < :currentDate")
    long countDriversWithExpiredLicenses(@Param("currentDate") LocalDate currentDate);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if driving license number exists
     */
    boolean existsByDrivingLicenseNumber(String drivingLicenseNumber);

    /**
     * Check if phone number exists
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Find drivers created between dates
     */
    @Query("SELECT d FROM Driver d WHERE d.createdAt BETWEEN :startDate AND :endDate")
    List<Driver> findByCreatedAtBetween(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    /**
     * Find drivers by age range
     */
    @Query("SELECT d FROM Driver d WHERE d.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Driver> findByDateOfBirthBetween(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
}