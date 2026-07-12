package com.origem.backend.web;

import com.origem.backend.dto.ApiError;
import com.origem.backend.exception.InvalidFieldException;
import com.origem.backend.exception.InvalidPaymentModeException;
import com.origem.backend.exception.SignupNotFoundException;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(ApiError.of("Campos inválidos", fieldErrors));
    }

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<ApiError> handleInvalidField(InvalidFieldException ex) {
        List<ApiError.FieldError> fieldErrors = List.of(new ApiError.FieldError(ex.getField(), ex.getMessage()));
        return ResponseEntity.badRequest().body(ApiError.of("Campos inválidos", fieldErrors));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiError> handleMissingHeader(MissingRequestHeaderException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of("Cadastro não encontrado"));
    }

    @ExceptionHandler(SignupNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(SignupNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of(ex.getMessage()));
    }

    @ExceptionHandler(InvalidPaymentModeException.class)
    public ResponseEntity<ApiError> handleInvalidPaymentMode(InvalidPaymentModeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of(ex.getMessage()));
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ApiError> handleStripe(StripeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiError.of("Erro ao comunicar com o Stripe: " + ex.getMessage()));
    }
}
