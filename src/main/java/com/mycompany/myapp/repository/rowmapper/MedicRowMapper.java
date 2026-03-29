package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Medic;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Medic}, with proper type conversions.
 */
@Service
public class MedicRowMapper implements BiFunction<Row, String, Medic> {

    private final ColumnConverter converter;

    public MedicRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Medic} stored in the database.
     */
    @Override
    public Medic apply(Row row, String prefix) {
        Long id = converter.fromRow(row, prefix + "_id", Long.class);
        if (id == null) {
            return null;
        }
        Medic entity = new Medic();
        entity.setId(id);
        entity.setGradProfesional(converter.fromRow(row, prefix + "_grad_profesional", String.class));
        entity.setTelefon(converter.fromRow(row, prefix + "_telefon", String.class));
        entity.setDisponibil(converter.fromRow(row, prefix + "_disponibil", Boolean.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setSectieId(converter.fromRow(row, prefix + "_sectie_id", Long.class));
        return entity;
    }
}
