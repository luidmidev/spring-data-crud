package io.github.luidmidev.springframework.data.crud.core.filters;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Filter {
    private List<FilterCriteria> filters = new ArrayList<>();

    public FilterCriteria get(String field) {
        return filters
                .stream()
                .filter(filter -> filter.getField().equals(field)).findFirst()
                .orElse(null);
    }
}
