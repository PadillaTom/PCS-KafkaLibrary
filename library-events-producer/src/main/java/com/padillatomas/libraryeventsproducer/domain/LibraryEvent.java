package com.padillatomas.libraryeventsproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class LibraryEvent {

    private Integer id;
    private LibraryEventType libraryEventType;

    @NotNull
    @Valid
    private Book book;

}
