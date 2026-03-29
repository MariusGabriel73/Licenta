package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProgramareSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("data_programare", table, columnPrefix + "_data_programare"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("observatii", table, columnPrefix + "_observatii"));

        columns.add(Column.aliased("pacient_id", table, columnPrefix + "_pacient_id"));
        columns.add(Column.aliased("medic_id", table, columnPrefix + "_medic_id"));
        columns.add(Column.aliased("clinica_id", table, columnPrefix + "_clinica_id"));
        return columns;
    }
}
