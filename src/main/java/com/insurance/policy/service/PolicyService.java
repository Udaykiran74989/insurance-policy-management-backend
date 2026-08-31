package com.insurance.policy.service;

import com.insurance.policy.dto.PolicyDtos;

import java.util.List;

public interface PolicyService {
    List<PolicyDtos.PolicyResponse> findMine(String email);
    PolicyDtos.PolicyResponse findMineById(String email, Long id);
    PolicyDtos.PolicyResponse purchase(String email, PolicyDtos.PurchaseRequest request);
    PolicyDtos.PolicyResponse cancel(String email, Long id);
    List<PolicyDtos.PolicyResponse> findAll();
    PolicyDtos.PolicyResponse updateStatus(Long id, PolicyDtos.StatusRequest request);
}