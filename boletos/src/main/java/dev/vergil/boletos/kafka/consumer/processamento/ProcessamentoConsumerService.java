package dev.vergil.boletos.kafka.consumer.processamento;

import dev.vergil.boletos.service.BoletoService;
import dev.vergil.boletos.service.dto.PagamentoBoletoDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class ProcessamentoConsumerService {
    private final BoletoService boletoService;
    @KafkaListener(topics = "batch", groupId = "batch")
    public void onMessage(@Payload List<String> codes,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        log.info("----- Starting the process to receive batch messages in topic {} -----", topic);

        codes.forEach(e -> {
            try {
                boletoService.pay(convertCodeToBoletoDTO(e));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });




    }

    private PagamentoBoletoDTO convertCodeToBoletoDTO(String e) {
        var documentoPagador = extractMeaningfulDataFromCode(e, 0, 14);

        var documentoUuid = extractMeaningfulDataFromCode(e, 14, 28);;

        var valor = extractMeaningfulDataFromCode(e, 28, 42);;

        BigDecimal valor1 = BigDecimal.valueOf(Double.parseDouble(valor)).setScale(2, RoundingMode.UP);
        return new PagamentoBoletoDTO(Long.valueOf(documentoUuid), documentoPagador, valor1);
    }

    @NotNull
    private static String extractMeaningfulDataFromCode(String code, int beginIndex, int endIndex) {
        return code
                .substring(beginIndex, endIndex)
                .replaceFirst("^0+(?!$)", "");
    }

}
