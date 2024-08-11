package com.example.spring_mock_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class EmptyValueException extends RuntimeException {
    static final long serialVersionUID = 4564358810063335201L;
    public EmptyValueException(String message) {
        super(message);
    }
}
