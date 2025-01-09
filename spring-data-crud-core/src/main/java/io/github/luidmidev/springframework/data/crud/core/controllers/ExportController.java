package io.github.luidmidev.springframework.data.crud.core.controllers;


import io.github.luidmidev.springframework.data.crud.core.export.Exporter;
import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.export.ExportConfig;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ExportController<ID, S extends ReadOperations<?, ID>> extends ServiceProvider<S> {

    Exporter getExporter();

    @GetMapping("/export")
    default ResponseEntity<ByteArrayResource> exportPage(
            @RequestParam List<String> fields,
            @RequestParam List<String> titles,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MultiValueMap<String, String> params,
            Pageable pageable
    ) {
        var config = ExportConfig.of(fields, titles);
        return getExporter().export(getService().page(search, pageable, params), config);
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
