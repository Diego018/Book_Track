package com.ProyectoFinal.BookTrack.entity.converter;

import com.ProyectoFinal.BookTrack.entity.EstadoPrestamo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoPrestamoConverter implements AttributeConverter<EstadoPrestamo, String> {

    @Override
    public String convertToDatabaseColumn(EstadoPrestamo attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public EstadoPrestamo convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return EstadoPrestamo.fromDatabaseValue(dbData);
    }
}
