package dev.vergil.boletos.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class PaymentAlreadyMadeException extends ErrorResponseException {

    public PaymentAlreadyMadeException() {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(), null);
    }

    private static ProblemDetail asProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "O boleto já foi pago");
        problemDetail.setTitle("Payment Already Made");
        problemDetail.setType(URI.create("https://localhost:8081/payment-already-made"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

}
