package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Clinica;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Clinica}, with proper type conversions.
 */
@Service
public class ClinicaRowMapper implements BiFunction<Row, String, Clinica> {

    private final ColumnConverter converter;

    public ClinicaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Clinica} stored in the database.
     */
    @Override
    public Clinica apply(Row row, String prefix) {
        Clinica entity = new Clinica();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNume(converter.fromRow(row, prefix + "_nume", String.class));
        entity.setTelefon(converter.fromRow(row, prefix + "_telefon", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setLocatieId(converter.fromRow(row, prefix + "_locatie_id", Long.class));
        return entity;
    }
}
