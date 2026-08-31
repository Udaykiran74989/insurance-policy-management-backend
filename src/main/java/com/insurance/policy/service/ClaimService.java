package com.insurance.policy.service;

import com.insurance.policy.dto.ClaimDtos;

import java.util.List;

public interface ClaimService {
    ClaimDtos.ClaimResponse submit(String email, ClaimDtos.ClaimRequest request);
    List<ClaimDtos.ClaimResponse> findMine(String email);
    ClaimDtos.ClaimResponse findMineById(String email, Long id);
    List<ClaimDtos.ClaimResponse> findAll();
    ClaimDtos.ClaimResponse updateStatus(Long id, ClaimDtos.StatusRequest request);
}