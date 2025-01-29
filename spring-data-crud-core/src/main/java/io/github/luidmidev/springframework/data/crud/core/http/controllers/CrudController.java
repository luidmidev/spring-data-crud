package io.github.luidmidev.springframework.data.crud.core.http.controllers;

import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import org.springframework.data.domain.Persistable;

/**
 * CRUD Controller for performing both read and write operations.
 * <p>
 * This controller combines both read and write operations. It provides endpoints for creating, updating, deleting,
 * and retrieving entities. It uses the services defined in {@link CrudOperations} for delegating the actual operations.
 * </p>
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <D>  DTO (Data Transfer Object) used to transfer data for create and update operations
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 * @param <S>  Service that extends {@link CrudOperations} for the CRUD operations
 */
public interface CrudController<M extends Persistable<ID>, D, ID, S extends CrudOperations<M, D, ID>> extends
        WriteController<M, D, ID, S>,
        ReadController<M, ID, S> {
}
