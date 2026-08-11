package com.kushal.razorpay.merchant.service;
import com.kushal.razorpay.merchant.dto.request.LoginRequest;
import com.kushal.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.kushal.razorpay.merchant.dto.response.LoginResponse;
import com.kushal.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);
    LoginResponse login(LoginRequest request);
}
