package io.github.luidmidev.springframework.data.crud.core.controllers;


import io.github.luidmidev.springframework.data.crud.core.export.Exporter;
import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.export.ExportConfig;
import io.github.luidmidev.springframework.data.crud.core.filters.Filter;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import io.github.luidmidev.springframework.data.crud.core.utils.CrudUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ExportController<ID, S extends ReadOperations<?, ID>> extends ServiceProvider<S> {

    Exporter getExporter();

    @GetMapping("/export")
    default ResponseEntity<ByteArrayResource> exportAll(
            @RequestParam List<String> fields,
            @RequestParam List<String> titles,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String[] properties,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) Filter filter
    ) {
        var sort = CrudUtils.resolveSort(direction, properties);
        var config = ExportConfig.of(fields, titles);
        return getExporter().export(getService().all(search, sort, filter), config);
    }


    @GetMapping("/export/page")
    default ResponseEntity<ByteArrayResource> exportPage(
            @RequestParam List<String> fields,
            @RequestParam List<String> titles,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) String[] properties,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) Filter filter
    ) {
        var pageable = CrudUtils.resolvePage(page, size, direction, properties);
        var config = ExportConfig.of(fields, titles);
        return getExporter().export(getService().page(search, pageable, filter), config);
    }

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
