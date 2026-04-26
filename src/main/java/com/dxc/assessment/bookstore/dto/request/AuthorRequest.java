package com.dxc.assessment.bookstore.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthorRequest {
    private String name;
    private LocalDate birthday;
}
