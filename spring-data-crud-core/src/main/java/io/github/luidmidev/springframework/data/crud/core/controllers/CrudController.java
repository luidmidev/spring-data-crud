package io.github.luidmidev.springframework.data.crud.core.controllers;

import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import org.springframework.data.domain.Persistable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public abstract class CrudController<M extends Persistable<ID>, D, ID, S extends CrudOperations<M, D, ID>> extends ReadController<M, ID, S> {

    protected CrudController(S service) {
        super(service);
    }

    @PostMapping
    public ResponseEntity<M> create(@RequestBody D dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<M> update(@PathVariable ID id, @RequestBody D dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable ID id) {
        service.delete(id);
        return ResponseEntity.ok(deletedMessage(id));
    }

    public String deletedMessage(ID id) {
        return "Deleted " + id;
    }
}
