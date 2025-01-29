package io.github.luidmidev.springframework.data.crud.core.services.hooks;

import org.springframework.data.domain.Persistable;

/**
 * Interface for CRUD operation hooks.
 * <p>
 * This interface extends both {@link ReadHooks} and {@link WriteHooks}, providing hooks for both
 * read and write operations. It allows for custom logic to be executed before or after CRUD operations
 * (Create, Read, Update, Delete) on entities.
 * </p>
 *
 * @param <M>  Entity model that extends {@link Persistable} with the specified {@code ID}.
 * @param <D>  DTO (Data Transfer Object) used for input during write operations.
 * @param <ID> The type of the entity's identifier.
 */
public interface CrudHooks<M extends Persistable<ID>, D, ID> extends ReadHooks<M, ID>, WriteHooks<M, D, ID> {

    /**
     * Default implementation of the {@link CrudHooks} interface.
     * <p>
     * Provides default (empty) behavior for all the hook methods related to both read and write operations.
     * </p>
     */
    CrudHooks<?, ?, ?> DEFAULT = new CrudHooks<>() {
    };

    /**
     * Returns the default {@link CrudHooks} instance.
     * <p>
     * This static method allows access to the default (no-op) implementation of the {@link CrudHooks} interface,
     * which provides no custom logic for CRUD operations.
     * </p>
     *
     * @param <M>  Entity model that extends {@link Persistable} with the specified {@code ID}.
     * @param <D>  DTO (Data Transfer Object) used for input during write operations.
     * @param <ID> The type of the entity's identifier.
     * @return The default {@link CrudHooks} instance.
     */
    @SuppressWarnings("unchecked")
    static <M extends Persistable<ID>, D, ID> CrudHooks<M, D, ID> getDefault() {
        return (CrudHooks<M, D, ID>) DEFAULT;
    }
}