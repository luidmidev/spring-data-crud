package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.EntityClassProvider;
import io.github.luidmidev.springframework.data.crud.core.RepositoryProvider;
import lombok.SneakyThrows;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.validation.annotation.Validated;

/**
 * Service for performing CRUD operations on a repository.
 * <p>
 * This service extends {@link WriteService} to provide the necessary hooks for write operations (create, update, delete)
 * and integrates with Spring Data repositories to persist, update, and delete entities.
 * It supports creating new entities, updating existing ones, and deleting entities from the repository.
 * </p>
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <D>  Data Transfer Object (DTO) used for input data in create and update operations
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 * @param <R>  Repository type, which extends {@link ListCrudRepository} for CRUD operations and
 *             {@link PagingAndSortingRepository} for pagination and sorting operations.
 */
@Validated
public interface RepositoryWriteService<M extends Persistable<ID>, D, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends
        WriteService<M, D, ID>,
        EntityClassProvider<M>,
        RepositoryProvider<R> {

    /**
     * Saves the given entity to the repository (create operation).
     * <p>
     * This method delegates to {@link ListCrudRepository#save(Object)} to save the entity.
     * </p>
     *
     * @param entity The entity to be created in the repository
     */
    @Override
    default void internalCreate(M entity) {
        getRepository().save(entity);
    }

    /**
     * Saves the given entity to the repository (update operation).
     * <p>
     * This method delegates to {@link ListCrudRepository#save(Object)} to update the entity.
     * </p>
     *
     * @param entity The entity to be updated in the repository
     */
    @Override
    default void internalUpdate(M entity) {
        getRepository().save(entity);
    }

    /**
     * Deletes the given entity from the repository.
     * <p>
     * This method delegates to {@link ListCrudRepository#delete(Object)} to delete the entity.
     * </p>
     *
     * @param entity The entity to be deleted from the repository
     */
    @Override
    default void internalDelete(M entity) {
        getRepository().delete(entity);
    }

    /**
     * Creates a new instance of the entity class.
     * <p>
     * This method uses reflection to instantiate a new object of the entity class by calling
     * the no-argument constructor.
     * </p>
     *
     * @return A new instance of the entity class
     */
    @Override
    @SneakyThrows
    default M newEntity() {
        return getEntityClass().getConstructor().newInstance();
    }
}
