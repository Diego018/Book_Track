package com.ProyectoFinal.BookTrack.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ProyectoFinal.BookTrack.Services.BooksImportService;
import com.ProyectoFinal.BookTrack.dto.ImportReport;

import lombok.RequiredArgsConstructor;

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
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            ImportReport rep = service.importCsv(file.getInputStream());
            log.info("Import CSV done: inserted={}, duplicates={}, errors={}",
                    rep.getInserted(), rep.getDuplicates(), rep.getErrors());

            // Si no se insertó nada y tampoco hay duplicados => todas las filas inválidas
            if (rep.getInserted() == 0 && rep.getDuplicates() == 0) {
                return ResponseEntity.badRequest().body(rep);
            }

            return ResponseEntity.ok(rep);
        } catch (IllegalArgumentException e) {
            log.warn("Import CSV bad request: {}", e.getMessage());
            // Cabecera inválida u otro error tratado
            ImportReport r = new ImportReport();
            r.addError(0, e.getMessage());
            return ResponseEntity.badRequest().body(r);
        } catch (Exception e) {
            log.error("Import CSV error", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
