package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class LocatieSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("oras", table, columnPrefix + "_oras"));
        columns.add(Column.aliased("adresa", table, columnPrefix + "_adresa"));
        columns.add(Column.aliased("cod_postal", table, columnPrefix + "_cod_postal"));

        return columns;
    }
}
