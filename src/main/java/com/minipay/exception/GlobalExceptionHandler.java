package com.minipay.exception;

import com.minipay.common.ApiResponse;
import com.minipay.common.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final String INVALID_REQUEST_URI ="https://docs.minipay.com/errors/invalid-request";
    private static final String INTERNAL_ERROR ="https://docs.minipay.com/errors/internal";
    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApiException(ApiException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid Request");
        problem.setType(URI.create(INVALID_REQUEST_URI));
        problem.setProperty("instance", request.getRequestURI());
        problem.setProperty("correlationId", request.getAttribute("CORRELATION_ID"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create(INTERNAL_ERROR));
        problem.setProperty("instance", request.getRequestURI());
        problem.setProperty("correlationId", request.getAttribute("CORRELATION_ID"));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation Failed");
        problem.setDetail("One or more request fields are invalid.");
        problem.setType(URI.create(INVALID_REQUEST_URI));
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

}
