package com.rodrigocoelhoo.lifemanager.exceptions;

public class DuplicateFieldException extends RuntimeException {
    public DuplicateFieldException(String fieldName, String fieldValue) {
        super(String.format("The %s '%s' is already in use", fieldName, fieldValue));
    }
}
