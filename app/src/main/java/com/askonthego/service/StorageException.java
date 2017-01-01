package com.askonthego.service;

public class StorageException extends Exception {

    public StorageException(String message, Exception e) {
        super(message, e);
    }
}
