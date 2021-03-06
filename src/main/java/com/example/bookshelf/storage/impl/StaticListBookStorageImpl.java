package com.example.bookshelf.storage.impl;

import com.example.bookshelf.storage.BookStorage;
import com.example.bookshelf.type.Book;

import java.util.ArrayList;
import java.util.List;

public class StaticListBookStorageImpl implements BookStorage {
    private static List<Book> bookStorage = new ArrayList<>();

    public List<Book> getBookStorage() {
        return bookStorage;
    }

    @Override
    public Book getBook(long id) {
        for (Book book : bookStorage) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookStorage;
    }

    @Override
    public long addBook(Book book) {
        bookStorage.add(book);
        return book.getId();
    }

    @Override
    public void deleteBook(long id) {

    }

    public void clearList() {
        bookStorage.clear();
    }
}
