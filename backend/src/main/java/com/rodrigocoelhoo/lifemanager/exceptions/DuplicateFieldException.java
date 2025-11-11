package com.rodrigocoelhoo.lifemanager.exceptions;

public class DuplicateFieldException extends RuntimeException {
    public DuplicateFieldException(String fieldName, String fieldValue) {
        super(String.format("The %s '%s' already exists", fieldName, fieldValue));
    }

    public DuplicateFieldException(String msg) {
        super(msg);
    }
}
