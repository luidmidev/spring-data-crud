package io.github.luidmidev.springframework.data.crud.core.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NotFoundModelException extends RuntimeException {
    private final Class<?> modelClass;
    private final transient Object id;
}
