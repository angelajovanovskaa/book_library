package com.kinandcarta.book_library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record SecretKey(String secret_key) {
}
