package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Specializare;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Specializare}, with proper type conversions.
 */
@Service
public class SpecializareRowMapper implements BiFunction<Row, String, Specializare> {

    private final ColumnConverter converter;

    public SpecializareRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Specializare} stored in the database.
     */
    @Override
    public Specializare apply(Row row, String prefix) {
        Specializare entity = new Specializare();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNume(converter.fromRow(row, prefix + "_nume", String.class));
        return entity;
    }
}
