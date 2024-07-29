package com.kinandcarta.book_library.services;

import java.util.List;

public interface BookAverageRatingCalculator {

    double getAverageRatingOnBook(List<Integer> reviews);
}
