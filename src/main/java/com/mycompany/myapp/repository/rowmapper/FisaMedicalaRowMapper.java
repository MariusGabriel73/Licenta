package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.FisaMedicala;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link FisaMedicala}, with proper type conversions.
 */
@Service
public class FisaMedicalaRowMapper implements BiFunction<Row, String, FisaMedicala> {

    private final ColumnConverter converter;

    public FisaMedicalaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link FisaMedicala} stored in the database.
     */
    @Override
    public FisaMedicala apply(Row row, String prefix) {
        FisaMedicala entity = new FisaMedicala();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDiagnostic(converter.fromRow(row, prefix + "_diagnostic", String.class));
        entity.setTratament(converter.fromRow(row, prefix + "_tratament", String.class));
        entity.setRecomandari(converter.fromRow(row, prefix + "_recomandari", String.class));
        entity.setDataConsultatie(converter.fromRow(row, prefix + "_data_consultatie", ZonedDateTime.class));
        entity.setProgramareId(converter.fromRow(row, prefix + "_programare_id", Long.class));
        return entity;
    }
}
