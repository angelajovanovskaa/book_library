package com.kinandcarta.book_library.dtos;

public interface RequestedBookLikeCounterClosedProjection {

    String getIsbn();

    String getTitle();

    Long getLikeCounter();
}
