package com.salomao.springassignment.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@ToString
@EqualsAndHashCode
@Entity
public class Book implements Serializable {

    @Serial
    private static final long serialVersionUID = 2428697559413907155L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String isbn;

    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate;
}
