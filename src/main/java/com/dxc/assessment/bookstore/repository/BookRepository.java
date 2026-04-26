package com.dxc.assessment.bookstore.repository;

import com.dxc.assessment.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    List<Book> findByTitle(String title);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.name = :name")
    List<Book> findByAuthorName(@Param("name") String name);

    @Query("""
        SELECT b FROM Book b JOIN b.authors a
        WHERE b.title = :title AND a.name = :name
    """)
    List<Book> findByTitleAndAuthor(
            @Param("title") String title,
            @Param("name") String name
    );
}
