package io.github.luidmidev.springframework.data.crud.core.security;

public record OperationMatcherEntry<T>(CrudMatcher matcher, T entry) {

}
