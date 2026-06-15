package com.kushal.razorpay.merchant.service;
import com.kushal.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.kushal.razorpay.merchant.dto.response.MerchantResponse;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);
}
