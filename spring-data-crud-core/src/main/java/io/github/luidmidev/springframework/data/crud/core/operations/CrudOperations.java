package io.github.luidmidev.springframework.data.crud.core.operations;


import org.springframework.data.domain.Persistable;
import org.springframework.validation.annotation.Validated;

/**
 * Interface that combines CRUD (Create, Read, Update, Delete) operations for an entity model.
 * <br>
 * This interface extends {@link ReadOperations} and {@link WriteOperations}, providing a complete set of
 * operations for managing entities, including reading, writing, and other CRUD functionalities.
 * <br><br>
 * The operations allow for creating, reading, updating, and deleting entities in a persistent storage.
 * The {@code M} type represents the entity model, the {@code D} type represents the Data Transfer Object (DTO) used for
 * interacting with the entity in the application layer, and the {@code ID} type represents the identifier of the entity.
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <D>  Data Transfer Object (DTO) used for passing entity data
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 */
@Validated
public non-sealed interface CrudOperations<M extends Persistable<ID>, D, ID> extends ReadOperations<M, ID>, WriteOperations<M, D, ID>, Crud {
}
