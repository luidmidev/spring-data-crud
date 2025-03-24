package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import io.github.luidmidev.springframework.data.crud.core.services.hooks.ReadHooks;
import io.github.luidmidev.springframework.data.crud.core.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Read Service that extends {@link ReadOperations} to provide CRUD operations for entities with hooks for additional processing.
 * <br>
 * This service interacts with a model of type {@code M} which is a {@link Persistable} entity identified by {@code ID}.
 * It adds hook calls to each of the read operations, enabling additional logic such as logging, validation, or other custom operations before or after the main action.
 * <br>
 * The hooks are provided by the {@link ReadHooks} and can be customized to fit the needs of the application.
 * <br><br>
 * The service supports the following operations:
 * <ul>
 *     <li>{@link #doPage(String, Pageable, MultiValueMap)}: Retrieves a paginated list of entities based on search criteria and filters.</li>
 *     <li>{@link #doFind(ID)}: Retrieves an entity by its ID.</li>
 *     <li>{@link #doFind(List)}: Retrieves entities by a list of IDs.</li>
 *     <li>{@link #doCount()}: Retrieves the total count of entities.</li>
 *     <li>{@link #doExists(ID)}: Checks if an entity exists by its ID.</li>
 * </ul>
 * Each of these operations is wrapped with corresponding hooks to enable custom logic during the process.
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 */
@Validated
public interface ReadService<M extends Persistable<ID>, ID> extends ReadOperations<M, ID> {

    /**
     * Gets the default hooks for the read operations.
     * <br>
     * The hooks provide additional behavior to be executed during the read operations.
     *
     * @return The default {@link ReadHooks} instance.
     */
    default ReadHooks<M, ID> getHooks() {
        return ReadHooks.getDefault();
    }

    /**
     * Retrieves a paginated list of entities based on search criteria and filters.
     * <br>
     * This method normalizes the search query, resolves the appropriate page, and triggers the {@link ReadHooks#onPage(Page)} hook before returning the result.
     *
     * @param search  Search query for filtering results
     * @param pageable Pagination information
     * @param filters Additional filters for the query
     * @return A page of entities that match the search and filter criteria
     */
    @Override
    default Page<M> doPage(String search, Pageable pageable, MultiValueMap<String, String> filters) {
        var normalized = StringUtils.normalize(search);
        var page = resolvePage(normalized, pageable, filters);
        getHooks().onPage(page);
        return page;
    }

    /**
     * Retrieves an entity by its ID.
     * <br>
     * This method triggers the {@link ReadHooks#onFind(M)}} hook before returning the entity.
     *
     * @param id ID of the entity to retrieve
     * @return The entity corresponding to the provided ID
     */
    @Override
    default M doFind(ID id) {
        var model = internalFind(id);
        getHooks().onFind(model);
        return model;
    }

    /**
     * Retrieves entities by a list of IDs.
     * <br>
     * This method triggers the {@link ReadHooks#onFind(Iterable, Iterable)} hook before returning the list of entities.
     *
     * @param ids List of entity IDs to retrieve
     * @return A list of entities matching the provided IDs
     */
    @Override
    default List<M> doFind(List<ID> ids) {
        var list = internalFind(ids);
        getHooks().onFind(list, ids);
        return list;
    }

    /**
     * Retrieves the total count of entities.
     * <br>
     * This method triggers the {@link ReadHooks#onCount(long)} hook before returning the count.
     *
     * @return The total count of entities
     */
    @Override
    default long doCount() {
        var count = internalCount();
        getHooks().onCount(count);
        return count;
    }

    /**
     * Checks if an entity exists by its ID.
     * <br>
     * This method triggers the {@link ReadHooks#onExists(boolean, ID)} hook before returning the result.
     *
     * @param id ID of the entity to check
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    @Override
    default boolean doExists(ID id) {
        var exists = internalExists(id);
        getHooks().onExists(exists, id);
        return exists;
    }

    /**
     * Internal method to retrieve a paginated list of entities.
     *
     * @param pageable Pagination information
     * @return A page of entities
     */
    Page<M> internalPage(Pageable pageable);

    /**
     * Internal method to retrieve a paginated list of entities based on a search query.
     *
     * @param search Search query for filtering results
     * @param pageable Pagination information
     * @return A page of entities matching the search criteria
     */
    Page<M> internalSearch(String search, Pageable pageable);

    /**
     * Internal method to retrieve a paginated list of entities based on search query and filters.
     *
     * @param search Search query for filtering results
     * @param pageable Pagination information
     * @param filters Additional filters for the query
     * @return A page of entities matching the search and filter criteria
     */
    default Page<M> internalSearch(String search, Pageable pageable, MultiValueMap<String, String> filters) {
        return internalSearch(search, pageable);
    }

    /**
     * Internal method to retrieve an entity by its ID.
     *
     * @param id ID of the entity to retrieve
     * @return The entity corresponding to the provided ID
     */
    M internalFind(ID id) throws NotFoundEntityException;

    /**
     * Internal method to retrieve entities by a list of IDs.
     *
     * @param ids List of entity IDs to retrieve
     * @return A list of entities matching the provided IDs
     */
    List<M> internalFind(List<ID> ids);

    /**
     * Internal method to retrieve the total count of entities.
     *
     * @return The total count of entities
     */
    long internalCount();

    /**
     * Internal method to check if an entity exists by its ID.
     *
     * @param id ID of the entity to check
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    boolean internalExists(ID id);

    /**
     * Resolves the appropriate page of entities based on the search query, pageable, and filters.
     *
     * @param search  Search query for filtering results
     * @param pageable Pagination information
     * @param filters Additional filters for the query
     * @return A resolved page of entities
     */
    private Page<M> resolvePage(String search, Pageable pageable, MultiValueMap<String, String> filters) {
        if (filters == null || filters.isEmpty()) {
            return search == null ? internalPage(pageable) : internalSearch(search, pageable);
        } else {
            return internalSearch(search, pageable, filters);
        }
    }
}
