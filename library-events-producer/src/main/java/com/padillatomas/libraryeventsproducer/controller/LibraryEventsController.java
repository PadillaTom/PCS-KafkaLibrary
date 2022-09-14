package com.padillatomas.libraryeventsproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.padillatomas.libraryeventsproducer.domain.LibraryEvent;
import com.padillatomas.libraryeventsproducer.producer.LibraryEventsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LibraryEventsController {

    @Autowired
    LibraryEventsProducer libraryEventsProducer;

    @PostMapping("/v1/library-event")
    public ResponseEntity<LibraryEvent> insertNewBook(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        // Send Message:
        libraryEventsProducer.sendLibraryEvents(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);

    }

}
