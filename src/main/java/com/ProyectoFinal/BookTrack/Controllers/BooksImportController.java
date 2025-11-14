package com.ProyectoFinal.BookTrack.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ProyectoFinal.BookTrack.Services.BooksImportService;
import com.ProyectoFinal.BookTrack.dto.ImportReport;

@RestController
@RequestMapping("/api/admin/libros")
public class BooksImportController {

    private final BooksImportService booksImportService;
    private static final Logger log = LoggerFactory.getLogger(BooksImportController.class);

    public BooksImportController(BooksImportService booksImportService) {
        this.booksImportService = booksImportService;
    }

    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportReport> importCsv(@RequestPart("file") MultipartFile file) {
        log.info("Solicitud de importación CSV recibida. Nombre de archivo: {}", file != null ? file.getOriginalFilename() : "sin archivo");
        ImportReport report = booksImportService.importCsv(file);
        return ResponseEntity.ok(report);
    }
}
