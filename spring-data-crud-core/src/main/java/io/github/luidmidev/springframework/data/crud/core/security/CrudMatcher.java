package io.github.luidmidev.springframework.data.crud.core.security;

import io.github.luidmidev.springframework.data.crud.core.operations.Operation;

public interface CrudMatcher {
    boolean matches(Object target, Operation operation);
}
