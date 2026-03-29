package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Programare;
import com.mycompany.myapp.domain.enumeration.ProgramareStatus;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Programare}, with proper type conversions.
 */
@Service
public class ProgramareRowMapper implements BiFunction<Row, String, Programare> {

    private final ColumnConverter converter;

    public ProgramareRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Programare} stored in the database.
     */
    @Override
    public Programare apply(Row row, String prefix) {
        Programare entity = new Programare();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDataProgramare(converter.fromRow(row, prefix + "_data_programare", ZonedDateTime.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ProgramareStatus.class));
        entity.setObservatii(converter.fromRow(row, prefix + "_observatii", String.class));
        entity.setPacientId(converter.fromRow(row, prefix + "_pacient_id", Long.class));
        entity.setMedicId(converter.fromRow(row, prefix + "_medic_id", Long.class));
        entity.setClinicaId(converter.fromRow(row, prefix + "_clinica_id", Long.class));
        return entity;
    }
}
