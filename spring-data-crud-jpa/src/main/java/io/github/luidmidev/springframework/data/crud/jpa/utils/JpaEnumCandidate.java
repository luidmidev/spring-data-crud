package io.github.luidmidev.springframework.data.crud.jpa.utils;

public interface JpaEnumCandidate {

    String name();

    int ordinal();

    boolean isCandidate(String value);
}
