package com.dxc.assessment.bookstore.mapper;

import com.dxc.assessment.bookstore.dto.request.AuthorRequest;
import com.dxc.assessment.bookstore.dto.response.AuthorResponse;
import com.dxc.assessment.bookstore.entity.Author;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    Author toEntity(AuthorRequest request);

    AuthorResponse toResponse(Author author);

    List<Author> toEntityList(List<AuthorRequest> requests);

    List<AuthorResponse> toResponseList(List<Author> authors);
}
