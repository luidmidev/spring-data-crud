package io.github.luidmidev.springframework.data.crud.core.http.export;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

/**
 * Interface for exporting data to a specified format (e.g., CSV, Excel, PDF).
 * <p>
 * The {@link Exporter} interface defines a method for exporting a collection of elements
 * in a specific format, such as CSV, Excel, or any other format supported by the implementation.
 * The exported data is returned as a {@link ByteArrayResource} that can be downloaded by the client.
 * </p>
 */
public interface Exporter {

    /**
     * Exports a collection of elements to a specified format.
     * <p>
     * This method takes a collection of elements and converts them into the desired export format,
     * such as CSV, Excel, or another format. The export is configured based on the provided
     * {@link ExportConfig}, which specifies the fields and titles to be included in the export.
     * </p>
     *
     * @param elements the collection of elements to export
     * @param config   the configuration specifying which fields and titles to export
     * @return a {@link ResponseEntity} containing the exported data as a {@link ByteArrayResource}
     *         that can be downloaded by the client
     */
    ResponseEntity<ByteArrayResource> export(Iterable<?> elements, ExportConfig config);
}
