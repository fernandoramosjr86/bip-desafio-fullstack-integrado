package com.example.backend.adapters.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.backend.adapters.in.web.dto.ErrorResponse;
import com.example.backend.adapters.in.web.dto.ValidationErrorResponse;
import com.example.backend.adapters.in.web.dto.BeneficioRequest;
import com.example.backend.domain.exception.BeneficioNaoEncontradoException;
import com.example.backend.domain.exception.RegraNegocioException;
import jakarta.persistence.OptimisticLockException;
import java.lang.reflect.Method;
import org.springframework.core.MethodParameter;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleNotFoundDeveRetornar404ComMensagem() {
        ResponseEntity<ErrorResponse> response =
                handler.handleNotFound(new BeneficioNaoEncontradoException(10L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Beneficio nao encontrado para id=10", response.getBody().message());
    }

    @Test
    void handleRegraNegocioDeveRetornar400ComMensagem() {
        ResponseEntity<ErrorResponse> response =
                handler.handleRegraNegocio(new RegraNegocioException("Falha de regra"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Falha de regra", response.getBody().message());
    }

    @Test
    void handleConcurrencyDeveRetornar409ComMensagemPadrao() {
        ResponseEntity<ErrorResponse> response =
                handler.handleConcurrency(new OptimisticLockException("erro"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflito de concorrencia. Tente novamente.", response.getBody().message());
    }

    @Test
    void handleValidationDeveRetornar400ComCampos() {
        MethodArgumentNotValidException exception = methodArgumentNotValidException("nome", "obrigatorio");

        ResponseEntity<ValidationErrorResponse> response =
                handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro de validacao", response.getBody().message());
        assertEquals("obrigatorio", response.getBody().fields().get("nome"));
    }

    private MethodArgumentNotValidException methodArgumentNotValidException(String field, String message) {
        try {
            Method method = ValidationControllerStub.class.getDeclaredMethod("endpoint", BeneficioRequest.class);
            MethodParameter parameter = new MethodParameter(method, 0);
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "beneficioRequest");
            bindingResult.addError(new FieldError("beneficioRequest", field, message));
            return new MethodArgumentNotValidException(parameter, bindingResult);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final class ValidationControllerStub {
        @SuppressWarnings("unused")
        void endpoint(BeneficioRequest request) {
        }
    }
}
