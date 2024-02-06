package dev.adan.bookapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

//lets implement all logic here also
@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookId;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide book title!")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Please provide books director name!")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide books publisher name!")
    private String publisher;

    //this is not field/parameter/variable but it is collection of strings which takes bookId as foreign key and different table called book_cast is created
    @ElementCollection
    @CollectionTable(name = "book_cast")
    private Set<String> bookCast;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Please provide books poster name!")
    private String poster;
}
