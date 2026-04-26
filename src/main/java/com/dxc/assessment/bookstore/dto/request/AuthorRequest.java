package com.dxc.assessment.bookstore.dto.request;

import lombok.Data;

@Data
public class AuthorRequest {
    private String name;
    private String birthday;//YYYY-MM-DD
}
