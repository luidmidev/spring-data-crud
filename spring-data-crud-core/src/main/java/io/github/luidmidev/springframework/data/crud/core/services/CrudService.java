package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import io.github.luidmidev.springframework.data.crud.core.services.hooks.CrudHooks;
import org.springframework.data.domain.Persistable;

/**
 * CRUD Service that combines both read and write operations, extending both {@link ReadService} and {@link WriteService}.
 * This service provides a complete set of CRUD operations for entities of type {@code M} (which extends {@link Persistable} with an {@code ID} type),
 * as well as hooks to allow additional processing before or after each operation.
 * <br>
 * The service automatically provides hooks for these operations via {@link CrudHooks}.
 * <br>
 * This service can be used in scenarios where both reading and writing operations are required with customizable logic through hooks.
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <D>  Data transfer object (DTO) used for data manipulation
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 */
public interface CrudService<M extends Persistable<ID>, D, ID> extends
        ReadService<M, ID>,
        WriteService<M, D, ID>,
        CrudOperations<M, D, ID> {

    /**
     * Gets the default CRUD hooks for the operations.
     * <br>
     * These hooks allow custom logic to be executed before or after CRUD operations.
     *
     * @return The default {@link CrudHooks} instance.
     */
    @Override
    default CrudHooks<M, D, ID> getHooks() {
        return CrudHooks.getDefault();
    }
}
