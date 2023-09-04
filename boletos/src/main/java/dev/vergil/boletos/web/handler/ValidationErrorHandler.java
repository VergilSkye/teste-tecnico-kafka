package dev.vergil.boletos.web.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ValidationErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var invalidFields = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String name = ((FieldError) error).getField();
                    String reason = error.getDefaultMessage();
                    return new InvalidField(name, reason);
                })
                .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Dados inválidos");
        problemDetail.setType(URI.create("https://localhost:8081/validation-error"));
        problemDetail.setProperty("invalid-fields", invalidFields);
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        var invalidFields = ex.getConstraintViolations().stream()
                .map(error -> {
                    String name = error.getPropertyPath().toString();
                    String reason = error.getMessage();
                    return new InvalidField(name, reason);
                })
                .toList();

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Dados inválidos");
        problemDetail.setType(URI.create("https://localhost:8081/validation-error"));
        problemDetail.setProperty("invalid-fields", invalidFields);
        return problemDetail;
    }

    record InvalidField(String nome, String motivo) {}
}

