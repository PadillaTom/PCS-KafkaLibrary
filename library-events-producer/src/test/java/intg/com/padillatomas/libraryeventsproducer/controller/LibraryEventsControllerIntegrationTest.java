package com.padillatomas.libraryeventsproducer.controller;

import com.padillatomas.libraryeventsproducer.domain.Book;
import com.padillatomas.libraryeventsproducer.domain.LibraryEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @LocalServerPort
    int randomServerPort;

    /**
     * POST at /api/v1/library-event
     * */
    @Test
    void postLibraryEvent() throws URISyntaxException {
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
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

    }

}