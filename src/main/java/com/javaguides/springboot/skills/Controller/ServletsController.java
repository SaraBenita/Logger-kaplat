package com.javaguides.springboot.skills.Controller;

import com.javaguides.springboot.skills.model.Book;
import com.javaguides.springboot.skills.model.Genre;
import com.javaguides.springboot.skills.model.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.javaguides.springboot.skills.Controller.ServerLogger.*;

@RestController
public class ServletsController {
    ServerUtils serverUtils = new ServerUtils();

    @GetMapping("/books/health")
    public String checkingIfServerIsUp() {
        return "OK";
    }
    @PostMapping(value = "/book", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ServerResponse> createBook(@RequestBody Book bookRequest) {
        int requestNumber = incrementRequestCounter();
        long startTime = System.currentTimeMillis();

        requestLogger.info("Incoming request | #{} | resource: /book | HTTP Verb POST", requestNumber);

        if (bookRequest.getYear() < 1940 || bookRequest.getYear() > 2100) {
            String errorMessage = "Error: Can’t create new Book that its year [" + bookRequest.getYear() + "] is not in the accepted range [1940 -> 2100]";
            booksLogger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ServerResponse(null, errorMessage));
        }

        if (bookRequest.getPrice() < 0) {
            String errorMessage = "Error: Can’t create new Book with negative price";
            booksLogger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ServerResponse(null, errorMessage));
        }

        for (Book book : serverUtils.getSetBooks()) {
            if (book.getTitle().equalsIgnoreCase(bookRequest.getTitle())) {
                String errorMessage = "Error: Book with the title [" + bookRequest.getTitle() + "] already exists in the system";
                booksLogger.error(errorMessage);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ServerResponse(null, errorMessage));
            }
        }

        booksLogger.info("Creating new Book with Title [{}]", bookRequest.getTitle());
        booksLogger.debug("Currently there are {} Books in the system. New Book will be assigned with id {}", serverUtils.getSetBooks().size(), serverUtils.getId() + 1);

        bookRequest.setId(serverUtils.getId() + 1);
        serverUtils.setId(serverUtils.getId() + 1);
        serverUtils.getSetBooks().add(bookRequest);

        long duration = System.currentTimeMillis() - startTime;
        requestLogger.debug("request #{} duration: {}ms", requestNumber, duration);

        return ResponseEntity.ok().body(new ServerResponse((serverUtils.getId()), null));
    }
    @GetMapping("/books/total")
    public ResponseEntity<ServerResponse> getTotalBooks(@RequestParam(required = false) String author,
                                                        @RequestParam(name = "price-bigger-than", required = false) Integer priceBiggerThan,
                                                        @RequestParam(name = "price-less-than", required = false) Integer priceLessThan,
                                                        @RequestParam(name = "year-bigger-than", required = false) Integer yearBiggerThan,
                                                        @RequestParam(name = "year-less-than", required = false) Integer yearLessThan,
                                                        @RequestParam(required = false) String genres) {
        int requestNumber = incrementRequestCounter();
        long startTime = System.currentTimeMillis();

        requestLogger.info("Incoming request | #{} | resource: /books/total | HTTP Verb GET", requestNumber);

        if (genres != null) {
            for (String genre : genres.split(",")) {
                if (Genre.getGenre(genre) == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ServerResponse(null, null));
                }
            }
        }

        int totalCount = 0;

        for (Book book : serverUtils.getSetBooks()) {
            if ((author == null || book.getAuthor().equalsIgnoreCase(author))
                    && (priceBiggerThan == null || book.getPrice() >= priceBiggerThan)
                    && (priceLessThan == null || book.getPrice() <= priceLessThan)
                    && (yearBiggerThan == null || book.getYear() >= yearBiggerThan)
                    && (yearLessThan == null || book.getYear() <= yearLessThan)
                    && (genres == null || containsAnyGenre(book, genres))) {
                totalCount++;
            }
        }

        if (author == null && priceBiggerThan == null && priceLessThan == null && yearBiggerThan == null && yearLessThan == null && genres == null) {
            totalCount = serverUtils.getSetBooks().size();
        }

        booksLogger.info("Total Books found for requested filters is {}", totalCount);

        long duration = System.currentTimeMillis() - startTime;
        requestLogger.debug("request #{} duration: {}ms", requestNumber, duration);

        return ResponseEntity.ok().body(new ServerResponse(totalCount, null));
    }
    @GetMapping("/books")
    public ResponseEntity<ServerResponse> getBooksData(@RequestParam(required = false) String author,
                                                       @RequestParam(name = "price-bigger-than", required = false) Integer priceBiggerThan,
                                                       @RequestParam(name = "price-less-than", required = false) Integer priceLessThan,
                                                       @RequestParam(name = "year-bigger-than", required = false) Integer yearBiggerThan,
                                                       @RequestParam(name = "year-less-than", required = false) Integer yearLessThan,
                                                       @RequestParam(required = false) String genres) {
        int requestNumber = incrementRequestCounter();
        long startTime = System.currentTimeMillis();

        requestLogger.info("Incoming request | #{} | resource: /books | HTTP Verb GET", requestNumber);

        if (genres != null) {
            for (String genre : genres.split(",")) {
                if (Genre.getGenre(genre) == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ServerResponse(null, "Error: Bad Request"));
                }
            }
        }

        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : serverUtils.getSetBooks()) {
            if ((author == null || book.getAuthor().equalsIgnoreCase(author))
                    && (priceBiggerThan == null || book.getPrice() >= priceBiggerThan)
                    && (priceLessThan == null || book.getPrice() <= priceLessThan)
                    && (yearBiggerThan == null || book.getYear() >= yearBiggerThan)
                    && (yearLessThan == null || book.getYear() <= yearLessThan)
                    && (genres == null || containsAnyGenre(book, genres))) {
                filteredBooks.add(book);
            }
        }

        if (author == null && priceBiggerThan == null && priceLessThan == null && yearBiggerThan == null && yearLessThan == null && genres == null) {
            filteredBooks.clear();
            filteredBooks.addAll(serverUtils.getSetBooks());
        }

        filteredBooks.sort(Comparator.comparing(book -> book.getTitle().toLowerCase()));

        booksLogger.info("Total Books found for requested filters is {}", filteredBooks.size());

        long duration = System.currentTimeMillis() - startTime;
        requestLogger.debug("request #{} duration: {}ms", requestNumber, duration);

        return ResponseEntity.ok().body(new ServerResponse(filteredBooks, null));
    }
    @GetMapping("/book")
    public ResponseEntity<ServerResponse> getSingleBookData(@RequestParam Integer id) {
        int requestNumber = incrementRequestCounter();
        long startTime = System.currentTimeMillis();

        requestLogger.info("Incoming request | #{} | resource: /book | HTTP Verb GET", requestNumber);

        Book optionalBook = null;
        for (Book book : serverUtils.getSetBooks()) {
            if (book.getId() == id) {
                optionalBook = book;
            }
        }

        if (optionalBook == null) {
            String errorMessage = "Error: no such Book with id " + id;
            booksLogger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ServerResponse(null, errorMessage));
        } else {
            booksLogger.debug("Fetching book id {} details", id);
            long duration = System.currentTimeMillis() - startTime;
            requestLogger.debug("request #{} duration: {}ms", requestNumber, duration);

            return ResponseEntity.ok().body(new ServerResponse(optionalBook, null));
        }
    }
    @PutMapping("/book")
    public ResponseEntity<ServerResponse> updateBooksPrice(@RequestParam Integer id, @RequestParam Integer price) {
        int requestNumber = incrementRequestCounter();
        long startTime = System.currentTimeMillis();

        requestLogger.info("Incoming request | #{} | resource: /book | HTTP Verb PUT", requestNumber);

        Book optionalBook = null;
        for (Book book : serverUtils.getSetBooks()) {
            if (book.getId() == id) {
                optionalBook = book;
            }
        }

        if (optionalBook == null) {
            String errorMessage = "Error: no such Book with id " + id;
            booksLogger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ServerResponse(null, errorMessage));
        } else if (price <= 0) {
            String errorMessage = "Error: price update for book [" + id + "] must be a positive integer";
            booksLogger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ServerResponse(null, errorMessage));
        } else {
            booksLogger.info("Update Book id [{}] price to {}", optionalBook.getId(), price);
            booksLogger.debug("Book [{}] price change: {} --> {}", optionalBook.getId(), optionalBook.getPrice(), price);
            optionalBook.setPrice(price);
            long duration = System.currentTimeMillis() - startTime;
            requestLogger.debug("request #{} duration: {}ms", requestNumber, duration);

            return ResponseEntity.ok().body(new ServerResponse(optionalBook, null));
        }
    }
    @DeleteMapping("/book")
    public ResponseEntity<ServerResponse> deleteSingleBook(@RequestParam Integer id) {
        int requestNumber = incrementRequestCounter();
        long startTime = System.currentTimeMillis();

        requestLogger.info("Incoming request | #{} | resource: /book | HTTP Verb DELETE", requestNumber);

        Book optionalBook = null;
        for (Book book : serverUtils.getSetBooks()) {
            if (book.getId() == id) {
                optionalBook = book;
            }
        }

        if (optionalBook == null) {
            String errorMessage = "Error: no such Book with id " + id;
            booksLogger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ServerResponse(null, errorMessage));
        } else {
            serverUtils.getSetBooks().remove(optionalBook);
            booksLogger.info("Removing book [{}]", optionalBook.getTitle());
            booksLogger.debug("After removing book [{}] id: [{}] there are {} books in the system", optionalBook.getTitle(), optionalBook.getId(), serverUtils.getSetBooks().size());
            long duration = System.currentTimeMillis() - startTime;
            requestLogger.debug("request #{} duration: {}ms", requestNumber, duration);

            return ResponseEntity.ok().body(new ServerResponse(null, null));
        }
    }
    private boolean containsAnyGenre(Book book, String genres) {
        for (String genre : genres.split(",")) {
            if (book.getGenres().contains(Genre.getGenre(genre))) {
                return true;
            }
        }
        return false;
    }
    @GetMapping("/logs/level")
    public ResponseEntity<String> getLoggerLevel(@RequestParam(name = "logger-name") String loggerName) {
        try {
            String level = getLogLevel(loggerName);
            return ResponseEntity.ok(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            //booksLogger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
    @PutMapping("/logs/level")
    public ResponseEntity<String> setLoggerLevel(@RequestParam(name = "logger-name") String loggerName,
                                                 @RequestParam(name = "logger-level") String loggerLevel) {
        try {
            ServerLogger.setLogLevel(loggerName, loggerLevel);
            return ResponseEntity.ok(loggerLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            //booksLogger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}
