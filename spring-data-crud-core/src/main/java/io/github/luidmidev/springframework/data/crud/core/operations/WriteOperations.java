package io.github.luidmidev.springframework.data.crud.core.operations;


import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.security.AuthorizationCrudManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Persistable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Operations for performing CRUD (Create, Read, Update, Delete) actions on an entity.
 * <br>
 * These methods are protected by {@link AuthorizationCrudManager} and require authorization checks via {@link PreAuthorize}
 * to ensure the calling user has the appropriate permissions before performing the operation.
 * <br><br>
 * The {@code doCreate}, {@code doUpdate}, and {@code doDelete} methods are the core operations for manipulating entities,
 * but are not directly exposed to external calls. They are used internally by the corresponding interface methods with
 * security and transaction management applied.
 * <br><br>
 * The rule java:S6809 is disabled because the method invocation occurs within the same class, which would
 * normally bypass the proxy mechanism that Spring uses for transaction management (i.e., @Transactional).
 * <br><br>
 * In this case, we are aware of the limitation but have made a conscious design decision. The methods
 * {@code doCreate}, {@code doUpdate}, and {@code doDelete} are explicitly annotated with @Transactional
 * to ensure that transaction management is applied directly to those methods. Furthermore, the methods
 * within the interface {@code WriteOperations} are intended to serve as a high-level interface for CRUD operations
 * and include additional security via {@link PreAuthorize} and custom authorization checks.
 * <br><br>
 * Since these methods are designed to be implemented by concrete service classes, which are expected to be
 * proxied by Spring at runtime, the direct invocation via `this` within the interface methods will not
 * bypass Spring's proxy and will maintain the transactional behavior. Therefore, the rule is suppressed
 * for this specific case.
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <D>  Data Transfer Object (DTO) used for passing entity data
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 */
@Validated
public non-sealed interface WriteOperations<M extends Persistable<ID>, D, ID> extends Crud {

    /**
     * Creates a new entity.
     * <br>
     * This method is protected by {@link AuthorizationCrudManager} to ensure that the user has permission
     * to create an entity and is further secured by {@link PreAuthorize} to validate that the user is authorized
     * to perform the 'CREATE' action.
     *
     * @param dto The Data Transfer Object (DTO) containing the data to create the entity.
     * @return The newly created entity.
     * @throws AccessDeniedException If the user is not authorized to create the entity.
     */
    @SuppressWarnings("java:S6809")
    @Transactional
    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'CREATE')")
    default M create(@Valid @NotNull D dto) {
        return doCreate(dto);
    }

    /**
     * Updates an existing entity.
     * <br>
     * This method is protected by {@link AuthorizationCrudManager} to ensure that the user has permission
     * to update an entity, and is further secured by {@link PreAuthorize} to validate that the user is authorized
     * to perform the 'UPDATE' action.
     * <br>
     * The method updates the entity with the provided ID, ensuring that all the necessary fields from the DTO are
     * properly applied.
     *
     * @param id The ID of the entity to update.
     * @param dto The Data Transfer Object (DTO) containing the data to update the entity.
     * @return The updated entity.
     * @throws NotFoundEntityException If the entity to update is not found.
     * @throws AccessDeniedException If the user is not authorized to update the entity.
     */
    @SuppressWarnings("java:S6809")
    @Transactional
    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'UPDATE')")
    default M update(@NotNull ID id, @Valid @NotNull D dto) throws NotFoundEntityException {
        return doUpdate(id, dto);
    }

    /**
     * Deletes an existing entity.
     * <br>
     * This method is protected by {@link AuthorizationCrudManager} to ensure that the user has permission
     * to delete the entity, and is further secured by {@link PreAuthorize} to validate that the user is authorized
     * to perform the 'DELETE' action.
     *
     * @param id The ID of the entity to delete.
     * @throws AccessDeniedException If the user is not authorized to delete the entity.
     */
    @SuppressWarnings("java:S6809")
    @Transactional
    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'DELETE')")
    default void delete(@NotNull ID id) {
        doDelete(id);
    }

    /**
     * Creates a new entity without additional protection.
     * <br>
     * This method is unprotected and serves as the core implementation for creating an entity.
     * It is called internally by the {@link WriteOperations#create} method after authorization checks are performed.
     *
     * @param dto The Data Transfer Object (DTO) containing the data to create the entity.
     * @return The newly created entity.
     */
    @Transactional
    M doCreate(@Valid @NotNull D dto);

    /**
     * Updates an existing entity without additional protection.
     * <br>
     * This method is unprotected and serves as the core implementation for updating an entity.
     * It is called internally by the {@link WriteOperations#update} method after authorization checks are performed.
     *
     * @param id The ID of the entity to update.
     * @param dto The Data Transfer Object (DTO) containing the data to update the entity.
     * @return The updated entity.
     * @throws NotFoundEntityException If the entity to update is not found.
     */
    @Transactional
    M doUpdate(@NotNull ID id, @Valid @NotNull D dto) throws NotFoundEntityException;

    /**
     * Deletes an existing entity without additional protection.
     * <br>
     * This method is unprotected and serves as the core implementation for deleting an entity.
     * It is called internally by the {@link WriteOperations#delete} method after authorization checks are performed.
     *
     * @param id The ID of the entity to delete.
     * @throws NotFoundEntityException If the entity to delete is not found.
     */
    @Transactional
    void doDelete(@NotNull ID id) throws NotFoundEntityException;
}
