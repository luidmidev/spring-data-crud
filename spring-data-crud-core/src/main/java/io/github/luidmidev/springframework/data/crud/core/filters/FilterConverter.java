package io.github.luidmidev.springframework.data.crud.core.filters;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class FilterConverter implements Converter<String, Filter> {

    private static final String FORMAT = "field:operator:value";
    private static final String EXAMPLE = "name:eq:John";

    @Nullable
    @Override
    public Filter convert(String source) {
        if (source.isBlank()) return null;
        log.debug("Converting filter: {}", source);

        var parts = source.split("(?<!\\\\),");
        if (parts.length == 0) {
            throw new CrudFilterException("Invalid filter format. Expected: " + FORMAT + ". Example: " + EXAMPLE + ". Received: " + source);
        }

        var filter = new Filter();
        var criterias = new ArrayList<FilterCriteria>();


        for (var part : parts) {
            var components = part.split("(?<!\\\\):");

            if (components.length != 3) {
                throw new CrudFilterException("Invalid filter format. Expected: " + FORMAT + ". Example: " + EXAMPLE);
            }

            var field = unescape(components[0]);
            var operator = unescape(components[1]);
            var value = unescape(components[2]);

            criterias.add(new FilterCriteria(field, operator(operator), value));
        }

        filter.setFilters(criterias);
        return filter;
    }

    private static String unescape(String input) {
        return input
                .replace("\\,", ",")
                .replace("\\:", ":");
    }

    private static FilterOperator operator(String operator) {
        return Arrays
                .stream(FilterOperator.values())
                .filter(op -> op.getValue().equals(operator))
                .findFirst()
                .orElseThrow(() -> new CrudFilterException("Invalid operator: " + operator));
    }
}