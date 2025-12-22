package com.example.exception;


import lombok.Getter;

@Getter
public class TranslateException extends RuntimeException {
    private final int code;

    public TranslateException(int code, String message) {
        super(message);
        this.code = code;
    }

}
