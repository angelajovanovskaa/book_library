package com.kinandcarta.book_library.projections;

public interface RequestedBookLikeCounterClosedProjection {

    String getIsbn();

    String getTitle();

    Long getLikeCounter();
}
