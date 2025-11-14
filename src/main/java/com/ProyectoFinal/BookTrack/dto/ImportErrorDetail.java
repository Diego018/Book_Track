package com.ProyectoFinal.BookTrack.dto;

public class ImportErrorDetail {

    private final long row;
    private final String reason;

    public ImportErrorDetail(long row, String reason) {
        this.row = row;
        this.reason = reason;
    }

    public long getRow() {
        return row;
    }

    public String getReason() {
        return reason;
    }
}
