package com.ProyectoFinal.BookTrack.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.ProyectoFinal.BookTrack.Repositories.LibroRepository;
import com.ProyectoFinal.BookTrack.dto.CrearLibroRequest;
import com.ProyectoFinal.BookTrack.dto.ImportReport;
import com.ProyectoFinal.BookTrack.exception.BadRequestException;

@Service
public class BooksImportService {

    private static final Logger log = LoggerFactory.getLogger(BooksImportService.class);

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .setIgnoreEmptyLines(true)
            .build();

    private final LibroService libroService;
    private final LibroRepository libroRepository;

    public BooksImportService(LibroService libroService, LibroRepository libroRepository) {
        this.libroService = libroService;
        this.libroRepository = libroRepository;
    }

    @Transactional
    public ImportReport importCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Debes adjuntar un archivo CSV para importar.");
        }

        try (CSVParser parser = CSV_FORMAT.parse(new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)))) {
            ImportReport report = new ImportReport();
            Set<String> processedKeys = new HashSet<>();
            log.info("Iniciando importación CSV de libros: {} bytes", file.getSize());

            for (CSVRecord record : parser) {
                long rowNumber = record.getRecordNumber();
                try {
                    CrearLibroRequest request = mapRecord(record);
                    String duplicateKey = buildKey(request);
                    boolean alreadyProcessed = processedKeys.contains(duplicateKey)
                            || libroRepository.existsByTituloIgnoreCaseAndAutorIgnoreCase(request.getTitulo(), request.getAutor());

                    if (alreadyProcessed) {
                        report.incrementDuplicates();
                        log.debug("Libro duplicado detectado en fila {}: {} - {}", rowNumber, request.getTitulo(), request.getAutor());
                        continue;
                    }

                    libroService.crearLibro(request);
                    processedKeys.add(duplicateKey);
                    report.incrementInserted();
                    log.debug("Libro importado desde CSV fila {}: {} - {}", rowNumber, request.getTitulo(), request.getAutor());
                } catch (ResponseStatusException ex) {
                    String reason = ex.getReason() != null ? ex.getReason() : ex.getMessage();
                    report.addError(rowNumber, reason);
                    log.warn("Error de negocio importando fila {}: {}", rowNumber, reason);
                } catch (IllegalArgumentException ex) {
                    report.addError(rowNumber, ex.getMessage());
                    log.warn("Validación fallida importando fila {}: {}", rowNumber, ex.getMessage());
                } catch (Exception ex) {
                    report.addError(rowNumber, "Error inesperado: " + (ex.getMessage() != null ? ex.getMessage() : "sin detalle"));
                    log.error("Falla inesperada importando fila {}", rowNumber, ex);
                }
            }

            log.info("Importación CSV finalizada. Insertados: {}, duplicados: {}, errores: {}", report.getInserted(), report.getDuplicates(), report.getErrors());
            return report;
        } catch (IOException e) {
            log.error("No se pudo leer el archivo CSV para importación", e);
            throw new BadRequestException("No se pudo leer el archivo CSV proporcionado.", e);
        }
    }

    private CrearLibroRequest mapRecord(CSVRecord record) {
        CrearLibroRequest request = new CrearLibroRequest();
        String titulo = requireField(record, "titulo");
        String autor = requireField(record, "autor");

        request.setTitulo(titulo);
        request.setAutor(autor);
        request.setCantidadTotal(parseInteger(record, true, "cantidad_total", "cantidad total"));
        Integer cantidadDisponible = parseInteger(record, false, "cantidad_disponible", "cantidadDisponible", "cantidad disponible");
        request.setCantidadDisponible(cantidadDisponible);
        request.setFecha(parseFecha(record));
        request.setGeneroLibro(getOptional(record, "genero", "genero_libro", "generoLibro", "género"));
        return request;
    }

    private String buildKey(CrearLibroRequest request) {
        return (request.getTitulo().toLowerCase(Locale.ROOT) + "|" + request.getAutor().toLowerCase(Locale.ROOT)).trim();
    }

    private String requireField(CSVRecord record, String column) {
        String value = getOptional(record, column);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La columna '" + column + "' es obligatoria.");
        }
        return value.trim();
    }

    private Integer parseInteger(CSVRecord record, boolean required, String... columns) {
        String value = getOptional(record, columns);
        if (value == null || value.isBlank()) {
            if (required) {
                throw new IllegalArgumentException("La columna '" + columns[0] + "' es obligatoria.");
            }
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor numérico inválido para '" + columns[0] + "'.");
        }
    }

    private LocalDate parseFecha(CSVRecord record) {
        String value = getOptional(record, "fecha");
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha debe tener el formato yyyy-MM-dd.");
        }
    }

    private String getOptional(CSVRecord record, String... columns) {
        for (String column : columns) {
            if (record.isMapped(column)) {
                String value = record.get(column);
                if (value != null && !value.isBlank()) {
                    return value.trim();
                }
            }
        }
        return null;
    }
}
