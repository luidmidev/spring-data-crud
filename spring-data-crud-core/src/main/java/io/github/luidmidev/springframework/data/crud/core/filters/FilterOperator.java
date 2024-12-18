package io.github.luidmidev.springframework.data.crud.core.filters;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilterOperator {
    EQ("eq"),
    NE("ne"),
    GT("gt"),
    GE("ge"),
    LT("lt"),
    LE("le"),
    IN("in"),
    NIN("nin"),
    LIKE("like"),
    IS_NULL("isNull"),
    NOT_NULL("notNull");

    private final String value;


}
