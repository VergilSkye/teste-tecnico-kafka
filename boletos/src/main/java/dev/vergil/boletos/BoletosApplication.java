package dev.vergil.boletos;


import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableScheduling
public class BoletosApplication {


    public static void main(String[] args) {
        SpringApplication.run(BoletosApplication.class, args);
    }




}
