package io.github.luidmidev.springframework.data.crud.core.filters;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilterCriteria {

    private String field;

    private FilterOperator operator;

    private Object value;

    public <T> T getValue(Class<T> clazz) {
        return clazz.cast(value);
    }

    public <T> List<T> getValues(Class<T> clazz) {
        if (value instanceof List<?> list) {
            return list.stream().map(clazz::cast).toList();
        } else {
            throw new ClassCastException("Value is not a list");
        }
    }
}
