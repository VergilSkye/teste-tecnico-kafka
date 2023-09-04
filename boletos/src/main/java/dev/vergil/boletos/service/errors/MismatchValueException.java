package dev.vergil.boletos.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class MismatchValueException extends ErrorResponseException {

    public MismatchValueException() {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(), null);
    }

    private static ProblemDetail asProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "O valor informado não é o mesmo cadastrado");
        problemDetail.setTitle(  "Mismatch Value");
        problemDetail.setType(URI.create("https://localhost:8081/mismatch-value"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

}
