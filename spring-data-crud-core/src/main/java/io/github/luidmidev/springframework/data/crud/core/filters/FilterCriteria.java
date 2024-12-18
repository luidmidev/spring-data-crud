package io.github.luidmidev.springframework.data.crud.core.filters;

import java.util.List;

public record FilterCriteria(String property, FilterOperator operator, Object value) {
    public <T> T value(Class<T> clazz) {
        return clazz.cast(value);
    }

    public <T> List<T> values(Class<T> clazz) {
        if (value instanceof List<?> list) {
            return list.stream().map(clazz::cast).toList();
        }
        throw new ClassCastException("Value is not a list");

    }

    public List<?> values() {
        if (value instanceof List<?> list) {
            return list;
        }
        throw new ClassCastException("Value is not a list");
    }
}