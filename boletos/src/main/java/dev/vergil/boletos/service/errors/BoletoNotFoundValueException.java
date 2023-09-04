package dev.vergil.boletos.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class BoletoNotFoundValueException extends ErrorResponseException {

    public BoletoNotFoundValueException() {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(), null);
    }

    private static ProblemDetail asProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "O boleto não foi encontrado por favor tente novamente");
        problemDetail.setTitle("Boleto Not Found");
        problemDetail.setType(URI.create("https://localhost:8081/boleto-not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

}
