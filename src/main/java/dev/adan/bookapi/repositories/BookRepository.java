package dev.adan.bookapi.repositories;

import dev.adan.bookapi.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
