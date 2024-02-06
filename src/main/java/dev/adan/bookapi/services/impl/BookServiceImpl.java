package dev.adan.bookapi.services.impl;

import dev.adan.bookapi.dtos.BookDto;
import dev.adan.bookapi.dtos.BookPageResponse;
import dev.adan.bookapi.entities.Book;
import dev.adan.bookapi.exceptions.FileExistsException;
import dev.adan.bookapi.exceptions.BookNotFoundException;
import dev.adan.bookapi.repositories.BookRepository;
import dev.adan.bookapi.services.FileService;
import dev.adan.bookapi.services.BookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final FileService fileService;

    private final BookRepository bookRepository;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public BookServiceImpl(FileService fileService, BookRepository bookRepository) {
        this.fileService = fileService;
        this.bookRepository = bookRepository;
    }

    @Override
    public BookDto addBook(BookDto bookDto, MultipartFile file) throws IOException {
        // 1. upload file -> returns file name
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists! Please give another file!");
        }
        String uploadedFileName = fileService.uploadFile(path, file);

        // 2. set poster value to file name
        bookDto.setPoster(uploadedFileName);

        // 3. convert to Book object
        Book book = new Book(
                null,
                bookDto.getTitle(),
                bookDto.getDirector(),
                bookDto.getPublisher(),
                bookDto.getBookCast(),
                bookDto.getReleaseYear(),
                bookDto.getPoster()
        );

        // 4. save Book object to DB -> return Book object
        Book savedBook = bookRepository.save(book);

        // 6. Get base url and construct poster's Url
        var posterUrl = baseUrl + "/file/" + uploadedFileName;

        // 5. convert to BookDto object, and return this object
        var responseObj = new BookDto(
                savedBook.getBookId(),
                savedBook.getTitle(),
                savedBook.getDirector(),
                savedBook.getPublisher(),
                savedBook.getBookCast(),
                savedBook.getReleaseYear(),
                savedBook.getPoster(),
                posterUrl
        );

        return responseObj;
    }

    @Override
    public BookDto getBook(Integer bookId) {
        // 1. check if any record exists in DB with given 'bookId'
        // 2. get the data if exists, else throw/handle exception
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id = " + bookId));

        // 3. generate 'posterUrl' with help of value from 'poster' field
        var posterUrl = baseUrl + "/file/" + book.getPoster();

        // 4. map the data to BookDto object and return the object
        BookDto response = new BookDto(
                book.getBookId(),
                book.getTitle(),
                book.getDirector(),
                book.getPublisher(),
                book.getBookCast(),
                book.getReleaseYear(),
                book.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public List<BookDto> getAllBook() {
        // 1. get all data from DB
        List<Book> books = bookRepository.findAll();

        List<BookDto> bookDtos = new ArrayList<>();

        // 2.1 iterate the list, generate posterUrl for each data,
        // 2.2 and map to BookDto object -> return the object
        for(Book book : books) {
            var posterUrl = baseUrl + "/file/" + book.getPoster();
            BookDto bookDto = new BookDto(
                    book.getBookId(),
                    book.getTitle(),
                    book.getDirector(),
                    book.getPublisher(),
                    book.getBookCast(),
                    book.getReleaseYear(),
                    book.getPoster(),
                    posterUrl
            );
            bookDtos.add(bookDto);
        }

        return bookDtos;
    }

    @Override
    public BookDto updateBook(Integer bookId, BookDto bookDto, MultipartFile file) throws IOException {
        // 1. check if movie exists in DB, and fetch the data if exists
        Book mv = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id = " + bookId));

        // 2. check if file is null, then no need to do anything,
        // else upload file, and replace existing
        String fileName = mv.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        // 3. set poster's value according to step 2
        bookDto.setPoster(fileName);

        // 4. map to Book object
        Book book = new Book(
                mv.getBookId(),
                bookDto.getTitle(),
                bookDto.getDirector(),
                bookDto.getPublisher(),
                bookDto.getBookCast(),
                bookDto.getReleaseYear(),
                bookDto.getPoster()
        );

        // 5. save the Movie object -> return saved Book object
        Book updatedBook = bookRepository.save(book);

        // 6. generate posterUrl
        var posterUrl = baseUrl + "/file/" + updatedBook.getPoster();

        // 7. map to BookDto object and return it
        var responseObj = new BookDto(
                updatedBook.getBookId(),
                updatedBook.getTitle(),
                updatedBook.getDirector(),
                updatedBook.getPublisher(),
                updatedBook.getBookCast(),
                updatedBook.getReleaseYear(),
                updatedBook.getPoster(),
                posterUrl
        );

        return responseObj;
    }

    @Override
    public String deleteBook(Integer bookId) throws IOException {
        // 1. check if book record exists in DB with given movieID
        Book bk = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with id = " + bookId));
        Integer id = bk.getBookId();
        // 2. if exists delete the movie object in DB, and file associated with in file path
        Files.deleteIfExists(Paths.get(path + File.separator + bk.getPoster()));
        bookRepository.delete(bk);

        return "Book object deleted with id = " + id;
    }

    @Override
    public BookPageResponse getAllBooksWithPagination(Integer pageNumber, Integer pageSize) {
        // 1. create Pageable object
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // 2. Get the data from DB
        Page<Book> bookPages = bookRepository.findAll(pageable);
        List<Book> books = bookPages.getContent();

        // 3. Convert to BookDto object and return it
        List<BookDto> bookDtos = new ArrayList<>();
        for (Book book : books) {
            var posterUrl = baseUrl + "/file/" + book.getPoster();
            BookDto bookDto = new BookDto(
                    book.getBookId(),
                    book.getTitle(),
                    book.getDirector(),
                    book.getPublisher(),
                    book.getBookCast(),
                    book.getReleaseYear(),
                    book.getPoster(),
                    posterUrl
            );
            bookDtos.add(bookDto);
        }

        return new BookPageResponse(bookDtos,
                                    pageNumber,
                                    pageSize,
                                    bookPages.getTotalElements(),
                                    bookPages.getTotalPages(),
                                    bookPages.isLast());
    }

    @Override
    public BookPageResponse getAllBooksWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                                 String sortBy, String sortDir) {
        // 1. create Sort and Pageable object
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // 2. Get the data from DB
        Page<Book> moviePages = bookRepository.findAll(pageable);
        List<Book> books = moviePages.getContent();

        // 3. Convert to BookDto object and return it
        List<BookDto> bookDtos = new ArrayList<>();
        for (Book book : books) {
            var posterUrl = baseUrl + "/file/" + book.getPoster();
            BookDto bookDto = new BookDto(
                    book.getBookId(),
                    book.getTitle(),
                    book.getDirector(),
                    book.getPublisher(),
                    book.getBookCast(),
                    book.getReleaseYear(),
                    book.getPoster(),
                    posterUrl
            );
            bookDtos.add(bookDto);
        }

        return new BookPageResponse(bookDtos,
                                    pageNumber,
                                    pageSize,
                                    moviePages.getTotalElements(),
                                    moviePages.getTotalPages(),
                                    moviePages.isLast());
    }
}
