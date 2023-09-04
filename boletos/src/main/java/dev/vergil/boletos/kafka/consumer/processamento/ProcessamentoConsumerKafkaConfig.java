package dev.vergil.boletos.kafka.consumer.processamento;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProcessamentoConsumerKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String boostrapServers;

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, String.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "batch");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "dev.vergil.*");
        return props;
    }

    @Bean
    public ConsumerFactory<String, String> consumerBatchFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaBatchListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerBatchFactory());
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(new CommonLoggingErrorHandler());
        return factory;
    }



}
