package com.dxc.assessment.bookstore.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BookRequest {
    private String isbn;
    private String title;
    private Integer year;
    private Double price;
    private String genre;
    private List<AuthorRequest> authors;
}