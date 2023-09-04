package dev.vergil.boletos.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class MismatchIdentifierException extends ErrorResponseException {


    public MismatchIdentifierException() {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(), null);
    }

    private static ProblemDetail asProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "O documento informado não é o mesmo cadastrado");
        problemDetail.setTitle("Mismatch Identifier");
        problemDetail.setType(URI.create("https://localhost:8081/mismatch-identifier"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}

