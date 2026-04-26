package com.dxc.assessment.bookstore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookResponse {
    private String isbn;
    private String title;
    private Integer year;
    private Double price;
    private String genre;
    private List<AuthorResponse> authors;
}
