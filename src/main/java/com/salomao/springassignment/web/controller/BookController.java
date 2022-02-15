package com.salomao.springassignment.web.controller;

import com.salomao.springassignment.error.ApiError;
import com.salomao.springassignment.error.exception.ResponseException;
import com.salomao.springassignment.model.Book;
import com.salomao.springassignment.service.BookService;
import com.salomao.springassignment.web.dto.BookRecordIn;
import com.salomao.springassignment.web.dto.BookRecordOut;
import com.salomao.springassignment.web.dto.InsertResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    private final MessageSource messageSource;

    /**
     * Returns a single BookRecord
     *
     * @param id      id of the Book
     * @param request HttpServletRequest
     * @return ResponseEntity with StatusCode 200 and single BookRecord or StatusCode 404 NOT FOUND
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get book",
            responses = {@ApiResponse(responseCode = "404", description = "Book was not found")}
    )
    public ResponseEntity<BookRecordOut> getBook(@PathVariable Integer id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(bookService.getBookById(id));
        } catch (ResponseException e) {
            log.error("Error retrieving book with id {}", id);
            throw new ResponseStatusException(e.getCode(),
                    String.format(messageSource.getMessage(e.getMessage(), null, request.getLocale()), id), e);
        }
    }

    /**
     * Returns a list of BookRecords
     *
     * @param bookSpec        specification with optional filters
     * @param title           optional filter
     * @param isbn            optional filter
     * @param publishedAfter  optional filter. If one of the date params is informed, the other one must also be present.
     *                        uses 'yyyy-MM-dd' format.
     * @param publishedBefore optional filter. If one of the date params is informed, the other one must also be present.
     *                        uses 'yyyy-MM-dd' format.
     * @return ResponseEntity with StatusCode 200 and List of BookRecords or StatusCode 400 BAD REQUEST
     */
    @GetMapping
    @Operation(summary = "Get List of Books",
            responses = {@ApiResponse(responseCode = "400", description = "Invalid filters")})
    public ResponseEntity<List<BookRecordOut>> getBooks(@And({
            @Spec(path = "title", spec = Like.class),
            @Spec(path = "isbn", spec = Like.class),
            @Spec(
                    path = "publishedDate",
                    params = {"publishedAfter", "publishedBefore"},
                    spec = Between.class,
                    config = "yyyy-MM-dd"
            )
    })
                                                     @Parameter(hidden = true) Specification<Book> bookSpec,
                                                        @Parameter(description = "Book title")
                                                     @RequestParam(value = "title", required = false) String title,
                                                        @Parameter(description = "Book's ISBN code")
                                                     @RequestParam(value = "isbn", required = false) String isbn,
                                                        @Parameter(description = "Date Range parameter indicating the beginning of the range. " +
                                                             "Inform date in the format: 'yyyy-MM-dd'")
                                                     @RequestParam(value = "publishedAfter", required = false) String publishedAfter,
                                                        @Parameter(description = "Date Range parameter indicating the end of the range. " +
                                                             "Inform date in the format: 'yyyy-MM-dd'")
                                                     @RequestParam(value = "publishedBefore", required = false) String publishedBefore) {
        return ResponseEntity.ok(bookService.getAllBooksBySpecification(bookSpec));
    }

    /**
     * Creates a new Book and inserts it into the database
     *
     * @param bookRecordIn BookRecord object containing valid data for the book to be inserted
     * @return ResponseEntity with StatusCode 201 CREATED and InsertResponse containing id and success message
     * or StatusCode 400 BAD REQUEST and error message
     */
    @PostMapping
    @Operation(summary = "Create Book",
            responses = {@ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<InsertResponseDTO> insertBook(@Valid @RequestBody BookRecordIn bookRecordIn,
                                                        HttpServletRequest request) {
        if (bookService.checkIfTitleAndISBNIsUnique(bookRecordIn.title(), bookRecordIn.isbn())) {
            InsertResponseDTO response = bookService.insertBook(bookRecordIn);
            response.setMessage(messageSource.getMessage(response.getMessage(), null, request.getLocale()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("error.book.notUnique", null, request.getLocale()));
        }
    }

    /**
     * Updates an existing book
     * @param bookRecordIn information to be updated
     * @param id id of book to be updated
     * @param request request
     * @return ResponseEntity with StatusCode 200 and success message or StatusCode 404 NOT FOUND or 400 BAD REQUEST
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update Book",
            responses = {
                @ApiResponse(responseCode = "400", description = "Bad request"),
                @ApiResponse(responseCode = "404", description = "Book was not found")

            })
    public ResponseEntity<String> updateBook(@RequestBody @Valid BookRecordIn bookRecordIn,
                                             @PathVariable Integer id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(messageSource
                                     .getMessage(bookService.updateBook(id, bookRecordIn),
                                                 null,
                                                 request.getLocale()));
        } catch (ResponseException e) {
            if (e.getCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ResponseStatusException(e.getCode(),
                        String.format(messageSource.getMessage(e.getMessage(), null, request.getLocale()), id), e);
            }
            throw new ResponseStatusException(e.getCode(),
                    messageSource.getMessage(e.getMessage(), null, request.getLocale()), e);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a book",
            responses = {@ApiResponse(responseCode = "400", description = "Invalid filters")})
    public ResponseEntity<String> deleteBook(@PathVariable Integer id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(messageSource.getMessage(bookService.deleteBook(id), null, request.getLocale()));
        } catch (ResponseException e) {
            throw new ResponseStatusException(e.getCode(),
                    String.format(messageSource.getMessage(e.getMessage(), null, request.getLocale()), id), e);
        }
    }

    /**
     * Handles IllegalArgumentExceptions, especially one thrown if one of the date range parameters is informed
     * but the other one is not
     *
     * @param e IllegalArgumentException to be handled
     * @return ApiError object
     */
    @ExceptionHandler({IllegalArgumentException.class})
    public ApiError handleException(IllegalArgumentException e) {
        if (e.getMessage().contains("expected 2 http")) {
            log.error("Bad request: only one date range parameter was informed", e);
            return new ApiError(HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    "If filtering by date, both before and after dates must be informed");
        } else {
            log.error("Bad request: illegalArgumentException", e);
            return new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Bad request");
        }
    }

    /**
     * Handles HttpMessageNotReadableException, especially if thrown when the date format is incorrect
     *
     * @param e IllegalArgumentException to be handled
     * @return ApiError object
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ApiError handleException(HttpMessageNotReadableException e) {
        if (e.getMessage().contains("Cannot deserialize value of type `java.time.LocalDate` from String")) {
            log.error("Bad request: Invalid date format", e);
            return new ApiError(HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    "Dates must be informed in 'yyyy-MM-dd' format");
        } else {
            log.error("Bad request: HttpMessageNotReadableException", e);
            return new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", "Bad request");
        }
    }

}
