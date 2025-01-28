package io.github.luidmidev.springframework.data.crud.core.http.controllers;

import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import org.springframework.data.domain.Persistable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface WriteController<M extends Persistable<ID>, D, ID, S extends CrudOperations<M, D, ID>> extends ServiceProvider<S> {

    @PostMapping
    default ResponseEntity<M> create(@RequestBody D dto) {
        return ResponseEntity.ok(getService().create(dto));
    }

    @PutMapping("/{id}")
    default ResponseEntity<M> update(@PathVariable ID id, @RequestBody D dto) {
        return ResponseEntity.ok(getService().update(id, dto));
    }

    @DeleteMapping("/{id}")
    default ResponseEntity<String> delete(@PathVariable ID id) {
        getService().delete(id);
        return ResponseEntity.ok(deletedMessage(id));
    }

    default String deletedMessage(ID id) {
        return "Deleted " + id;
    }
}
