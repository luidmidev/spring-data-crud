package io.github.luidmidev.springframework.data.crud.core.security;

import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperation;

public record CrudAuthorizationContext(Object target, CrudOperation crudOperation) {
}

