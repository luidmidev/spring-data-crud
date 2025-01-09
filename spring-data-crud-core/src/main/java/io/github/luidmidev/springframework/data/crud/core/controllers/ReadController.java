package io.github.luidmidev.springframework.data.crud.core.controllers;


import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * CRUD Controller
 *
 * @param <M>  Model
 * @param <ID> ID
 * @param <S>  Service
 */
@Data
@RequiredArgsConstructor
public abstract class ReadController<M extends Persistable<ID>, ID, S extends ReadOperations<M, ID>> implements ServiceProvider<S> {

    protected final S service;

    private List<String> ignoreParams;

    @GetMapping
    public ResponseEntity<Page<M>> page(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MultiValueMap<String, String> params,
            Pageable pageable
    ) {
        if (ignoreParams != null) ignoreParams.forEach(params::remove);
        return ResponseEntity.ok(service.page(search, pageable, params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<M> find(@PathVariable ID id) {
        return ResponseEntity.ok(service.find(id));
    }

    @GetMapping("/ids")
    public ResponseEntity<List<M>> find(@RequestParam List<ID> ids) {
        return ResponseEntity.ok(service.find(ids));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.count());
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam ID id) {
        return ResponseEntity.ok(service.exists(id));
    }
}
