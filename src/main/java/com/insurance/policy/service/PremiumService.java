package com.insurance.policy.service;

import com.insurance.policy.dto.PremiumDtos;

public interface PremiumService {
    PremiumDtos.PremiumResponse calculate(PremiumDtos.PremiumRequest request);
}