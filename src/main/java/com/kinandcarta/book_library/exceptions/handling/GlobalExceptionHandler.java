package com.kinandcarta.book_library.exceptions.handling;

import com.kinandcarta.book_library.exceptions.CustomBadRequestException;
import com.kinandcarta.book_library.exceptions.CustomNotFoundException;
import com.kinandcarta.book_library.exceptions.CustomUnprocessableEntityException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> objectErrorList = bindingResult.getAllErrors();
        Map<String, String> errorFields = new HashMap<>();
        for (ObjectError objectError : objectErrorList) {
            FieldError fieldError = (FieldError) objectError;
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            errorFields.put(fieldName, errorMessage);
        }

        ExceptionMessage exceptionMessage = new ExceptionMessage(errorFields);
        return ResponseEntity.badRequest().body(exceptionMessage);
    }

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ExceptionMessage> handleCustomNotFoundException(CustomNotFoundException customNotFoundException) {
        ExceptionMessage exceptionMessage = new ExceptionMessage(customNotFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionMessage);
    }

    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionMessage> handleCustomBadRequestException(CustomBadRequestException customBadRequestException) {
        ExceptionMessage exceptionMessage = new ExceptionMessage(customBadRequestException.getMessage());

        return ResponseEntity.badRequest().body(exceptionMessage);
    }

    @ExceptionHandler(CustomUnprocessableEntityException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ExceptionMessage> handleCustomUnprocessableEntityException(CustomUnprocessableEntityException customUnprocessableEntityException) {
        ExceptionMessage exceptionMessage = new ExceptionMessage(customUnprocessableEntityException.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(exceptionMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionMessage> handleCustomIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        ExceptionMessage exceptionMessage = new ExceptionMessage(illegalArgumentException.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionMessage);
    }
}
