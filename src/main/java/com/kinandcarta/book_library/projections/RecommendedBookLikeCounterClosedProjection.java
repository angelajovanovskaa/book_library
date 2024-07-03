package com.kinandcarta.book_library.projections;

public interface RecommendedBookLikeCounterClosedProjection {

    String getIsbn();

    String getTitle();

    Long getLikeCounter();
}
