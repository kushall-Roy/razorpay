package com.kushal.razorpay.merchant.service;
import com.kushal.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.kushal.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);
}
