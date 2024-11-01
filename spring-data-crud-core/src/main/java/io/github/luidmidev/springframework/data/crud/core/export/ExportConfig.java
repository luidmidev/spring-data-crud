package io.github.luidmidev.springframework.data.crud.core.export;

import io.github.luidmidev.springframework.web.problemdetails.ApiError;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public final class ExportConfig {

    private final List<Column> columns = new ArrayList<>();

    public record Column(String title, String field) {
    }

    public void addColumn(String title, String field) {
        columns.add(new Column(title, field));
    }

    static ExportConfig of(List<String> fields, List<String> titles) {

        if (fields.size() != titles.size()) {
            throw ApiError.badRequest("fields and titles must have the same size");
        }

        if (fields.isEmpty()) {
            throw ApiError.badRequest("fields and titles must not be empty");
        }

        var config = new ExportConfig();
        for (int i = 0; i < fields.size(); i++) {
            config.addColumn(titles.get(i), fields.get(i));
        }

        return config;
    }


}
