package com.ProyectoFinal.BookTrack.dto;

import java.util.*;

public class ImportReport {
    private int inserted, duplicates, errors;
    private final List<Map<String,Object>> errorsDetail = new ArrayList<>();

    public void addInserted(){ inserted++; }
    public void addDuplicate(){ duplicates++; }
    public void addError(int row, String reason){
        errors++;
        Map<String,Object> e = new HashMap<>();
        e.put("row", row);
        e.put("reason", reason);
        errorsDetail.add(e);
    }

    public int getInserted(){ return inserted; }
    public int getDuplicates(){ return duplicates; }
    public int getErrors(){ return errors; }
    public List<Map<String, Object>> getErrorsDetail(){ return errorsDetail; }
}
