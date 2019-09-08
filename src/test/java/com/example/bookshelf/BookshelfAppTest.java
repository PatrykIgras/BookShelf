package com.example.bookshelf;

import com.example.bookshelf.storage.impl.PostgresBookStorage;
import com.example.bookshelf.type.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BookshelfAppTest {

    private static final String BOOK_1 = "{\"title\":\"Java. Kompendium programisty\", \"author\":\"Herbert Schildt\"," +
            "\"pagesSum\":1152, \"yearOfPublished\":2019,\"publishingHouse\":\"Helion\"}";
    private static final String BOOK_2 = "{\"title\":\"Python. Wprowadzenie\", \"author\":\"Mark Lutz\"," +
            "\"pagesSum\":1184, \"yearOfPublished\":2017,\"publishingHouse\":\"Helion\"}";

    private static final int APP_PORT = 8091;

    private BookshelfApp bookshelfApp;
    PostgresBookStorage postgresBookStorage = new PostgresBookStorage();

    @BeforeAll
    public static void beforeAll() {
        RestAssured.port = APP_PORT;
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        bookshelfApp = new BookshelfApp(APP_PORT);
    }

    @AfterEach
    public void afterEach() {
        bookshelfApp.stop();
        postgresBookStorage.clearDataBase();
    }

    private long addBook(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Book bookToAdd = null;

        try {
            bookToAdd = objectMapper.readValue(json, Book.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return postgresBookStorage.addBook(bookToAdd);
    }

    @Test
    public void AddMethod_correctBody_shouldReturnStatus200() {
        with().body(BOOK_1).when().post("/book/add").then().statusCode(200).body(startsWith("Book has been successfully added"));
    }

    @Test
    public void addMethod_fieldTypeMismatch_shouldReturnStatus500() {

        String bookWtithFieldTypeMismatch = "{\"title\":\"Python. Wprowadzenie\", \"author\":\"Mark Lutz\"," +
                "\"pagesSum\":\"1184 pages\", \"yearOfPublished\":2017,\"publishingHouse\":\"Helion\"}";

        with().body(bookWtithFieldTypeMismatch).when().post("/book/add").then().statusCode(500);
    }

    @Test
    public void addMethod_unexpectedField_shouldReturnStatus500() {
        with().body("{\"numberOfChapters\":10}").when().post("/book/add").then().statusCode(500);
    }

    @Test
    public void getMethod_correctBookIdParam_shouldReturnStatus200() {
        int id = (int) addBook(BOOK_1);
        addBook(BOOK_2);

        with().param("bookId", id)
                .when().get("/book/get")
                .then().statusCode(200)
                .body("id", equalTo(id))
                .body("title", equalTo("Java. Kompendium programisty"))
                .body("author", equalTo("Herbert Schildt"))
                .body("pagesSum", equalTo(1152))
                .body("yearOfPublished", equalTo(2019))
                .body("publishingHouse", equalTo("Helion"));
    }

    @Test
    public void getMethod_noBookIdPrameter_shouldReturnStatus400() {
        when().get("/book/get").then().statusCode(400).body(equalTo("Uncorrected request params"));
    }

    @Test
    public void getMethod_wrongTypeOfBookIdParameter_shouldReturnStatus400() {
        with().param("bookId", "abc").when().get("/book/get").then().statusCode(400).body(equalTo("Request param 'bookId' have to be a number"));
    }

    @Test
    public void getMethod_bookDoesNotExist_shouldReturnStatus404() {
        with().param("bookId", 12345).when().get("/book/get").then().statusCode(404);
    }

    @Test
    public void getAllMethod_0Books_shouldReturnStatus200() throws IOException {
        when().get("/book/getAll").then().statusCode(200).body("", hasSize(0));
    }

    @Test
    public void getAllMethod_1Book_shouldReturnStatus200() {
        int id = (int) addBook(BOOK_1);

        when().get("book/getAll").
                then().statusCode(200)
                .body("id", hasItem(id))
                .body("title", hasItem("Java. Kompendium programisty"))
                .body("author", hasItem("Herbert Schildt"))
                .body("pagesSum", hasItem(1152))
                .body("yearOfPublished", hasItem(2019))
                .body("publishingHouse", hasItem("Helion"));
    }

    @Test
    public void getAllMethod_2Books_shouldReturnStatus200() {
        int id1 = (int) addBook(BOOK_1);
        int id2 = (int) addBook(BOOK_2);

        when().get("book/getAll").
                then().statusCode(200)
                .body("", hasSize(2))
                .body("id", hasItems(id1, id2))
                .body("title", hasItems("Java. Kompendium programisty", "Python. Wprowadzenie"))
                .body("author", hasItems("Herbert Schildt", "Mark Lutz"))
                .body("pagesSum", hasItems(1152, 1184))
                .body("yearOfPublished", hasItems(2019, 2017))
                .body("publishingHouse", hasItem("Helion"));
    }

    @Test
    public void deleteBookMethod_correctParam_shouldReturnStatus200() {
        int id1 = (int) addBook(BOOK_1);
        int id2 = (int) addBook(BOOK_2);

        with().param("bookId", id1)
                .when().delete("/book/delete")
                .then().statusCode(200)
                .body(startsWith("Book has been successfully removed"));
    }

    @Test
    public void deleteBookMethod_bookDoesNotExist_shouldReturnStatus404(){
        int id2 = (int) addBook(BOOK_2);

        with().param("bookId", -1)
                .when().delete("/book/delete")
                .then().statusCode(404)
                .body(startsWith("Data Base not have book with request id"));
    }

    @Test
    public void deleteBookMethod_uncorrectParam_shouldReturnStatus400(){
        int id2 = (int) addBook(BOOK_2);

        with().param("bookId", "text")
                .when().delete("/book/delete")
                .then().statusCode(400)
                .body(startsWith("Request param 'bookId' have to be a number"));
    }

    @Test
    public void deleteBookMethod_noParamInRequest_shouldReturnStatus400(){
        int id2 = (int) addBook(BOOK_2);

        with().when().delete("/book/delete")
                .then().statusCode(400)
                .body(startsWith("Uncorrected request params"));
    }
}