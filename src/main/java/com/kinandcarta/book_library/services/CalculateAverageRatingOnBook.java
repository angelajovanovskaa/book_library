package com.kinandcarta.book_library.services;

import com.kinandcarta.book_library.entities.Review;

import java.util.List;

public interface CalculateAverageRatingOnBook {

    Double getAverageRatingOnBook(List<Integer> reviews);
}
