package com.ProyectoFinal.BookTrack.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportReport {

    private int inserted;
    private int duplicates;
    private int errors;
    private final List<ImportErrorDetail> errorsDetail = new ArrayList<>();

    public int getInserted() {
        return inserted;
    }

    public int getDuplicates() {
        return duplicates;
    }

    public int getErrors() {
        return errors;
    }

    public List<ImportErrorDetail> getErrorsDetail() {
        return Collections.unmodifiableList(errorsDetail);
    }

    public void incrementInserted() {
        this.inserted++;
    }

    public void incrementDuplicates() {
        this.duplicates++;
    }

    public void addError(long row, String reason) {
        this.errors++;
        this.errorsDetail.add(new ImportErrorDetail(row, reason));
    }
}
