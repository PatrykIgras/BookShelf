package com.example.bookshelf.storage.impl;

import com.example.bookshelf.type.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StaticListBookStorageImplTest {
    Book book = new Book();
    StaticListBookStorageImpl storage = new StaticListBookStorageImpl();

    @BeforeEach
    void setData() {
        book.setId(1);
        book.setAuthor("XYZ");
        book.setTitle("XXX");
        book.setPagesSum(100);
        book.setPublishingHouse("ZZZ");
        book.setYearOfPublished(2019);
    }

    @Test
    void shouldAddBookToList() {
        List<Book> list = new ArrayList<>();
        list.add(book);
        storage.addBook(book);
        assertEquals(list, storage.getBookStorage());
        storage.getBookStorage().clear();
    }

    @Test
    void shouldGetBookFromStorage() {
        storage.addBook(book);
        assertAll(
                () -> assertEquals(book, storage.getBook(1)),
                () -> assertEquals(null, storage.getBook(2))
        );
        storage.getBookStorage().clear();
    }

    @Test
    void getAllBooks() {
        List<Book> list = new ArrayList<>();
        Book book2 = book;
        storage.addBook(book);
        storage.addBook(book2);
        list.add(book);
        list.add(book2);

        assertEquals(list, storage.getBookStorage());
        storage.getBookStorage().clear();
    }
}