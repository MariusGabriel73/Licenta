package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Locatie;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Locatie}, with proper type conversions.
 */
@Service
public class LocatieRowMapper implements BiFunction<Row, String, Locatie> {

    private final ColumnConverter converter;

    public LocatieRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Locatie} stored in the database.
     */
    @Override
    public Locatie apply(Row row, String prefix) {
        Locatie entity = new Locatie();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setOras(converter.fromRow(row, prefix + "_oras", String.class));
        entity.setAdresa(converter.fromRow(row, prefix + "_adresa", String.class));
        entity.setCodPostal(converter.fromRow(row, prefix + "_cod_postal", String.class));
        return entity;
    }
}
