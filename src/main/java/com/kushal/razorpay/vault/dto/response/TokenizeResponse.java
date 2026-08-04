package com.kushal.razorpay.vault.dto.response;
import com.kushal.razorpay.common.enums.CardBrand;

public record TokenizeResponse(

        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear

) {
}
