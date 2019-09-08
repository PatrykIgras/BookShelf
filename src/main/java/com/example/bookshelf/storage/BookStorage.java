package com.example.bookshelf.storage;

import com.example.bookshelf.type.Book;

import java.util.List;

public interface BookStorage {
    Book getBook(long id);

    List<Book> getAllBooks();

    long addBook(Book book);

    void deleteBook(long id);
}
