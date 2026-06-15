package com.kushal.razorpay.common.exception;
import lombok.Getter;

@Getter //to get the errorCode
public class DuplicateResourceException extends RuntimeException{

    private final String errorCode;

    public DuplicateResourceException(String errorCode,String message){
        super(message);
        this.errorCode = errorCode;
    }
}
