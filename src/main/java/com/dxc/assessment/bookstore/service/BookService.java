package com.dxc.assessment.bookstore.service;

import com.dxc.assessment.bookstore.dto.request.BookRequest;
import com.dxc.assessment.bookstore.dto.response.BookResponse;

import java.util.List;

public interface BookService {
    BookResponse addBook(BookRequest request);
    BookResponse updateBook(String isbn, BookRequest request);
    List<BookResponse> findBooks(String title, String author);
    void deleteBook(String isbn);
}