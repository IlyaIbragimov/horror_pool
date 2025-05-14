package com.social.horror_pool.exception;

public class ResourceNotFoundException extends RuntimeException{

    private String resourceName;

    private String fieldName;

    private Long fieldId;

    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldId) {
        super(String.format("%s was not found with %s : %d", resourceName, fieldName, fieldId));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldId =fieldId;
    }
}
