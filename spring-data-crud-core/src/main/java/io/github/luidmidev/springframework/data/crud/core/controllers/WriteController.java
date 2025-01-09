package io.github.luidmidev.springframework.data.crud.core.controllers;


import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.operations.WriteOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Persistable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CRUD Controller
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 * @param <S>  Service
 */
@RequiredArgsConstructor
public abstract class WriteController<M extends Persistable<ID>, D, ID, S extends WriteOperations<M, D, ID>> implements ServiceProvider<S> {

    private final S service;

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
