package com.padillatomas.libraryeventsproducer.controller;

import com.padillatomas.libraryeventsproducer.domain.Book;
import com.padillatomas.libraryeventsproducer.domain.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
        topics = {"library-events"},
        partitions = 3
)
@TestPropertySource(
        properties = {
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
        }
)
class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @LocalServerPort
    int randomServerPort;

    private Consumer<Integer, String> consumer;


    @BeforeEach
    void setUp()
    {
        Map<String, Object> configsForConsumer = new HashMap<>(
                KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker)
        );

        consumer = new DefaultKafkaConsumerFactory<Integer, String>(
                configsForConsumer,
                new IntegerDeserializer(),
                new StringDeserializer()
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown()
    {
        consumer.close();
    }

    /**
     * POST at /api/v1/library-event
     * */
    @Test
    @Timeout(3)
    void postLibraryEvent() throws URISyntaxException, InterruptedException {
        // Given:
        Book myBook = Book.builder()
                .withId(123)
                .withTitle("POST Book")
                .withAuthor("POST Book")
                .build();

        LibraryEvent myLibraryEvent = LibraryEvent.builder()
                .withId(null)
                .withBook(myBook)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(myLibraryEvent, headers);

        final URI myURI = new URI("http://localhost:" + randomServerPort + "/api/v1/library-event");

        // When:
        ResponseEntity<LibraryEvent> responseEntity =
                testRestTemplate.exchange(myURI, HttpMethod.POST, request, LibraryEvent.class);

        // Then:

        // POST
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        // Message
        ConsumerRecord<Integer, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");

        // Thread.sleep(3000); // Will wait 3s before executing the rest of the code. Should be replaced with @Timeout from Junit

        String expectedRecord = "{\"id\":null,\"libraryEventType\":\"NEW\",\"book\":{\"id\":123,\"title\":\"POST Book\",\"author\":\"POST Book\"}}";
        String singleRecordValue = singleRecord.value();
        assertEquals(expectedRecord,singleRecordValue);

    }

}