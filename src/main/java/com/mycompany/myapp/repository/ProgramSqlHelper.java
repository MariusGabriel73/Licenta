package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProgramSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("ziua_saptamanii", table, columnPrefix + "_ziua_saptamanii"));
        columns.add(Column.aliased("ora_start", table, columnPrefix + "_ora_start"));
        columns.add(Column.aliased("ora_final", table, columnPrefix + "_ora_final"));

        columns.add(Column.aliased("medic_id", table, columnPrefix + "_medic_id"));
        columns.add(Column.aliased("locatie_id", table, columnPrefix + "_locatie_id"));
        return columns;
    }
}
