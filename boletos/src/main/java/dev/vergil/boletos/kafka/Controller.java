package dev.vergil.boletos.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vergil.boletos.kafka.consumer.AssociadoResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;



@Component
@Slf4j
@RequiredArgsConstructor
public class Controller {

    private final ObjectMapper objectMapper;

    private final ReplyingKafkaTemplate<String, String, AssociadoResponse> template;

    // Starting with version 2.5, the framework will detect if these headers are missing and populate them with the topic
    // - either the topic determined from the @SendTo value or the incoming KafkaHeaders.REPLY_TOPIC header (if present).
    // It will also echo the incoming KafkaHeaders.CORRELATION_ID and KafkaHeaders.REPLY_PARTITION, if present.
    @KafkaListener(id = "server-side-replyto-group-id", topics = "request")
    @SendTo  // default REPLY_TOPIC header
    public Message<AssociadoResponse> messageReturn(ConsumerRecord<String,AssociadoResponse> record) {
        log.info("-- request -- headers: {}", record.headers());
        log.info("-- request -- message: {}", record.value());

        return MessageBuilder.withPayload(getAss())
                .setHeader(KafkaHeaders.KEY, getAss().toString())
                .build();
    }

    @NotNull
    private static AssociadoResponse getAss() {
        return new AssociadoResponse(UUID.randomUUID(), "49351840000", "49351840000");
    }

    @SneakyThrows
    @Scheduled(fixedRate = 5000, initialDelay = 500)
    public void scheduleTaskWithFixedRate() {
        log.info("Init request-reply");
        ProducerRecord<String, String> record = new ProducerRecord<>("request", "49351840000");
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "response".getBytes()));
        RequestReplyFuture<String, String, AssociadoResponse> replyFuture = template.sendAndReceive(record);
        SendResult<String, String> sendResult = replyFuture.getSendFuture().get(5, TimeUnit.SECONDS);
        log.info("Sent ok: {}", sendResult.getRecordMetadata());
        ConsumerRecord<String, AssociadoResponse> consumerRecord = replyFuture.get(5, TimeUnit.SECONDS);
        log.info("-- response -- message: {}", consumerRecord.value());
        log.info("-- response -- headers: {}", consumerRecord.headers());
    }



}
