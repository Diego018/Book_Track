package com.ProyectoFinal.BookTrack.entity;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum EstadoPrestamo {
    ACTIVO(true, "ACTIVO"),
    DEVUELTO(false, "DEVUELTO"),
    DESCONOCIDO(false, "DESCONOCIDO");

    private static final Map<String, EstadoPrestamo> NORMALIZED_VALUES = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(EstadoPrestamo::getValue, Function.identity()));

    private final boolean activo;
    private final String value;

    EstadoPrestamo(boolean activo, String value) {
        this.activo = activo;
        this.value = value;
    }

    public boolean esActivo() {
        return activo;
    }

    public String getValue() {
        return value;
    }

    public static EstadoPrestamo fromDatabaseValue(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }

        String normalized = rawValue.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("ACTIVA") || normalized.equals("EN_CURSO") || normalized.equals("EN CURSO")) {
            return ACTIVO;
        }
        if (normalized.equals("DEVUELTA") || normalized.equals("FINALIZADO") || normalized.equals("CERRADO")) {
            return DEVUELTO;
        }

        return NORMALIZED_VALUES.getOrDefault(normalized, DESCONOCIDO);
    }
}
