package dev.vergil.boletos.kafka.config;


import dev.vergil.boletos.kafka.consumer.AssociadoResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


//@Slf4j
//@EnableKafka
//@Configuration
//public class KafkaConfig {
//
//    @Autowired
//    KafkaProperties kafkaProperties;
//
//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${topics.associado-request}")
//    private String associadoRequest;
//
//
//    @Value("${topics.associado-response}")
//    private String associadoResponse;
//
//    /*
//     * Client side (sending record with reply)
//     * Spring provides a ReplyingKafkaTemplate <K, V, R>, which offers a return object or a reply object once the message is consumed by the kafka listener from another side.
//     */
//    // ReplyingKafkaTemplate is not autoconfigure
//    // ConcurrentKafkaListenerContainerFactory is autoconfigure
//    // ProducerFactory is autoconfigure
//    @Bean
//    public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate(ProducerFactory<String, String> pf,
//                                                                               ConcurrentKafkaListenerContainerFactory<String, String> factory) {
//        ConcurrentMessageListenerContainer<String, String> replyContainer = factory.createContainer(associadoResponse);
//        replyContainer.getContainerProperties().setGroupId(groupId);
//        return new ReplyingKafkaTemplate<>(pf, replyContainer);
//    }
//
//    /*
//     * Server side: @KafkaListener with @sendto
//     * We also need to define a KafkaTemplate bean, which is used as the reply template with producer factory having data type: ProducerFactory<K, V>. This template is set as the reply template of ConcurrentKafkaListenerContainerFactory (which is the expecting String as a return type from the listener).
//     */
//    @Bean
//    public KafkaTemplate<String, String> templateListenerWithReply(ProducerFactory<String, String> pf,
//                                                                   ConcurrentKafkaListenerContainerFactory<String, String> factory) {
//        // Create a new standard kafkaTemplate
//        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(pf);
//        // add it to factory
//        factory.setReplyTemplate(kafkaTemplate);
//        return kafkaTemplate;
//    }
//
//
//    /*
//     * Topic creation part
//     */
//    @Bean
//    public NewTopic request() {
//        return TopicBuilder.name(associadoRequest).partitions(1).replicas(1).build();
//    }
//
//    @Bean
//    public NewTopic response() {
//        return TopicBuilder.name(associadoResponse).partitions(1).replicas(1).build();
//    }
//
////
////    @Bean
////    public KafkaAdmin.NewTopics newTopics(){
////        return new KafkaAdmin.NewTopics(
////                TopicBuilder.name(associadoRequest).build(),
////                TopicBuilder.name(associadoResponse).build()
////        );
////    }
////
////    @Bean
////    public ReplyingKafkaTemplate<String, String, Object> replyingKafkaTemplate(ProducerFactory<String, String> producerFactory,
////                                                                               ConcurrentMessageListenerContainer<String, Object> repliesContainer){
////        return new ReplyingKafkaTemplate<>(producerFactory, repliesContainer);
////    }
////
////    @Bean
////    public ConcurrentMessageListenerContainer<String, Object> repliesContainer(ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
////        ConcurrentMessageListenerContainer<String, Object> container = containerFactory.createContainer(associadoResponse);
////        container.getContainerProperties().setGroupId(groupId);
////        return container;
////    }
////
////    @Bean
////    @Primary
////    public KafkaTemplate<String, Object> defaultKafkaTemplate(
////            ProducerFactory<String, Object> producerFactory) {
////        return new KafkaTemplate<>(producerFactory);
////    }
////
////    @Bean
////    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
////            ConsumerFactory<String, Object> consumerFactory,
////            KafkaTemplate<String, Object> kafkaTemplate) {
////        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
////        factory.setConsumerFactory(consumerFactory);
////        factory.setReplyTemplate(kafkaTemplate);
////        factory.setMissingTopicsFatal(false);
////        return factory;
////    }
////
////    @Bean
////    public KafkaTemplate<String, Object> kafkaTemplate(){
////        return new KafkaTemplate<>(producerFactory());
////    }
////
////    private ProducerFactory<String, Object> producerFactory() {
////        return new DefaultKafkaProducerFactory(producerProps());
////    }
////
////    private Map<String, Object> producerProps() {
////        Map<String, Object> props = new HashMap<>();
////
////        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
////        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
////        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
////
////        return props;
////    }
//
//
//
//
////
////    @Bean
////    ConsumerFactory<String, AssociadoResponse> consumerFactory() {
////
////        final JsonDeserializer<AssociadoResponse> jsonDeserializer = new JsonDeserializer<>();
////        jsonDeserializer.addTrustedPackages("*");
////        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), new StringDeserializer(), jsonDeserializer);
////    }
////
////
////
////    @Bean
////    public Map<String, Object> producerConfiguration() {
////        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties());
////        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
////        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
////        return properties;
////    }
////    @Bean
////    ProducerFactory<String, String> producerFactory() {
////        return new DefaultKafkaProducerFactory<>(producerConfiguration());
////    }
//////
////    @Bean
////    KafkaTemplate<String, String> kafkaTemplate() {
////        return new KafkaTemplate<>(producerFactory());
////    }
////
////
////
////        @Bean //register and configure replying kafka template
////    public ReplyingKafkaTemplate<String, String, AssociadoResponse> replyingAssociadoTemplate() {
////        ReplyingKafkaTemplate<String, String, AssociadoResponse> replyTemplate = new ReplyingKafkaTemplate<>(producerFactory(), repliesAssociadoContainer());
////        replyTemplate.setDefaultReplyTimeout(Duration.ofSeconds(10));
////        replyTemplate.setSharedReplyTopic(true);
////        return replyTemplate;
////    }
////
////        @Bean //register ConcurrentMessageListenerContainer bean
////    public ConcurrentMessageListenerContainer<String, AssociadoResponse> repliesAssociadoContainer() {
////        ConcurrentMessageListenerContainer<String, AssociadoResponse> repliesContainer = kafkaListenerContainerFactory().createContainer(associadoResponse);
////        repliesContainer.getContainerProperties().setGroupId(groupId);
////        repliesContainer.setAutoStartup(true);
////        return repliesContainer;
////    }
//    //
//
//
//
//
//}
