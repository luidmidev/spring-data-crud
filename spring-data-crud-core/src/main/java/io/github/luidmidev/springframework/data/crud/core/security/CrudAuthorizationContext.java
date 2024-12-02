package io.github.luidmidev.springframework.data.crud.core.security;

import io.github.luidmidev.springframework.data.crud.core.operations.Operation;

public record CrudAuthorizationContext(Object target, Operation operation) {
}

