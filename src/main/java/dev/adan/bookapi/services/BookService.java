package dev.adan.bookapi.services;

import dev.adan.bookapi.dtos.BookDto;
import dev.adan.bookapi.dtos.BookPageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BookService {

    BookDto addBook(BookDto bookDto, MultipartFile file) throws IOException;

    BookDto getBook(Integer movieId);

    List<BookDto> getAllBook();

    BookDto updateBook(Integer movieId, BookDto bookDto, MultipartFile file) throws IOException;

    String deleteBook(Integer movieId) throws IOException;

    BookPageResponse getAllBooksWithPagination(Integer pageNumber, Integer pageSize);

    BookPageResponse getAllBooksWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
}
