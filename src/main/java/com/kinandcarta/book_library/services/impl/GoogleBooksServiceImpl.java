package com.kinandcarta.book_library.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kinandcarta.book_library.entities.Book;
import com.kinandcarta.book_library.entities.BookItem;
import com.kinandcarta.book_library.projections.bookAPI.BookApiResponse;
import com.kinandcarta.book_library.projections.bookAPI.VolumeInfo;
import com.kinandcarta.book_library.services.GoogleBooksService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleBooksServiceImpl implements GoogleBooksService {

    @Value("${google.books.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GoogleBooksServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String searchBooks(String isbn) {

        String filterType = "full";

        String url = "https://www.googleapis.com/books/v1/volumes?q=+isbn:" + isbn + "&key=" + apiKey;

        return restTemplate.getForObject(url, String.class);
    }

    @Override
    public Book convertToBook(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            System.out.println("Received JSON: " + json); // Debug statement

            BookApiResponse bookApiResponse = objectMapper.readValue(json, BookApiResponse.class);


//            List<com.kinandcarta.book_library.projections.bookAPI.BookItem> bookItems = bookApiResponse.getItems();
//            for (BookItem bookItem : bookItems) {
//                VolumeInfo volumeInfo = bookItem.getVolumeInfo();
//                List<String> authors = volumeInfo.getAuthors();
//
//                // Now you can work with the authors list
//                for (String author : authors) {
//                    System.out.println("Author: " + author);
//                }
//            }


            return null;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }


    private Book convertToBook1(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            //todo: Implement with ObjectMapper

            JsonNode jsonNode = objectMapper.readTree(json);

            JsonNode volumeInfo = jsonNode.path("items").get(0).path("volumeInfo");

            //todo: retrieve isbn from industryIdentifiers
            JsonNode industryIdentifiers = volumeInfo.path("industryIdentifiers");
            String isbn13 = null;
            for (JsonNode identifier : industryIdentifiers) {
                if ("ISBN_13".equals(identifier.path("type").asText())) {
                    isbn13 = identifier.path("identifier").asText();
                    break;
                }
            }

            //todo: retrieve title
            String title = volumeInfo.path("title").asText();

            //todo: retrieve authors
            JsonNode authorsNode = volumeInfo.path("authors");
            List<String> authors = new ArrayList<>();
            if (authorsNode.isArray()) {
                for (JsonNode author : authorsNode) {
                    authors.add(author.asText());
                }
            }

            System.out.println(title);
            authors.forEach(System.out::println);
            System.out.println(isbn13); // Print the ISBN-13 for verification


            return null;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }
}
