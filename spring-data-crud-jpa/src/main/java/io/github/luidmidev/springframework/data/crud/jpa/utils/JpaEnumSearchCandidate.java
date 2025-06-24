package io.github.luidmidev.springframework.data.crud.jpa.utils;

public interface JpaEnumSearchCandidate {

    String name();

    int ordinal();

    boolean isCandidate(String value);
}
