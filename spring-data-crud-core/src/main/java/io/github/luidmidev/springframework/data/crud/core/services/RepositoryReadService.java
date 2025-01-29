package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.EntityClassProvider;
import io.github.luidmidev.springframework.data.crud.core.RepositoryProvider;
import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Service for performing read operations on a repository.
 * <p>
 * This service extends {@link ReadService} to provide the necessary hooks for reading entities and integrates
 * with Spring Data repositories to access data. It supports pagination, finding entities by ID, counting entities,
 * and checking for the existence of entities.
 * </p>
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 * @param <R>  Repository type, which extends {@link ListCrudRepository} for CRUD operations and
 *             {@link PagingAndSortingRepository} for pagination and sorting operations.
 */
public interface RepositoryReadService<M extends Persistable<ID>, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends
        ReadService<M, ID>,
        EntityClassProvider<M>,
        RepositoryProvider<R> {

    /**
     * Retrieves all entities, with pagination applied.
     * <p>
     * This method delegates to {@link PagingAndSortingRepository#findAll(Pageable)} and is used to retrieve
     * a page of entities.
     * </p>
     *
     * @param pageable Paging information
     * @return A {@link Page} of entities
     */
    @Override
    default Page<M> internalPage(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    /**
     * Retrieves an entity by its ID.
     * <p>
     * This method delegates to {@link ListCrudRepository#findById(ID)} and throws a {@link NotFoundEntityException}
     * if the entity is not found.
     * </p>
     *
     * @param id ID of the entity to retrieve
     * @return The entity corresponding to the provided ID
     * @throws NotFoundEntityException If the entity is not found
     */
    @Override
    default M internalFind(ID id) {
        return getRepository().findById(id).orElseThrow(() -> new NotFoundEntityException(getEntityClass(), id));
    }

    /**
     * Retrieves a list of entities by their IDs.
     * <p>
     * This method delegates to {@link ListCrudRepository#findAllById(Iterable)}.
     * </p>
     *
     * @param ids List of entity IDs to retrieve
     * @return A list of entities corresponding to the provided IDs
     */
    @Override
    default List<M> internalFind(List<ID> ids) {
        return getRepository().findAllById(ids);
    }

    /**
     * Retrieves the total number of entities.
     * <p>
     * This method delegates to {@link ListCrudRepository#count()} to get the total count of entities in the repository.
     * </p>
     *
     * @return The total count of entities
     */
    @Override
    default long internalCount() {
        return getRepository().count();
    }

    /**
     * Checks whether an entity exists by its ID.
     * <p>
     * This method delegates to {@link ListCrudRepository#existsById(ID)} to check if an entity with the given ID exists.
     * </p>
     *
     * @param id ID of the entity to check for existence
     * @return {@code true} if the entity exists, otherwise {@code false}
     */
    @Override
    default boolean internalExists(ID id) {
        return getRepository().existsById(id);
    }
}
