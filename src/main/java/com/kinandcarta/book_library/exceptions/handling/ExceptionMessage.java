package com.kinandcarta.book_library.exceptions.handling;

import lombok.Getter;

import java.util.Map;

@Getter
public class ExceptionMessage {
    private Map<String, String> errorFields;
    private String generalExceptionMessage;

    public ExceptionMessage(Map<String, String> errorFields) {
        this.errorFields = errorFields;
    }

    public ExceptionMessage(String generalExceptionMessage) {
        this.generalExceptionMessage = generalExceptionMessage;
    }
}
