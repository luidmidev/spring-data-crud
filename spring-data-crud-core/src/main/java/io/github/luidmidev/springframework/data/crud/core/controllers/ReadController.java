package io.github.luidmidev.springframework.data.crud.core.controllers;


import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.SpringDataCrudAutoConfiguration;
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
public interface ReadController<M extends Persistable<ID>, ID, S extends ReadOperations<M, ID>> extends ServiceProvider<S> {


    @GetMapping
    default ResponseEntity<Page<M>> page(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MultiValueMap<String, String> params,
            Pageable pageable
    ) {
        var ignoreParams = SpringDataCrudAutoConfiguration.getIgnoreParams();
        if (ignoreParams != null) ignoreParams.forEach(params::remove);
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
}
