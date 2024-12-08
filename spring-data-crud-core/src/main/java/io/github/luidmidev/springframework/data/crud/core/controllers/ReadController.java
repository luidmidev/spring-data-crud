package io.github.luidmidev.springframework.data.crud.core.controllers;


import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import io.github.luidmidev.springframework.data.crud.core.utils.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD Controller
 *
 * @param <M>  Model
 * @param <ID> ID
 * @param <S>  Service
 */
public interface ReadController<M extends Persistable<ID>, ID, S extends ReadOperations<M, ID>> extends ServiceProvider<S> {

    @GetMapping
    default ResponseEntity<List<M>> list(
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(getService().list(search));
    }

    @GetMapping("/page")
    default ResponseEntity<Page<M>> page(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) String[] properties,
            @RequestParam(required = false) Sort.Direction direction
    ) {
        var pageable = PageableUtils.resolvePage(size, page, direction, properties);
        return ResponseEntity.ok(getService().page(search, pageable));
    }

    @GetMapping("/{id}")
    default ResponseEntity<M> find(@PathVariable ID id) {
        return ResponseEntity.ok(getService().find(id));
    }

    @GetMapping("/ids")
    default ResponseEntity<List<M>> find(@RequestParam List<ID> ids) {
        return ResponseEntity.ok(getService().find(ids));
    }

    @GetMapping("/count")
    default ResponseEntity<Long> count() {
        return ResponseEntity.ok(getService().count());
    }

    @PostMapping("/exists")
    default ResponseEntity<Boolean> exists(@RequestBody ID id) {
        return ResponseEntity.ok(getService().exists(id));
    }
}
