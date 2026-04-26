package com.dxc.assessment.bookstore.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AuthorResponse {
    private String name;
    private LocalDate birthday;
}
