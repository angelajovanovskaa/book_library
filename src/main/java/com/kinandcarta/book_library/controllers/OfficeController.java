package com.kinandcarta.book_library.controllers;

import com.kinandcarta.book_library.dtos.OfficeResponseDTO;
import com.kinandcarta.book_library.services.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/offices")
public class OfficeController {

    private final OfficeService officeService;

    @GetMapping
    ResponseEntity<List<OfficeResponseDTO>> getOffices(){
        List<OfficeResponseDTO> response = officeService.getOffices();

        return ResponseEntity.ok(response);
    }
}
