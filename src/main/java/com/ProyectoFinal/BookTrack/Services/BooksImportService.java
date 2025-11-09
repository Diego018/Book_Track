package com.ProyectoFinal.BookTrack.Services;

import com.ProyectoFinal.BookTrack.Repositories.LibroRepository;
import com.ProyectoFinal.BookTrack.dto.ImportReport;
import com.ProyectoFinal.BookTrack.entity.Libro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

@Service
@RequiredArgsConstructor
public class BooksImportService {

    private final LibroRepository libroRepo;

    public ImportReport importCsv(InputStream raw) {
        ImportReport rep = new ImportReport();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(raw, StandardCharsets.UTF_8))) {
            String header = br.readLine();
            validateHeader(header);
            String line; int row = 1;
            while ((line = br.readLine()) != null) {
                row++;
                if (line.trim().isEmpty()) continue;
                try {
                    String[] c = parseCsvLine(line); // title,author,cantidad_total,cantidad_disponible,fecha
                    String titulo = norm(c[0]);
                    String autor  = norm(c[1]);
                    int total = parseInt(c[2], "cantidad_total");
                    int disp  = parseInt(c[3], "cantidad_disponible");
                    Date fecha = Date.valueOf(c[4].trim()); // YYYY-MM-DD

                    if (libroRepo.existsByTituloIgnoreCaseAndAutorIgnoreCase(titulo, autor)) {
                        rep.addDuplicate();
                        continue;
                    }

                    Libro b = new Libro();
                    b.setTitulo(titulo);
                    b.setAutor(autor);
                    b.setCantidad_total(total);
                    b.setCantidad_disponible(disp);
                    b.setFecha(fecha);

                    libroRepo.save(b);
                    rep.addInserted();
                } catch (Exception ex) {
                    rep.addError(row, ex.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el CSV");
        }
        return rep;
    }

    private void validateHeader(String h){
        if (h == null) throw new IllegalArgumentException("CSV vacío");
        String want="title,author,cantidad_total,cantidad_disponible,fecha";
        String got=h.trim().toLowerCase().replace(" ","");
        if(!got.equals(want)) throw new IllegalArgumentException("Header inválido. Use: "+want);
    }
    private String norm(String x){ return x==null? "": x.trim().replaceAll("\\s+"," "); }
    private int parseInt(String s, String f){
        try { int v=Integer.parseInt(s.trim()); if(v<0) throw new IllegalArgumentException(f+" debe ser ≥ 0"); return v; }
        catch(NumberFormatException e){ throw new IllegalArgumentException(f+" no es entero"); }
    }
    private String[] parseCsvLine(String line){
        String[] p=line.split("(?<!\\\\),",-1);
        if(p.length!=5) throw new IllegalArgumentException("Columnas inválidas en fila"); return p;
    }
}
