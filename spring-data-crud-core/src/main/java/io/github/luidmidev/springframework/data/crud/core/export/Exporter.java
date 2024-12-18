package io.github.luidmidev.springframework.data.crud.core.export;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Exporter {

    ResponseEntity<ByteArrayResource> export(Iterable<?> elements, ExportConfig config);

}
