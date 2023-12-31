package dev.vergil.boletos.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class MismatchUuidAssociadoException extends ErrorResponseException {

    public MismatchUuidAssociadoException() {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(), null);
    }

    private static ProblemDetail asProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "O documento informado não é o mesmo cadastrado");
        problemDetail.setTitle("Mismatch Uuid Associado");
        problemDetail.setType(URI.create("https://localhost:8081/mismatch-uuid-associado"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }


}
