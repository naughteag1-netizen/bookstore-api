package com.dxc.assessment.bookstore.mapper;

import com.dxc.assessment.bookstore.dto.request.BookRequest;
import com.dxc.assessment.bookstore.dto.response.BookResponse;
import com.dxc.assessment.bookstore.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "authors", ignore = true)
    Book toEntity(BookRequest request);

    BookResponse toResponse(Book book);

    List<BookResponse> toResponseList(List<Book> books);
}