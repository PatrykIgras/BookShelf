package com.example.bookshelf.storage.impl;

import com.example.bookshelf.storage.BookStorage;
import com.example.bookshelf.type.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresBookStorage implements BookStorage {
    private static final String POSTGRES_JDBC_URL = "jdbc:postgresql://localhost:5432/book_storage_db";
    private static final String POSTGRES_USER_NAME = "postgres";
    private static final String POSTGRES_USER_PASS = "password";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Server can't find postgresql Driver class: \n" + e);
        }
    }

    private Connection initializeConnection() {
        try {
            return DriverManager.getConnection(POSTGRES_JDBC_URL, POSTGRES_USER_NAME, POSTGRES_USER_PASS);
        } catch (SQLException e) {
            System.err.println("Server can't initialize data base connection: \n" + e);
            throw new RuntimeException("Server can't initialize data base connection");
        }
    }

    private void closeDataBaseConnection(Connection connection, Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }catch (SQLException e) {
            System.err.println("Server can't close connection for data base: \n" + e);
            throw new RuntimeException("Server can't close connection for data base");
        }

    }

        @Override
        public Book getBook (long id){
            final String sqlGetBook = "SELECT book_id, title, author, pages_sum, year_of_published, publishing_house " +
                    " FROM books WHERE book_id  = ?;";

            Connection connection = initializeConnection();
            PreparedStatement preparedStatement = null;

            try {
                preparedStatement = connection.prepareStatement(sqlGetBook);
                preparedStatement.setLong(1, id);

                ResultSet resultSet = preparedStatement.executeQuery();

                Book book = new Book();

                if (resultSet.next()){
                    book.setId(id);
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor(resultSet.getString("author"));
                    book.setPagesSum(resultSet.getInt("pages_sum"));
                    book.setYearOfPublished(resultSet.getInt("year_of_published"));
                    book.setPublishingHouse(resultSet.getString("publishing_house"));

                    return book;
                }
                return null;

            } catch (SQLException e) {
                System.err.println("Error while execute sql querry: \n" + e);
                throw new RuntimeException("Error while execute sql querry");
            }finally {
                closeDataBaseConnection(connection, preparedStatement);
            }
        }

        @Override
        public List<Book> getAllBooks () {
            final String sqlGetAll = "SELECT * FROM books;";

            Connection connection = initializeConnection();
            Statement statement = null;

            List<Book> bookList = new ArrayList<>();

            try {
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sqlGetAll);


                while (resultSet.next()){
                    Book book = new Book();
                    book.setId(resultSet.getLong("book_id"));
                    book.setTitle(resultSet.getString("title"));
                    book.setAuthor(resultSet.getString("author"));
                    book.setPagesSum(resultSet.getInt("pages_sum"));
                    book.setYearOfPublished(resultSet.getInt("year_of_published"));
                    book.setPublishingHouse(resultSet.getString("publishing_house"));

                    bookList.add(book);
                }
            } catch (SQLException e) {
                System.err.println("Error while execute sql querry: \n" + e);
                throw new RuntimeException("Error while execute sql querry");
            }finally {
                closeDataBaseConnection(connection, statement);
            }
            return bookList;
        }

        @Override
        public void addBook (Book book){
            final String sqlAddBook = ("INSERT INTO books (book_id, title, author, pages_sum, year_of_published, publishing_house)" +
                    "VALUES (?, ?, ?, ?, ?, ?);");

            Connection connection = initializeConnection();
            PreparedStatement preparedStatement = null;

            try {
                preparedStatement = connection.prepareStatement(sqlAddBook);

                preparedStatement.setLong(1, book.getId());
                preparedStatement.setString(2, book.getTitle());
                preparedStatement.setString(3, book.getAuthor());
                preparedStatement.setInt(4, book.getPagesSum());
                preparedStatement.setInt(5, book.getYearOfPublished());
                preparedStatement.setString(6, book.getPublishingHouse());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Error while execute sql querry: \n" + e);
                throw new RuntimeException("Error while execute sql querry");
            }finally {
                closeDataBaseConnection(connection, preparedStatement);
            }
        }
    }
