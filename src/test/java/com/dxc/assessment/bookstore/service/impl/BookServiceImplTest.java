package com.dxc.assessment.bookstore.service.impl;

import com.dxc.assessment.bookstore.dto.request.AuthorRequest;
import com.dxc.assessment.bookstore.dto.request.BookRequest;
import com.dxc.assessment.bookstore.dto.response.BookResponse;
import com.dxc.assessment.bookstore.entity.Book;
import com.dxc.assessment.bookstore.exception.BookDeletionException;
import com.dxc.assessment.bookstore.exception.ResourceNotFoundException;
import com.dxc.assessment.bookstore.mapper.BookMapper;
import com.dxc.assessment.bookstore.repository.AuthorRepository;
import com.dxc.assessment.bookstore.repository.BookRepository;
import com.dxc.assessment.bookstore.utils.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookMapper bookMapper;

    @Test
    void addBook_success() {
        BookRequest request = createBookRequest();

        Book bookEntity = new Book();
        Book saved = new Book();
        BookResponse response = new BookResponse();

        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUsername).thenReturn("user");

            when(bookRepository.existsById(request.getIsbn())).thenReturn(false);
            when(bookMapper.toEntity(request)).thenReturn(bookEntity);
            when(authorRepository.findByName(anyString())).thenReturn(Optional.empty());
            when(bookRepository.save(any(Book.class))).thenReturn(saved);
            when(bookMapper.toResponse(saved)).thenReturn(response);

            BookResponse result = bookService.addBook(request);

            assertNotNull(result);
            verify(bookRepository).save(any(Book.class));
        }
    }

    @Test
    void addBook_shouldThrow_whenIsbnExists() {
        BookRequest request = createBookRequest();

        when(bookRepository.existsById(request.getIsbn())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(request));
    }

    @Test
    void updateBook_success() {
        BookRequest request = createBookRequest();
        Book existing = new Book();
        Book saved = new Book();

        when(bookRepository.findById("123")).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(saved);
        when(bookMapper.toResponse(saved)).thenReturn(new BookResponse());

        BookResponse result = bookService.updateBook("123", request);

        assertNotNull(result);
        verify(bookRepository).save(existing);
    }

    @Test
    void updateBook_shouldThrow_whenNotFound() {
        when(bookRepository.findById("123")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.updateBook("123", new BookRequest()));
    }

    @Test
    void findBooks_byTitleAndAuthor() {
        List<Book> books = List.of(new Book());
        List<BookResponse> responses = List.of(new BookResponse());

        when(bookRepository.findByTitleAndAuthor("Clean Code", "Robert")).thenReturn(books);
        when(bookMapper.toResponseList(books)).thenReturn(responses);

        List<BookResponse> result = bookService.findBooks("Clean Code", "Robert");

        assertEquals(1, result.size());
    }

    @Test
    void findBooks_shouldThrow_whenNoParams() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.findBooks(null, null));
    }

    @Test
    void findBooks_shouldThrow_whenEmpty() {
        when(bookRepository.findByTitle("x")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.findBooks("x", null));
    }

    @Test
    void deleteBook_success() {
        when(bookRepository.existsById("123")).thenReturn(true);

        bookService.deleteBook("123");

        verify(bookRepository).deleteById("123");
    }

    @Test
    void deleteBook_shouldThrow_whenNotFound() {
        when(bookRepository.existsById("123")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> bookService.deleteBook("123"));
    }

    @Test
    void deleteBook_shouldWrapException() {
        when(bookRepository.existsById("123")).thenReturn(true);
        doThrow(new RuntimeException("DB error"))
                .when(bookRepository).deleteById("123");

        assertThrows(BookDeletionException.class,
                () -> bookService.deleteBook("123"));
    }

    private BookRequest createBookRequest() {
        AuthorRequest author = new AuthorRequest();
        author.setName("Robert C. Martin");
        author.setBirthday("1952-12-05");

        BookRequest request = new BookRequest();
        request.setIsbn("1234567890");
        request.setTitle("Test Book");
        request.setYear(2021);
        request.setPrice(19.99);
        request.setGenre("Fiction");
        request.setAuthors(List.of(author));

        return request;
    }
}