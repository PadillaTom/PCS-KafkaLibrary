package com.padillatomas.libraryeventsproducer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.padillatomas.libraryeventsproducer.domain.Book;
import com.padillatomas.libraryeventsproducer.domain.LibraryEvent;
import com.padillatomas.libraryeventsproducer.producer.LibraryEventsProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
class LibraryEventsControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    // MOCKS:
    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibraryEvent() throws Exception {
        // Given:
        Book myBook = Book.builder()
                .withId(123)
                .withTitle("POST Book")
                .withAuthor("POST Book")
                .build();

        LibraryEvent myLibraryEvent = LibraryEvent.builder()
                .withId(456)
                .withBook(myBook)
                .build();


        doNothing().when(libraryEventsProducer)
                        .sendLibraryEvents(
                                isA(LibraryEvent.class)
                        );

        // When:
        mockMvc.perform(
                post("/api/v1/library-event")
                        .content(objectMapper.writeValueAsString(myLibraryEvent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(
                        status().isCreated()
                );
    }

}