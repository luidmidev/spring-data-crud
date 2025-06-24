package io.github.luidmidev.springframework.data.crud.core.http.controllers;


import io.github.luidmidev.springframework.data.crud.core.SpringDataCrudAutoConfiguration;
import io.github.luidmidev.springframework.data.crud.core.http.PaginationParameters;
import io.github.luidmidev.springframework.data.crud.core.http.export.Exporter;
import io.github.luidmidev.springframework.data.crud.core.providers.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.http.export.ExportConfig;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for exporting data in a specific format (e.g., CSV, Excel).
 * <p>
 * This controller allows exporting entity data, either as a whole page or a single entity,
 * based on the configured fields and titles. The exported data can be returned as a downloadable resource.
 * </p>
 *
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 * @param <S>  Service that extends {@link ReadOperations} to provide the necessary read operations for the entity
 */
public interface ExportController<ID, S extends ReadOperations<?, ID>> extends ServiceProvider<S> {

    /**
     * Retrieves the exporter used for exporting data.
     *
     * @return the {@link Exporter} that handles the export process
     */
    Exporter getExporter();

    /**
     * Endpoint to export a page of data with specified fields and titles.
     * <p>
     * This method allows exporting data in a paginated manner, using filters and search parameters.
     * The export format is determined by the exporter.
     * </p>
     *
     * @param fields  the list of fields to be included in the export
     * @param titles  the list of titles corresponding to the fields
     * @param search  an optional search string to filter the results
     * @param filters an optional set of additional filters
     * @param pageable the pagination information
     * @return a {@link ResponseEntity} containing the export file as a {@link ByteArrayResource}
     */
    @PaginationParameters
    @GetMapping("/export")
    default ResponseEntity<ByteArrayResource> exportPage(
            @RequestParam List<String> fields,
            @RequestParam List<String> titles,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MultiValueMap<String, String> filters,
            @PageableDefault Pageable pageable
    ) {
        var config = ExportConfig.of(fields, titles);
        SpringDataCrudAutoConfiguration.clearIgnoreParams(filters);
        return getExporter().export(getService().page(search, pageable, filters), config);
    }

    /**
     * Endpoint to export a single entity's data with specified fields and titles.
     * <p>
     * This method allows exporting a specific entity based on its ID, using the provided fields and titles.
     * </p>
     *
     * @param fields the list of fields to be included in the export
     * @param titles the list of titles corresponding to the fields
     * @param id     the ID of the entity to export
     * @return a {@link ResponseEntity} containing the export file as a {@link ByteArrayResource}
     */
    @GetMapping("/export/{id}")
    default ResponseEntity<ByteArrayResource> exportFind(
            @RequestParam List<String> fields,
            @RequestParam List<String> titles,
            @PathVariable ID id
    ) {
        var config = ExportConfig.of(fields, titles);
        return getExporter().export(List.of(getService().find(id)), config);
    }
}
