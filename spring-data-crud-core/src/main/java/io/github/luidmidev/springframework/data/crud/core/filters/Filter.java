package io.github.luidmidev.springframework.data.crud.core.filters;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class Filter {
    private List<FilterCriteria> filters = new ArrayList<>();

    public Optional<FilterCriteria> get(String property) {
        return filters
                .stream()
                .filter(filter -> filter.getProperty().equals(property))
                .findFirst();
    }
}
