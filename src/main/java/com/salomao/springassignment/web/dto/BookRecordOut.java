package com.salomao.springassignment.web.dto;

import java.time.LocalDate;

public record BookRecordOut(Integer id, String title, String isbn, LocalDate publishedDate) {
}
