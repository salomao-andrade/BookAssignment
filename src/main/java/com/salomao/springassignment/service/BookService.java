package com.salomao.springassignment.service;

import com.salomao.springassignment.error.exception.ResponseException;
import com.salomao.springassignment.model.Book;
import com.salomao.springassignment.repository.BookRepository;
import com.salomao.springassignment.web.dto.BookRecordIn;
import com.salomao.springassignment.web.dto.BookRecordOut;
import com.salomao.springassignment.web.dto.InsertResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepo;

    /**
     * Returns a single BookRecord object from an id
     *
     * @param id book id
     * @return BookRecord object
     * @throws ResponseException 404 FOUND error if book isn't found
     */
    public BookRecordOut getBookById(Integer id) throws ResponseException {
        if (bookRepo.findById(id).isPresent()) {
            return bookToBookRecord(bookRepo.findById(id).get());
        }
        throw new ResponseException(HttpStatus.NOT_FOUND, "error.book.notFound");
    }

    /**
     * Converts book entity to DTO
     *
     * @param book book entity object
     * @return BookRecord DTO with requested book
     */
    private BookRecordOut bookToBookRecord(Book book) {
        return new BookRecordOut(book.getId(), book.getTitle(), book.getIsbn(), book.getPublishedDate());
    }

    /**
     * Converts list of Book objects to list of BookRecord objects
     *
     * @param books List of Book objects
     * @return List of BookRecord objects
     */
    private List<BookRecordOut> booksToBookRecords(List<Book> books) {
        List<BookRecordOut> bookRecordOuts = new ArrayList<>();
        for (Book book : books) {
            bookRecordOuts.add(bookToBookRecord(book));
        }
        return bookRecordOuts;

    }

    /**
     * Returns a list of BookRecords from a Specification<Book>
     *
     * @param bookSpec specification with filters for searching books
     * @return List of BookRecord objects
     */
    public List<BookRecordOut> getAllBooksBySpecification(Specification<Book> bookSpec) {
        return booksToBookRecords(bookRepo.findAll(bookSpec));
    }

    /**
     * Saves a book to the repository
     *
     * @param bookRecordOut BookRecordOut DTO object
     */
    public InsertResponseDTO insertBook(BookRecordIn bookRecordOut) {
        Book book = new Book();
        book.setTitle(bookRecordOut.title());
        book.setIsbn(bookRecordOut.isbn());
        book.setPublishedDate(bookRecordOut.publishedDate());

        return new InsertResponseDTO(bookRepo.save(book).getId(), "success.book.insert");
    }

    /**
     * Updates a book in the repository
     *
     * @param id           id of book to be updated
     * @param bookRecordIn data to be updated
     * @return success message code
     * @throws ResponseException 404 NOT FOUND
     */
    public String updateBook(Integer id, BookRecordIn bookRecordIn) throws ResponseException {
        Book book = bookRepo.findById(id).orElseThrow(() ->
                new ResponseException(HttpStatus.NOT_FOUND, "error.book.notFound"));
        if (!checkIfTitleAndISBNIsUnique(bookRecordIn.title(), bookRecordIn.isbn())) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, "error.book.notUnique");
        }
        book.setTitle(bookRecordIn.title());
        book.setIsbn(bookRecordIn.isbn());
        book.setPublishedDate(bookRecordIn.publishedDate());

        bookRepo.save(book);

        return "success.book.update";
    }

    /**
     * Checks if book title is unique
     *
     * @param title title to be checked
     * @return true if its unique, false if it already exists
     */
    public boolean checkIfTitleAndISBNIsUnique(String title, String isbn) {
        return !(bookRepo.findByTitle(title).isPresent() || bookRepo.findByIsbn(isbn).isPresent());
    }

}
