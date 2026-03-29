package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Sectie;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Sectie}, with proper type conversions.
 */
@Service
public class SectieRowMapper implements BiFunction<Row, String, Sectie> {

    private final ColumnConverter converter;

    public SectieRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Sectie} stored in the database.
     */
    @Override
    public Sectie apply(Row row, String prefix) {
        Sectie entity = new Sectie();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNume(converter.fromRow(row, prefix + "_nume", String.class));
        return entity;
    }
}
