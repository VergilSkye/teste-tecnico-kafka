//package dev.vergil.boletos.kafka.config;
//
//import dev.vergil.boletos.kafka.consumer.AssociadoResponse;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.protocol.types.Field;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.config.TopicBuilder;
//import org.springframework.kafka.core.*;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaConfigRequestReply {
//    /*
//     * Client side (sending record with reply)
//     * Spring provides a ReplyingKafkaTemplate <K, V, R>, which offers a return object or a reply object once the message is consumed by the kafka listener from another side.
//     */
//    // ReplyingKafkaTemplate is not autoconfigure
//    // ConcurrentKafkaListenerContainerFactory is autoconfigure
//    // ProducerFactory is autoconfigure
//    @Bean
//    public ReplyingKafkaTemplate<String, String, AssociadoResponse> replyingKafkaTemplate() {
//        ProducerFactory<String, String> pf = producerFactory();
//
//        ConcurrentMessageListenerContainer<String, AssociadoResponse> replyContainer = associadoKafkaListenerContainerFactory().createContainer("response");
//        replyContainer.getContainerProperties().setGroupId("client-side-replyto-group-id");
//
//        return new ReplyingKafkaTemplate<>(pf, replyContainer);
//    }
//
//
//
//    /*
//     * Server side: @KafkaListener with @sendto
//     * We also need to define a KafkaTemplate bean, which is used as the reply template with producer factory having data type: ProducerFactory<K, V>. This template is set as the reply template of ConcurrentKafkaListenerContainerFactory (which is the expecting String as a return type from the listener).
//     */
//    @Bean
//    public KafkaTemplate<String, AssociadoResponse> templateListenerWithReply() {
//        // Create a new standard kafkaTemplate
//        KafkaTemplate<String, AssociadoResponse> kafkaTemplate = new KafkaTemplate<>(associadoProducerFactory());
//        // add it to factory
//        associadoKafkaListenerContainerFactory().setReplyTemplate(kafkaTemplate);
//        return kafkaTemplate;
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, AssociadoResponse>
//    associadoKafkaListenerContainerFactory() {
//
//        ConcurrentKafkaListenerContainerFactory<String, AssociadoResponse> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(associadoConsumerFactory());
//        return factory;
//    }
//
//    private ProducerFactory<String, AssociadoResponse> associadoProducerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerProps() );
//    }
//
//    private ProducerFactory<String, String> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerProps() );
//    }
//
//    private Map<String, Object> producerProps() {
//        Map<String, Object> props = new HashMap<>();
//
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//        return props;
//    }
//
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        return props;
//    }
//
//    @Bean
//    public ConsumerFactory<String, AssociadoResponse> associadoConsumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), new JsonDeserializer<>(AssociadoResponse.class));
//    }
//
//
//
//    /*
//     * Topic creation part
//     */
//    @Bean
//    public NewTopic request() {
//        return TopicBuilder.name("request").partitions(1).replicas(1).build();
//    }
//
//    @Bean
//    public NewTopic response() {
//        return TopicBuilder.name("response").partitions(1).replicas(1).build();
//    }
//
//
//}
