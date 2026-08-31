package com.insurance.policy.dto;

public final class AdminDtos {
    private AdminDtos() {}

    public record DashboardResponse(long totalCustomers, long totalProducts, long activePolicies,
                                    long pendingClaims, long approvedClaims, long rejectedClaims) {}
}