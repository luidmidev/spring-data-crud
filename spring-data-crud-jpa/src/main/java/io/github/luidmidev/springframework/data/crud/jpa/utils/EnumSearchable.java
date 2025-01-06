package io.github.luidmidev.springframework.data.crud.jpa.utils;

public interface EnumSearchable {
    String name();

    int ordinal();

    boolean matches(String value);

}
