package io.github.luidmidev.springframework.data.crud.core.http.export;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public final class ExportConfig {

    private final List<Column> columns = new ArrayList<>();

    record Column(String title, String field) {
        Column {
            if (title == null || field == null) throw new ExporterException("title and field must not be null");
        }
    }

    public void addColumn(String title, String field) {
        columns.add(new Column(title, field));
    }

    public static ExportConfig of(List<String> fields, List<String> titles) {
        if (fields.size() != titles.size()) throw new ExporterException("fields and titles must have the same size");
        if (fields.isEmpty()) throw new ExporterException("fields and titles must not be empty");

        var config = new ExportConfig();
        for (int i = 0; i < fields.size(); i++) {
            config.addColumn(titles.get(i), fields.get(i));
        }

        return config;
    }
}
