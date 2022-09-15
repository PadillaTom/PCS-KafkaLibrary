package com.padillatomas.libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.padillatomas.libraryeventsproducer.domain.LibraryEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;

@Component
@Slf4j
public class LibraryEventsProducer {

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    final String TOPIC_NAME = "library-events";

    // Send
//    public void sendLibraryEventsToDefaultTopic(LibraryEvent libraryEvent) throws JsonProcessingException {
//
//        // Create KEY and VALUE for Message:
//        Integer myKey = libraryEvent.getId();
//        String myValue = objectMapper.writeValueAsString(libraryEvent);
//
//        // Send to topic:
//        ListenableFuture<SendResult<Integer, String>> myCallBack =
//                kafkaTemplate.sendDefault(myKey, myValue);
//
//        // CallBack for Sent Message:
//        myCallBack.addCallback(
//                new ListenableFutureCallback<SendResult<Integer, String>>() {
//                    @SneakyThrows
//                    @Override
//                    public void onFailure(Throwable ex) {
//                        handleFailure(ex);
//                    }
//
//                    @Override
//                    public void onSuccess(SendResult<Integer, String> result) {
//                        handleSuccess(myKey, myValue, result);
//                    }
//                }
//        );
//    }

    public void sendLibraryEvents(LibraryEvent libraryEvent) throws JsonProcessingException {

        // Create KEY and VALUE for Message:
        Integer myKey = libraryEvent.getId();
        String myValue = objectMapper.writeValueAsString(libraryEvent);

        // Build ProducerRecord:
        ProducerRecord<Integer, String> myRecord = buildProducerRecord(TOPIC_NAME, myKey, myValue);

        // Send ProducerRecord to topic:
        ListenableFuture<SendResult<Integer, String>> myCallBack =
                kafkaTemplate.send(myRecord);

        // CallBack for Sent Message:
        myCallBack.addCallback(
                new ListenableFutureCallback<SendResult<Integer, String>>() {
                    @SneakyThrows
                    @Override
                    public void onFailure(Throwable ex) {
                        handleFailure(ex);
                    }

                    @Override
                    public void onSuccess(SendResult<Integer, String> result) {
                        handleSuccess(myKey, myValue, result);
                    }
                }
        );
    }

    /**
     *
     * Will receive: Topic, Partition, Key, Value, Header.
     *
     */
    private ProducerRecord<Integer, String> buildProducerRecord(String topic_name, Integer key, String value) {
        List<Header> myHeader = List.of(
                new RecordHeader("event-source", "book-scanner".getBytes())
        );
        return new ProducerRecord<Integer, String>(topic_name, null, key, value, myHeader);
    }


    private void handleSuccess(Integer myKey, String myValue, SendResult<Integer, String> result) {
        log.info("=== Message succesfully SENT ===");
        log.info("KEY: {}", myKey);
        log.info("VALUE: {}", myValue);
        log.info("PARTITION: {}", result.getRecordMetadata().partition());
        log.info("================================");
    }

    private void handleFailure(Throwable ex) throws Throwable {
        log.error("=== ERROR sending message ===");
        log.info("Error Message: {}", ex.getMessage());
        throw ex;
    }
}
