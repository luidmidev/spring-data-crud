package io.github.luidmidev.springframework.data.crud.core.controllers;


import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import io.github.luidmidev.springframework.data.crud.core.utils.CrudUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
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
    default ResponseEntity<Page<M>> page(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) String[] properties,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) MultiValueMap<String, String> params
    ) {
        var pageable = processPageParams(size, page, properties, direction, params);
        return ResponseEntity.ok(getService().page(search, pageable, params));
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

    @GetMapping("/exists")
    default ResponseEntity<Boolean> exists(@RequestParam ID id) {
        return ResponseEntity.ok(getService().exists(id));
    }

    static Pageable processPageParams(int size, int page, String[] properties, Sort.Direction direction, MultiValueMap<String, String> params) {
        CrudUtils.cleanParams(params);
        return CrudUtils.resolvePage(size, page, direction, properties);
    }
}
