package io.github.luidmidev.springframework.data.crud.core.http.controllers;


import io.github.luidmidev.springframework.data.crud.core.ServiceProvider;
import io.github.luidmidev.springframework.data.crud.core.SpringDataCrudAutoConfiguration;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * CRUD Controller for performing read operations.
 * <p>
 * This controller provides endpoints for reading and retrieving data, such as fetching a list of entities with pagination,
 * finding an entity by its ID, counting entities, and checking if an entity exists. It uses the service defined in
 * {@link ReadOperations} to delegate the actual read operations.
 * </p>
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 * @param <S>  Service that extends {@link ReadOperations} for the read operations
 */
public interface ReadController<M extends Persistable<ID>, ID, S extends ReadOperations<M, ID>> extends ServiceProvider<S> {

    /**
     * Endpoint to retrieve a paginated list of entities, with optional search and filter parameters.
     * <p>
     * The pagination is handled using {@link Pageable} and search/filter parameters are passed in the request.
     * </p>
     *
     * @param search Optional search string to filter entities based on a search term
     * @param filters Optional filters as a multi-value map for further refining the results
     * @param pageable Pageable object to define pagination details (e.g., page number, page size)
     * @return A paginated list of entities matching the search and filter criteria
     */
    @GetMapping
    default ResponseEntity<Page<M>> page(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MultiValueMap<String, String> filters,
            @PageableDefault Pageable pageable
    ) {
        var ignoreParams = SpringDataCrudAutoConfiguration.getIgnoreParams();
        if (ignoreParams != null) ignoreParams.forEach(filters::remove);
        return ResponseEntity.ok(getService().page(search, pageable, filters));
    }

    /**
     * Endpoint to retrieve an entity by its unique identifier.
     *
     * @param id The unique identifier of the entity to retrieve
     * @return The entity corresponding to the provided ID
     */
    @GetMapping("/{id}")
    default ResponseEntity<M> find(@PathVariable ID id) {
        return ResponseEntity.ok(getService().find(id));
    }

    /**
     * Endpoint to retrieve a list of entities by their unique identifiers.
     *
     * @param ids The list of IDs for the entities to retrieve
     * @return A list of entities corresponding to the provided IDs
     */
    @GetMapping("/ids")
    default ResponseEntity<List<M>> find(@RequestParam List<ID> ids) {
        return ResponseEntity.ok(getService().find(ids));
    }

    /**
     * Endpoint to retrieve the total count of entities.
     *
     * @return The total number of entities in the repository
     */
    @GetMapping("/count")
    default ResponseEntity<Long> count() {
        return ResponseEntity.ok(getService().count());
    }

    /**
     * Endpoint to check if an entity exists by its unique identifier.
     *
     * @param id The unique identifier of the entity to check
     * @return {@code true} if the entity exists, {@code false} otherwise
     */
    @GetMapping("/exists")
    default ResponseEntity<Boolean> exists(@RequestParam ID id) {
        return ResponseEntity.ok(getService().exists(id));
    }
}
