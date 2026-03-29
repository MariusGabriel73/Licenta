package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Pacient;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pacient}, with proper type conversions.
 */
@Service
public class PacientRowMapper implements BiFunction<Row, String, Pacient> {

    private final ColumnConverter converter;

    public PacientRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pacient} stored in the database.
     */
    @Override
    public Pacient apply(Row row, String prefix) {
        Long id = converter.fromRow(row, prefix + "_id", Long.class);
        if (id == null) {
            return null;
        }
        Pacient entity = new Pacient();
        entity.setId(id);
        entity.setCnp(converter.fromRow(row, prefix + "_cnp", String.class));
        entity.setTelefon(converter.fromRow(row, prefix + "_telefon", String.class));
        entity.setDataNasterii(converter.fromRow(row, prefix + "_data_nasterii", LocalDate.class));
        entity.setAdresa(converter.fromRow(row, prefix + "_adresa", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
