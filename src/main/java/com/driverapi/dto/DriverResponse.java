package com.driverapi.dto;

import com.driverapi.entity.Driver;
import com.driverapi.entity.DriverStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    private Integer age;
    private String drivingLicenseNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate licenseExpiryDate;
    
    private String licenseIssuingAuthority;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private DriverStatus status;
    private Boolean isVerified;
    private Boolean isLicenseExpired;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructors
    public DriverResponse() {}

    public DriverResponse(Driver driver) {
        this.id = driver.getId();
        this.firstName = driver.getFirstName();
        this.lastName = driver.getLastName();
        this.fullName = driver.getFullName();
        this.email = driver.getEmail();
        this.phoneNumber = driver.getPhoneNumber();
        this.dateOfBirth = driver.getDateOfBirth();
        this.age = calculateAge(driver.getDateOfBirth());
        this.drivingLicenseNumber = driver.getDrivingLicenseNumber();
        this.licenseExpiryDate = driver.getLicenseExpiryDate();
        this.licenseIssuingAuthority = driver.getLicenseIssuingAuthority();
        this.address = driver.getAddress();
        this.city = driver.getCity();
        this.state = driver.getState();
        this.country = driver.getCountry();
        this.postalCode = driver.getPostalCode();
        this.status = driver.getStatus();
        this.isVerified = driver.getIsVerified();
        this.isLicenseExpired = driver.isLicenseExpired();
        this.createdAt = driver.getCreatedAt();
        this.updatedAt = driver.getUpdatedAt();
    }

    // Helper method to calculate age
    private Integer calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return null;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public String getLicenseIssuingAuthority() {
        return licenseIssuingAuthority;
    }

    public void setLicenseIssuingAuthority(String licenseIssuingAuthority) {
        this.licenseIssuingAuthority = licenseIssuingAuthority;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Boolean getIsLicenseExpired() {
        return isLicenseExpired;
    }

    public void setIsLicenseExpired(Boolean isLicenseExpired) {
        this.isLicenseExpired = isLicenseExpired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "DriverResponse{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", age=" + age +
                ", drivingLicenseNumber='" + drivingLicenseNumber + '\'' +
                ", licenseExpiryDate=" + licenseExpiryDate +
                ", licenseIssuingAuthority='" + licenseIssuingAuthority + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", status=" + status +
                ", isVerified=" + isVerified +
                ", isLicenseExpired=" + isLicenseExpired +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}