package io.github.luidmidev.springframework.data.crud.core.security;

import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperation;

public interface CrudMatcher {
    boolean matches(Object target, CrudOperation crudOperation);
}
