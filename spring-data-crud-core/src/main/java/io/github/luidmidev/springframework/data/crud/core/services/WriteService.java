package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.operations.WriteOperations;
import io.github.luidmidev.springframework.data.crud.core.services.hooks.WriteHooks;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.data.domain.Persistable;
import org.springframework.validation.annotation.Validated;

/**
 * CRUD Service that extends {@link WriteOperations} to provide CRUD operations for entities with hooks for additional processing.
 * <br>
 * This service interacts with a model of type {@code M} which is a {@link Persistable} entity identified by {@code ID},
 * and a DTO type {@code D} used for data transfer.
 * It adds hook calls to each of the write operations, enabling additional logic such as logging, validation, or other custom operations before or after the main action.
 * <br>
 * The hooks are provided by the {@link WriteHooks} and can be customized to fit the needs of the application.
 * <br><br>
 * The service supports the following operations:
 * <ul>
 *     <li>{@link #doCreate(Object)}: Creates a new entity from the provided DTO.</li>
 *     <li>{@link #doUpdate(ID, Object)}: Updates an existing entity by its ID using the provided DTO.</li>
 *     <li>{@link #doDelete(ID)}: Deletes an entity by its ID.</li>
 * </ul>
 * Each of these operations is wrapped with corresponding hooks to enable custom logic during the process.
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <D>  Data transfer object (DTO) used for data manipulation
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 */
@Validated
public interface WriteService<M extends Persistable<ID>, D, ID> extends WriteOperations<M, D, ID> {

    /**
     * Gets the default hooks for the write operations.
     * <br>
     * The hooks provide additional behavior to be executed during the write operations.
     *
     * @return The default {@link WriteHooks} instance.
     */
    default WriteHooks<M, D, ID> getHooks() {
        return WriteHooks.getDefault();
    }

    /**
     * Creates a new entity from the provided DTO.
     * <br>
     * This method maps the DTO to a new entity, triggers the {@link WriteHooks#onBeforeCreate(D, M)} hook before saving,
     * and triggers the {@link WriteHooks#onAfterCreate(D, M)} hook after the entity is created.
     *
     * @param dto DTO containing data for creating the new entity
     * @return The created entity
     */
    @SneakyThrows
    default M doCreate(@Valid @NotNull D dto) {
        var entity = newEntity();
        mapModel(dto, entity);
        getHooks().onBeforeCreate(dto, entity);
        internalCreate(entity);
        getHooks().onAfterCreate(dto, entity);
        return entity;
    }

    /**
     * Updates an existing entity by its ID using the provided DTO.
     * <br>
     * This method maps the DTO to the existing entity, triggers the {@link WriteHooks#onBeforeUpdate(D, M)} hook before updating,
     * and triggers the {@link WriteHooks#onAfterUpdate(D, M)} hook after the entity is updated.
     *
     * @param id  ID of the entity to update
     * @param dto DTO containing updated data for the entity
     * @return The updated entity
     * @throws NotFoundEntityException if the entity is not found
     */
    @SneakyThrows
    default M doUpdate(@NotNull ID id, @Valid @NotNull D dto) throws NotFoundEntityException {
        var entity = internalFind(id);
        mapModel(dto, entity);
        getHooks().onBeforeUpdate(dto, entity);
        internalUpdate(entity);
        getHooks().onAfterUpdate(dto, entity);
        return entity;
    }

    /**
     * Deletes an entity by its ID.
     * <br>
     * This method triggers the {@link WriteHooks#onBeforeDelete(M)} hook before deleting,
     * and triggers the {@link WriteHooks#onAfterDelete(M)} hook after the entity is deleted.
     *
     * @param id ID of the entity to delete
     * @throws NotFoundEntityException if the entity is not found
     */
    default void doDelete(@NotNull ID id) throws NotFoundEntityException {
        var entity = internalFind(id);
        getHooks().onBeforeDelete(entity);
        internalDelete(entity);
        getHooks().onAfterDelete(entity);
    }

    /**
     * Creates a new entity instance.
     * <br>
     * This method is used to initialize a new entity object that will be mapped with the provided DTO data.
     *
     * @return A new instance of the entity
     */
    M newEntity();

    /**
     * Internal method to retrieve an entity by its ID.
     *
     * @param id ID of the entity to retrieve
     * @return The entity corresponding to the provided ID
     *
     * @throws NotFoundEntityException if the entity is not found
     */
    M internalFind(ID id) throws NotFoundEntityException;


    /**
     * Maps the data from the DTO to the entity model.
     *
     * @param dto  DTO containing the data to map
     * @param model Entity model to be populated with the DTO data
     */
    void mapModel(D dto, M model);

    /**
     * Internal method to create an entity.
     *
     * @param entity Entity to create
     */
    void internalCreate(M entity);

    /**
     * Internal method to update an entity.
     *
     * @param entity Entity to update
     */
    void internalUpdate(M entity);

    /**
     * Internal method to delete an entity.
     *
     * @param entity Entity to delete
     */
    void internalDelete(M entity);
}
