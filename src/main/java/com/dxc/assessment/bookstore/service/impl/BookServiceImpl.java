package com.dxc.assessment.bookstore.service.impl;

import com.dxc.assessment.bookstore.dto.request.AuthorRequest;
import com.dxc.assessment.bookstore.dto.request.BookRequest;
import com.dxc.assessment.bookstore.dto.response.BookResponse;
import com.dxc.assessment.bookstore.entity.Author;
import com.dxc.assessment.bookstore.entity.Book;
import com.dxc.assessment.bookstore.exception.BookDeletionException;
import com.dxc.assessment.bookstore.exception.ResourceNotFoundException;
import com.dxc.assessment.bookstore.mapper.BookMapper;
import com.dxc.assessment.bookstore.repository.AuthorRepository;
import com.dxc.assessment.bookstore.repository.BookRepository;
import com.dxc.assessment.bookstore.service.BookService;
import com.dxc.assessment.bookstore.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    private static final String BOOK_NOT_FOUND = "Book not found";

    @Override
    public BookResponse addBook(BookRequest request) {
        validateBookRequest(request);

        if (bookRepository.existsById(request.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + request.getIsbn() + " already exists");
        }

        String user = getCurrentUsername();
        log.info("AUDIT: user={} action=CREATE_BOOK isbn={}", user, request.getIsbn());

        Book book = bookMapper.toEntity(request);
        book.setAuthors(resolveAuthors(request.getAuthors()));

        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    @Override
    public BookResponse updateBook(String isbn, BookRequest request) {
        validateBookRequestForUpdate(request);

        Book existing = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException(BOOK_NOT_FOUND));

        String user = getCurrentUsername();
        log.info("AUDIT: user={} action=UPDATE_BOOK isbn={}", user, isbn);

        // Only update non-null fields to avoid overwriting with nulls
        if (StringUtils.hasText(request.getTitle())) {
            existing.setTitle(request.getTitle());
        }
        if (request.getYear() != null) {
            existing.setYear(request.getYear());
        }
        if (request.getPrice() != null && request.getPrice() > 0) {
            existing.setPrice(request.getPrice());
        }
        if (StringUtils.hasText(request.getGenre())) {
            existing.setGenre(request.getGenre());
        }
        if (request.getAuthors() != null) {
            existing.setAuthors(resolveAuthors(request.getAuthors()));
        }

        // Only save if changes were made (optional optimization)
        return bookMapper.toResponse(bookRepository.save(existing));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findBooks(String title, String author) {

        String user = getCurrentUsername();
        log.info("AUDIT: user={} action=SEARCH_BOOKS title={} author={}", user, title, author);

        List<Book> books;

        boolean hasTitle = StringUtils.hasText(title);
        boolean hasAuthor = StringUtils.hasText(author);

        if (hasTitle && hasAuthor) {
            books = bookRepository.findByTitleAndAuthor(title, author);
        } else if (hasTitle) {
            books = bookRepository.findByTitle(title);
        } else if (hasAuthor) {
            books = bookRepository.findByAuthorName(author);
        } else {
            throw new IllegalArgumentException("At least title or author must be provided");
        }

        if (books.isEmpty()) {
            throw new ResourceNotFoundException("No books found matching given criteria");
        }

        return bookMapper.toResponseList(books);
    }

    @Override
    public void deleteBook(String isbn) {
        String user = getCurrentUsername();
        log.warn("AUDIT: user={} action=DELETE_BOOK isbn={}", user, isbn);

        if (StringUtils.hasText(isbn) && !bookRepository.existsById(isbn)) {
            throw new ResourceNotFoundException(BOOK_NOT_FOUND);
        }

        try {
            bookRepository.deleteById(isbn);
        } catch (Exception e) {
            log.error("Failed to delete book with ISBN {}: {}", isbn, e.getMessage());
            throw new BookDeletionException("Unable to delete book due to data constraints", e);
        }
    }

    private List<Author> resolveAuthors(List<AuthorRequest> requests) {
        if (CollectionUtils.isEmpty(requests)) {
            throw new IllegalArgumentException("At least one author is required");
        }

        return requests.stream()
                .filter(Objects::nonNull)
                .filter(req -> StringUtils.hasText(req.getName())) // Validate name
                .map(req -> authorRepository.findByName(req.getName())
                        .orElseGet(() -> authorRepository.save(
                                new Author(null, req.getName(), req.getBirthday())
                        ))
                )
                .toList();
    }

    private void validateBookRequest(BookRequest request) {
        if (request == null || !StringUtils.hasText(request.getIsbn()) || !StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("ISBN and title are required");
        }
        if (request.getPrice() != null && request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }

    private void validateBookRequestForUpdate(BookRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        // Allow partial updates, so less strict validation
    }

    private String getCurrentUsername() {
        String username = SecurityUtils.getCurrentUsername();
        return username != null ? username : "anonymous";
    }
}