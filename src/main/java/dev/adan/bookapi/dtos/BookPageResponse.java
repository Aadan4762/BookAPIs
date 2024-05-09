package dev.adan.bookapi.dtos;

import java.util.List;

public record BookPageResponse(List<BookDto> bookDtos,
                               int pageNumber,
                               int pageSize,
                               long totalElements,
                               int totalPages,
                               boolean isLastPage) {

}
