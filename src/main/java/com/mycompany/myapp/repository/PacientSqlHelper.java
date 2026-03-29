package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PacientSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("cnp", table, columnPrefix + "_cnp"));
        columns.add(Column.aliased("telefon", table, columnPrefix + "_telefon"));
        columns.add(Column.aliased("data_nasterii", table, columnPrefix + "_data_nasterii"));
        columns.add(Column.aliased("adresa", table, columnPrefix + "_adresa"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
