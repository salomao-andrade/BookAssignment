package com.salomao.springassignment.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookRecordIn(@NotBlank(message = "{error.book.title.notNull}") String title,
                           @NotBlank(message = "{error.book.isbn.notNull}") String isbn,
                           @NotNull(message = "{error.book.publishedDate.notNull}") LocalDate publishedDate) {
}
