package dev.vergil.boletos.kafka;

import dev.vergil.boletos.kafka.consumer.AssociadoConsumerService;
import dev.vergil.library.kafka.model.AssociadoResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;



@Component
@Slf4j
@RequiredArgsConstructor
public class Controller {
private final AssociadoConsumerService service;

    @SneakyThrows
    @Scheduled(fixedRate = 5000, initialDelay = 500)
    public void scheduleTaskWithFixedRate() {
        log.info("Init request-reply");
        var associado = service.getAssociado("49351840000");
        if (Objects.equals(associado.getDocumento(), "49351840000")) {
            log.info("-- response -- message: {}", associado);
        }

    }

}
