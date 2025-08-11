package com.driverapi.entity;

public enum DriverStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended"),
    EXPIRED("Expired"),
    PENDING_VERIFICATION("Pending Verification"),
    REJECTED("Rejected");

    private final String displayName;

    DriverStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}