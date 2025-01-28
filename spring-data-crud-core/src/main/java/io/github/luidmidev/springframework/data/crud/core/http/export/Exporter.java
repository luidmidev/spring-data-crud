package io.github.luidmidev.springframework.data.crud.core.http.export;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

public interface Exporter {

    ResponseEntity<ByteArrayResource> export(Iterable<?> elements, ExportConfig config);

}
