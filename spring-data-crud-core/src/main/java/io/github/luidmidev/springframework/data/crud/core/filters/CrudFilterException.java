package io.github.luidmidev.springframework.data.crud.core.filters;

public class CrudFilterException extends RuntimeException {

    public CrudFilterException(String message) {
        super(message);
    }

    public CrudFilterException(String message, Throwable cause) {
        super(message, cause);
    }
}
