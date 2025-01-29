package io.github.luidmidev.springframework.data.crud.core.http.controllers;

import io.github.luidmidev.springframework.data.crud.core.EnabledStatePersistable;
import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.operations.WriteOperations;
import org.springframework.data.domain.Persistable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CRUD Controller for performing write operations.
 * <p>
 * This controller provides endpoints for creating, updating, and deleting entities. It uses the service defined in
 * {@link WriteOperations} to delegate the actual write operations.
 * </p>
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <D>  DTO (Data Transfer Object) used to transfer data for create and update operations
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 * @param <S>  Service that extends {@link WriteOperations} for the CRUD operations
 */
public interface WriteController<M extends Persistable<ID>, D, ID, S extends WriteOperations<M, D, ID>> extends ServiceProvider<S> {

    /**
     * Endpoint to create a new entity from a DTO.
     * <p>
     * This method maps the provided DTO to a new entity and delegates the creation operation to the service.
     * </p>
     *
     * @param dto The DTO containing the data to create a new entity
     * @return The newly created entity
     */
    @PostMapping
    default ResponseEntity<M> create(@RequestBody D dto) {
        return ResponseEntity.ok(getService().create(dto));
    }

    /**
     * Endpoint to update an existing entity by its unique identifier.
     * <p>
     * This method maps the provided DTO to the existing entity and delegates the update operation to the service.
     * </p>
     *
     * @param id The unique identifier of the entity to update
     * @param dto The DTO containing the updated data for the entity
     * @return The updated entity
     */
    @PutMapping("/{id}")
    default ResponseEntity<M> update(@PathVariable ID id, @RequestBody D dto) {
        return ResponseEntity.ok(getService().update(id, dto));
    }

    /**
     * Endpoint to delete an entity by its unique identifier.
     * <p>
     * This method delegates the delete operation to the service and returns a message confirming the deletion.
     * </p>
     *
     * @param id The unique identifier of the entity to delete
     * @return A confirmation message indicating the entity was deleted
     */
    @DeleteMapping("/{id}")
    default ResponseEntity<String> delete(@PathVariable ID id) {
        getService().delete(id);
        return ResponseEntity.ok(deletedMessage(id));
    }

    /**
     * Generates a message indicating the deletion of an entity.
     *
     * @param id The unique identifier of the entity that was deleted
     * @return A string message confirming the deletion of the entity
     */
    default String deletedMessage(ID id) {
        return "Deleted " + id;
    }

    /**
     * Controller for enabling/disabling entities.
     * @param <M> Entity model that extends {@link EnabledStatePersistable} with the specified {@code ID}.
     * @param <ID> The type of the entity's identifier.
     * @param <S> Service that extends {@link WriteOperations.EnabledStateOperation} for the enabled state operations.
     */
    interface EnableStatusController<M extends EnabledStatePersistable<ID>, ID, S extends WriteOperations.EnabledStateOperation<M, ID>> extends ServiceProvider<S> {

        @PutMapping("/{id}/enabled")
        default ResponseEntity<M> updateEnabled(@PathVariable ID id, @RequestParam boolean value) {
            return ResponseEntity.ok(getService().updateEnabled(id, value));
        }
    }

}
