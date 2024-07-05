package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.RecommendedBook;
import com.kinandcarta.book_library.projections.RecommendedBookDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface RecommendedBookService {
    List<RecommendedBookDTO> getAll();
    List<RecommendedBookDTO> getAllRecommendedBooks();
    List<RecommendedBookDTO> getAllPendingRecommendedBooks();
    RecommendedBookDTO getRecommendedBookById(UUID id);
    RecommendedBookDTO getRecommendedBookByISBN(String ISBN);
    RecommendedBookDTO getRecommendedBookByTitle(String ISBN);
    RecommendedBookDTO getFavoriteRecommendedBook();
    RecommendedBookDTO save(String bookISBN);
    RecommendedBookDTO delete(UUID recommendedBookId);
    RecommendedBookDTO setStatusToPendingPurchase(UUID recommendedBookId);
    RecommendedBookDTO setStatusToRecommendedBook(UUID recommendedBookId);
}