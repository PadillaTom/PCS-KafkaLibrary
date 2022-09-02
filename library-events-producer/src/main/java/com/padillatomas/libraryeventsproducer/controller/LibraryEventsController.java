package com.padillatomas.libraryeventsproducer.controller;

import com.padillatomas.libraryeventsproducer.domain.LibraryEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LibraryEventsController {

    @PostMapping("/v1/library-event")
    public ResponseEntity<LibraryEvent> insertNewBook(@RequestBody LibraryEvent libraryEvent)
    {
        // Invoke Producer:
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);

    }

}
