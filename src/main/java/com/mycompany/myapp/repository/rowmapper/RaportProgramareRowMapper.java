package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.RaportProgramare;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RaportProgramare}, with proper type conversions.
 */
@Service
public class RaportProgramareRowMapper implements BiFunction<Row, String, RaportProgramare> {

    private final ColumnConverter converter;

    public RaportProgramareRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RaportProgramare} stored in the database.
     */
    @Override
    public RaportProgramare apply(Row row, String prefix) {
        RaportProgramare entity = new RaportProgramare();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setOraProgramata(converter.fromRow(row, prefix + "_ora_programata", ZonedDateTime.class));
        entity.setOraInceputConsultatie(converter.fromRow(row, prefix + "_ora_inceput_consultatie", ZonedDateTime.class));
        entity.setDurataConsultatie(converter.fromRow(row, prefix + "_durata_consultatie", Integer.class));
        entity.setProgramareId(converter.fromRow(row, prefix + "_programare_id", Long.class));
        return entity;
    }
}
