package com.dxc.assessment.bookstore.dto.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {
    @NotBlank
    private String isbn;

    @NotBlank
    private String title;

    private Integer year;
    private Double price;
    private String genre;

    @NotNull
    private List<AuthorDto> authors;
}
