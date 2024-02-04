package dev.adan.bookapi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.adan.bookapi.services.BookService;
import dev.adan.bookapi.dtos.BookDto;
import dev.adan.bookapi.dtos.BookPageResponse;
import dev.adan.bookapi.exceptions.EmptyFileException;
import dev.adan.bookapi.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

   // @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-book")
    public ResponseEntity<BookDto> addBookHandler(@RequestPart String bookDto,
                                                   @RequestPart MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new EmptyFileException("File cannot be empty, please send a file!");
        BookDto obj = getBookDto(bookDto);
        return new ResponseEntity<>(bookService.addBook(obj, file), HttpStatus.CREATED);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDto> getBookHandler(@PathVariable Integer bookId) {
        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookDto>> getBooksHandler() {
        return ResponseEntity.ok(bookService.getAllBook());
    }

    @PutMapping("/update-book/{bookId}")
    public ResponseEntity<BookDto> updateBookHandler(@PathVariable Integer bookId,
                                                      @RequestPart MultipartFile file,
                                                      @RequestPart String bookDtoObj) throws IOException {
        if (file.isEmpty()) file = null;
        BookDto bookDto = getBookDto(bookDtoObj);
        return ResponseEntity.ok(bookService.updateBook(bookId, bookDto, file));
    }

    @DeleteMapping("/delete/{bookId}")
    public ResponseEntity<String> deleteBookHandler(@PathVariable Integer bookId) throws IOException {
        return ResponseEntity.ok(bookService.deleteBook(bookId));
    }

    @GetMapping("/allBooks")
    public ResponseEntity<BookPageResponse> getAllBooksWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
    ) {
        return ResponseEntity.ok(bookService.getAllBooksWithPagination(pageNumber, pageSize));
    }

    @GetMapping("/allBooksSort")
    public ResponseEntity<BookPageResponse> getAllBooksWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String sortDir
    ) {
        return ResponseEntity.ok(bookService.getAllBooksWithPaginationAndSorting(pageNumber, pageSize, sortBy, sortDir));
    }

    // Convert RequestPart data in String to JSON object mapped to BookDto class
    private BookDto getBookDto(String bookObj) {
        BookDto bookDto = new BookDto();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            bookDto = objectMapper.readValue(bookObj, BookDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return bookDto;
    }
}
