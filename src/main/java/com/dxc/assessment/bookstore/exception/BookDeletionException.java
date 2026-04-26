package com.dxc.assessment.bookstore.exception;

public class BookDeletionException extends RuntimeException {
    public BookDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
