package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Program;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Program}, with proper type conversions.
 */
@Service
public class ProgramRowMapper implements BiFunction<Row, String, Program> {

    private final ColumnConverter converter;

    public ProgramRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Program} stored in the database.
     */
    @Override
    public Program apply(Row row, String prefix) {
        Program entity = new Program();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setZiuaSaptamanii(converter.fromRow(row, prefix + "_ziua_saptamanii", String.class));
        entity.setOraStart(converter.fromRow(row, prefix + "_ora_start", ZonedDateTime.class));
        entity.setOraFinal(converter.fromRow(row, prefix + "_ora_final", ZonedDateTime.class));
        entity.setMedicId(converter.fromRow(row, prefix + "_medic_id", Long.class));
        entity.setLocatieId(converter.fromRow(row, prefix + "_locatie_id", Long.class));
        return entity;
    }
}
