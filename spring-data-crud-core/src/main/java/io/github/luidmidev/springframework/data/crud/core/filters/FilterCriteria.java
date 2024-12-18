package io.github.luidmidev.springframework.data.crud.core.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PACKAGE)
public class FilterCriteria {

    private String property;
    private FilterOperator operator;
    private Object value;

    public <T> T value(Class<T> clazz) {
        return clazz.cast(value);
    }

    public Object value() {
        return value;
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
