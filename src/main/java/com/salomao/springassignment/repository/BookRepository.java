package com.salomao.springassignment.repository;

import com.salomao.springassignment.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {

    List<Book> findAll(Specification<Book> bookSpec);

    Optional<Book> findByTitle(String title);
    Optional<Book> findByIsbn(String isbn);
}
