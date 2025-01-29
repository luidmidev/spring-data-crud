package io.github.luidmidev.springframework.data.crud.core.services;


import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Service for performing CRUD operations (Create, Read, Update, Delete) with repository support.
 * <p>
 * This service extends {@link RepositoryReadService} for reading operations, {@link RepositoryWriteService} for write operations,
 * and {@link CrudService} to integrate hooks for CRUD operations. It is designed to work with
 * Spring Data repositories for performing CRUD operations on the entity model.
 * </p>
 *
 * @param <M>  Entity model, which extends {@link Persistable} with an ID type of {@code ID}
 * @param <D>  Data Transfer Object (DTO) used for input data in create and update operations
 * @param <ID> Type of the entity's identifier (e.g., {@link Long}, {@link String})
 * @param <R>  Repository type, which extends {@link ListCrudRepository} for CRUD operations and
 *             {@link PagingAndSortingRepository} for pagination and sorting operations.
 */
public interface RepositoryCrudService<M extends Persistable<ID>, D, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends
        RepositoryReadService<M, ID, R>,
        RepositoryWriteService<M, D, ID, R>,
        CrudService<M, D, ID> {

    @Override
    default M internalFind(ID id) {
        return RepositoryReadService.super.internalFind(id);
    }
}
