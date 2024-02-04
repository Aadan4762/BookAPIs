package dev.adan.bookapi.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Integer bookId;

    @NotBlank(message = "Please provide movie title!")
    private String title;

    @NotBlank(message = "Please provide movie's director name!")
    private String director;

    @NotBlank(message = "Please provide movie's studio name!")
    private String studio;

    private Set<String> bookCast;

    private Integer releaseYear;

    private String poster;

    private String posterUrl;
}
