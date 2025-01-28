package io.github.luidmidev.springframework.data.crud.core.http.export;

public class ExporterException extends RuntimeException {
    public ExporterException(String message) {
        super(message);
    }

    public ExporterException(String message, Throwable cause) {
        super(message, cause);
    }
}
