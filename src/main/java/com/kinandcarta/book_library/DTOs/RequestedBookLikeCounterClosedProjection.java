package com.kinandcarta.book_library.DTOs;

public interface RequestedBookLikeCounterClosedProjection {

    String getIsbn();

    String getTitle();

    Long getLikeCounter();
}
