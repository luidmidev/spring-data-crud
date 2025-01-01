package io.github.luidmidev.springframework.data.crud.core.filters;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@Data
public class Filter {
    private List<FilterCriteria> filters = new ArrayList<>();

    @Nullable
    public FilterCriteria get(String property) {
        return filters
                .stream()
                .filter(filter -> filter.property().equals(property))
                .findFirst()
                .orElse(null);
    }
}
