package dev.vergil.boletos.kafka.consumer;


import dev.vergil.library.kafka.model.AssociadoResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Configuration
public class ConsumerKafkaConfig {

    @Bean
    public ProducerFactory<String, String> requestProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProps(), new StringSerializer(), new JsonSerializer<>());
    }

    @Bean
    public ConsumerFactory<String, AssociadoResponse> replyConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),  new StringDeserializer(), new JsonDeserializer<>(AssociadoResponse.class));
    }
    @Bean
    public ReplyingKafkaTemplate<String, String, AssociadoResponse> replyKafkaTemplate(ProducerFactory<String, String> pf, KafkaMessageListenerContainer<String, AssociadoResponse> container) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    @Bean
    public KafkaMessageListenerContainer<String, AssociadoResponse> replyContainer(ConsumerFactory<String, AssociadoResponse> cf) {
        ContainerProperties containerProperties = new ContainerProperties("response");
        containerProperties.setGroupId("client-side-replyto-group-id");
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }


    @Bean
    public NewTopic response() {
        return TopicBuilder.name("response").partitions(1).replicas(1).build();
    }

    private Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, String.class);

        return props;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "dev.vergil.*");
        return props;
    }
}