package com.desarrolloweb.DeliveryApp.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Traduce la columna INTEGER fue_valido (0/1) al boolean de Java. */
@Converter
public class BooleanAsIntegerConverter implements AttributeConverter<Boolean, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Boolean value) {
        return Boolean.TRUE.equals(value) ? 1 : 0;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer value) {
        return value != null && value != 0;
    }
}
