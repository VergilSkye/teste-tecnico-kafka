package dev.vergil.boletos.kafka.consumer;


import dev.vergil.library.kafka.model.AssociadoResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@AllArgsConstructor
public class AssociadoConsumerService {

    private final ReplyingKafkaTemplate<String, String, AssociadoResponse> template;

    public AssociadoResponse getAssociado(String document) {
        try {
            return getAssociadoAsync(document).value();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get Associado", e);
        }
    }
    private ConsumerRecord<String, AssociadoResponse> getAssociadoAsync(String document) throws ExecutionException, InterruptedException, TimeoutException, ExecutionException, TimeoutException {
        log.info("Init request-reply");
        ProducerRecord<String, String> record = new ProducerRecord<>("request", document);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "response".getBytes()));
        RequestReplyFuture<String, String, AssociadoResponse> replyFuture = template.sendAndReceive(record);
        SendResult<String, String> sendResult = replyFuture.getSendFuture().get(5, TimeUnit.SECONDS);
        log.info("Sent ok: {}", sendResult.getRecordMetadata());
        return replyFuture.get(5, TimeUnit.SECONDS);


    }


}
