package com.dxc.assessment.bookstore.controller;

import com.dxc.assessment.bookstore.dto.request.BookRequest;
import com.dxc.assessment.bookstore.dto.response.BookResponse;
import com.dxc.assessment.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/add")
    public ResponseEntity<BookResponse> addBook(@RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.addBook(request));
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable String isbn,
            @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(isbn, request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> findBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {
        return ResponseEntity.ok(bookService.findBooks(title, author));
    }

    @DeleteMapping("/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }
}
