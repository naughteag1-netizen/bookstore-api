package com.dxc.assessment.bookstore.service.impl;

import com.dxc.assessment.bookstore.dto.request.AuthorRequest;
import com.dxc.assessment.bookstore.dto.request.BookRequest;
import com.dxc.assessment.bookstore.dto.response.BookResponse;
import com.dxc.assessment.bookstore.entity.Author;
import com.dxc.assessment.bookstore.entity.Book;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    @Override
    public BookResponse addBook(BookRequest request) {

        String user = SecurityUtils.getCurrentUsername();

        log.info("AUDIT: user={} action=CREATE_BOOK isbn={}", user, request.getIsbn());

        Book book = bookMapper.toEntity(request);
        book.setAuthors(resolveAuthors(request.getAuthors()));

        Book saved = bookRepository.save(book);

        return bookMapper.toResponse(saved);
    }

    @Override
    public BookResponse updateBook(String isbn, BookRequest request) {

        String user = SecurityUtils.getCurrentUsername();

        log.info("AUDIT: user={} action=UPDATE_BOOK isbn={}", user, isbn);

        Book existing = bookRepository.findById(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        existing.setTitle(request.getTitle());
        existing.setYear(request.getYear());
        existing.setPrice(request.getPrice());
        existing.setGenre(request.getGenre());
        existing.setAuthors(resolveAuthors(request.getAuthors()));

        return bookMapper.toResponse(bookRepository.save(existing));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findBooks(String title, String author) {

        String user = SecurityUtils.getCurrentUsername();

        log.info("AUDIT: user={} action=SEARCH_BOOKS title={} author={}",
                user, title, author);

        List<Book> books;

        if (title != null && author != null) {
            books = bookRepository.findByTitleAndAuthor(title, author);
        } else if (title != null) {
            books = bookRepository.findByTitle(title);
        } else if (author != null) {
            books = bookRepository.findByAuthorName(author);
        } else {
            books = bookRepository.findAll();
        }

        return bookMapper.toResponseList(books);
    }

    @Override
    public void deleteBook(String isbn) {

        String user = SecurityUtils.getCurrentUsername();

        log.warn("AUDIT: user={} action=DELETE_BOOK isbn={}", user, isbn);

        if (!bookRepository.existsById(isbn)) {
            throw new ResourceNotFoundException("Book not found");
        }

        bookRepository.deleteById(isbn);
    }

    private List<Author> resolveAuthors(List<AuthorRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        return requests.stream()
                .map(req -> authorRepository.findByName(req.getName())
                        .orElseGet(() -> authorRepository.save(
                                new Author(null, req.getName(), req.getBirthday())
                        ))
                )
                .toList();
    }
}