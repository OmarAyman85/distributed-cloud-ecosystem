package com.ayman.distributed.authy.common.exception;

public class PhoneAlreadyExistsException extends RuntimeException {
    public PhoneAlreadyExistsException(String phone) {
        super("Phone number already exists: " + phone);
    }
}
