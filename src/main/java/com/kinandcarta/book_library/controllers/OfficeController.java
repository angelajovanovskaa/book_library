package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.OfficeResponseDTO;
import com.kinandcarta.book_library.services.OfficeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offices")
@Validated
public class OfficeController {

    private final OfficeQueryService officeQueryService;

    @GetMapping
    ResponseEntity<List<OfficeResponseDTO>> getOffices(){
        List<OfficeResponseDTO> response = officeQueryService.getOffices();

        return ResponseEntity.ok(response);
    }
}
