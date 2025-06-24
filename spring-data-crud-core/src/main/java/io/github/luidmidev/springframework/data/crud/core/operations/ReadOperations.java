package io.github.luidmidev.springframework.data.crud.core.operations;

import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.security.AuthorizationCrudManager;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Operations for reading entities from the data store.
 * <br>
 * These methods allow for retrieving individual entities, performing searches with pagination and filtering,
 * and checking the existence of entities. The operations are protected by authorization checks via {@link AuthorizationCrudManager}
 * and {@link PreAuthorize}.
 * <br><br>
 * The {@code doPage}, {@code doFind}, {@code doFind(ids)}, {@code doCount}, and {@code doExists} methods are
 * used to interact directly with the data store. The corresponding public methods in this interface are
 * protected by authorization logic to ensure only authorized users can perform these operations.
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 */
@Validated
public non-sealed interface ReadOperations<M, ID> extends Crud {

    /**
     * Retrieves a paginated list of entities based on search criteria and filters.
     * <br>
     * This method is protected by {@link AuthorizationCrudManager} and {@link PreAuthorize}.
     *
     * @param search   Search query for filtering results
     * @param pageable Pagination information
     * @param filters  Additional filters for the query
     * @return A page of entities that match the search and filter criteria
     */
    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'PAGE')")
    default Page<M> page(String search, Pageable pageable, MultiValueMap<String, String> filters) {
        return doPage(search, pageable, filters);
    }

    /**
     * Retrieves an entity by its ID.
     * <br>
     * This method is protected by {@link AuthorizationCrudManager} and {@link PreAuthorize}.
     *
     * @param id ID of the entity to retrieve
     * @return The entity corresponding to the provided ID
     * @throws NotFoundEntityException If the entity with the provided ID does not exist
     */
    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'FIND')")
    default M find(@NotNull ID id) throws NotFoundEntityException {
        return doFind(id);
    }

    /**
     * Retrieves entities by a list of IDs.
     * <br>
     * This method is protected by {@link AuthorizationCrudManager} and {@link PreAuthorize}.
     *
     * @param ids List of entity IDs to retrieve
     * @return A list of entities matching the provided IDs, or an empty list if none found
     */
    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'FIND')")
    default List<M> find(@NotEmpty List<@NotNull ID> ids) {
        return doFind(ids);
    }

    /**
     * Retrieves the total count of entities.
     * <br>
     * This method is protected by {@link AuthorizationCrudManager} and {@link PreAuthorize}.
     *
     * @return The total count of entities
     */
    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'COUNT')")
    default long count() {
        return doCount();
    }

    /**
     * Checks if an entity exists by its ID.
     * <br>
     * This method is protected by {@link AuthorizationCrudManager} and {@link PreAuthorize}.
     *
     * @param id ID of the entity to check
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'EXISTS')")
    default boolean exists(@NotNull ID id) {
        return doExists(id);
    }

    /**
     * Retrieves a paginated list of entities based on search criteria and filters.
     * <br>
     * This method is not protected by authorization checks.
     *
     * @param search   Search query for filtering results
     * @param pageable Pagination information
     * @param filters  Additional filters for the query
     * @return A page of entities that match the search and filter criteria
     */
    Page<M> doPage(String search, Pageable pageable, MultiValueMap<String, String> filters);

    /**
     * Retrieves an entity by its ID.
     * <br>
     * This method is not protected by authorization checks.
     *
     * @param id ID of the entity to retrieve
     * @return The entity corresponding to the provided ID
     * @throws NotFoundEntityException If the entity with the provided ID does not exist
     */
    M doFind(@NotNull ID id) throws NotFoundEntityException;

    /**
     * Retrieves entities by a list of IDs.
     * <br>
     * This method is not protected by authorization checks.
     *
     * @param ids List of entity IDs to retrieve
     * @return A list of entities matching the provided IDs, or an empty list if none found
     */
    List<M> doFind(@NotEmpty List<@NotNull ID> ids);

    /**
     * Retrieves the total count of entities.
     * <br>
     * This method is not protected by authorization checks.
     *
     * @return The total count of entities
     */
    long doCount();

    /**
     * Checks if an entity exists by its ID.
     * <br>
     * This method is not protected by authorization checks.
     *
     * @param id ID of the entity to check
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    boolean doExists(@NotNull ID id);
}
