package io.github.luidmidev.springframework.data.crud.core.controllers;


import io.github.luidmidev.springframework.data.crud.core.export.Exporter;
import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.export.ExportConfig;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import io.github.luidmidev.springframework.data.crud.core.utils.PageableUtils;
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
            @RequestParam(required = false) String search
    ) {
        var config = ExportConfig.of(fields, titles);
        return getExporter().export(getService().list(search), config);
    }


    @GetMapping("/export/page")
    default ResponseEntity<ByteArrayResource> exportPage(
            @RequestParam List<String> fields,
            @RequestParam List<String> titles,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) String[] properties,
            @RequestParam(required = false) Sort.Direction direction
    ) {
        var pageable = PageableUtils.resolvePage(page, size, direction, properties);
        var config = ExportConfig.of(fields, titles);
        return getExporter().export(getService().page(search, pageable).toList(), config);
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
