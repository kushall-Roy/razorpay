package com.kushal.razorpay.merchant.dto.request;
import com.kushal.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
