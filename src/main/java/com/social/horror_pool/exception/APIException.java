package com.social.horror_pool.exception;

public class APIException extends RuntimeException{

    private String message;

    public APIException(String message) {
        super(message);
    }
}
