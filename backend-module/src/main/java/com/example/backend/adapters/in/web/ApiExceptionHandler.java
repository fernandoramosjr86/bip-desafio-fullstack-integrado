package com.example.backend.adapters.in.web;

import com.example.backend.adapters.in.web.dto.ErrorResponse;
import com.example.backend.adapters.in.web.dto.ValidationErrorResponse;
import com.example.backend.domain.exception.BeneficioNaoEncontradoException;
import com.example.backend.domain.exception.RegraNegocioException;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BeneficioNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(BeneficioNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(ex.getMessage()));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErrorResponse> handleRegraNegocio(RegraNegocioException ex) {
        return ResponseEntity.badRequest().body(errorBody(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ValidationErrorResponse("Erro de validacao", fields));
    }

    @ExceptionHandler({OptimisticLockException.class, PessimisticLockException.class, LockTimeoutException.class})
    public ResponseEntity<ErrorResponse> handleConcurrency(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorBody("Conflito de concorrencia. Tente novamente."));
    }

    private ErrorResponse errorBody(String message) {
        return new ErrorResponse(message);
    }
}
