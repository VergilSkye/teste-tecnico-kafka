package dev.vergil.boletos.kafka.consumer;


import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class AssociadoConsumerService {

    // private ReplyingKafkaTemplate<String, String, AssociadoResponse> template;
    private final String requestTopic = "requestAssociado";

    public AssociadoResponse getAssociado(String document) {
        return new AssociadoResponse(UUID.randomUUID(), "", "");
    }

//    public AssociadoResponse getAssociado(String document) {
//        try {
//            return getAssociadoAsync(document).value();
//        } catch (InterruptedException | ExecutionException | TimeoutException e) {
//            Thread.currentThread().interrupt();
//            throw new RuntimeException("Failed to get Associado", e);
//        }
//    }
//    private ConsumerRecord<String, AssociadoResponse> getAssociadoAsync(String document) throws ExecutionException, InterruptedException, TimeoutException {
//        ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, null, document, document);
//        RequestReplyFuture<String, String, AssociadoResponse> future = template.sendAndReceive(record);
//        return future.get(10, TimeUnit.SECONDS);
//
//
//    }


}
