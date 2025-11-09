package com.ProyectoFinal.BookTrack.Controllers;

import com.ProyectoFinal.BookTrack.Services.BooksImportService;
import com.ProyectoFinal.BookTrack.dto.ImportReport;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.http.*; 
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class BooksImportController {

    private static final Logger log = LoggerFactory.getLogger(BooksImportController.class);
    private final BooksImportService service;

    @PostMapping(value="/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportReport> importCsv(@RequestParam("file") MultipartFile file) {
        log.info("Import CSV start: size={} name={}", file.getSize(), file.getOriginalFilename());
        try {
            ImportReport rep = service.importCsv(file.getInputStream());
            log.info("Import CSV done: inserted={}, duplicates={}, errors={}",
                    rep.getInserted(), rep.getDuplicates(), rep.getErrors());
            return ResponseEntity.ok(rep);
        } catch (IllegalArgumentException e) {
            log.warn("Import CSV bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Import CSV error", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
