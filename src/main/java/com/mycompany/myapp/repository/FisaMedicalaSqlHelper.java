package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FisaMedicalaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("diagnostic", table, columnPrefix + "_diagnostic"));
        columns.add(Column.aliased("tratament", table, columnPrefix + "_tratament"));
        columns.add(Column.aliased("recomandari", table, columnPrefix + "_recomandari"));
        columns.add(Column.aliased("data_consultatie", table, columnPrefix + "_data_consultatie"));

        columns.add(Column.aliased("programare_id", table, columnPrefix + "_programare_id"));
        return columns;
    }
}
