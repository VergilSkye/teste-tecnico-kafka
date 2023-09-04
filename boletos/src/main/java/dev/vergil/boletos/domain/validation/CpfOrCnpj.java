package dev.vergil.boletos.domain.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = {CpfOrCnpjValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface CpfOrCnpj {

    String message() default "CPF ou CNPJ em formato inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

