package com.frenchfries.qrmanager.exceptionhandler;

import com.frenchfries.qrmanager.exceptions.ServiceException;
import com.frenchfries.qrmanager.models.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.SortedSet;
import java.util.TreeSet;

@ControllerAdvice
public class QRExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        SortedSet<String> messages = new TreeSet<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> messages
                .add(fieldError.getField() + " " + fieldError.getDefaultMessage()));

        Response response = new Response("Invalid parameter - "
                + String.join(",", messages));

        return super.handleExceptionInternal(ex, response, null,
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException ex, WebRequest request) {
        Response response = new Response(ex.getMessage());

        return super.handleExceptionInternal(ex, response, null, HttpStatus.INTERNAL_SERVER_ERROR,
                request);

    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, WebRequest request) {
        Response response = new Response("We are unable to process your request. Please try again later.");

        return super.handleExceptionInternal(ex, response, null, HttpStatus.INTERNAL_SERVER_ERROR,
                request);

    }

}
