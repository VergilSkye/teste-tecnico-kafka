package dev.vergil.boletos.kafka.producer;

import dev.vergil.boletos.service.BoletoService;
import dev.vergil.boletos.service.dto.BoletoDTO;
import dev.vergil.library.kafka.model.AssociadoBoletosResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class AssociadoBoletosProducerService {

    private final BoletoService service;
   
    @KafkaListener(id = "${spring.kafka.consumer.group-id}", topics = "${topics.boleto-request}")
    @SendTo  // default REPLY_TOPIC header
    public Message<AssociadoBoletosResponse> messageReturn(ConsumerRecord<String,String> record) {
        log.info("-- request -- headers: {}", record.headers());
        log.info("-- request -- message: {}", record.value());

//        var boletoResponses = service.findAllBoletoByUuidAssociado(uuidAssociado).stream().map(AssociadoBoletosProducerService::BoletoResponse).toList();


        var boletoResponses = service.findAllBoletoByDocumentoPagador(record.value()).stream().map(AssociadoBoletosProducerService::BoletoResponse).toList();


        return MessageBuilder.withPayload(new AssociadoBoletosResponse(UUID.randomUUID(),boletoResponses ))
                .setHeader(KafkaHeaders.KEY, record.value())
                .build();
    }

    private static AssociadoBoletosResponse.BoletoResponse BoletoResponse(BoletoDTO boletoDTO) {
        return new AssociadoBoletosResponse.BoletoResponse(boletoDTO.getId(), boletoDTO.getValor(), boletoDTO.getVencimento(), boletoDTO.getSituacao());
    }
}
