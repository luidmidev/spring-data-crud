package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.ModelClassProvider;
import io.github.luidmidev.springframework.data.crud.core.RepositoryProvider;
import lombok.SneakyThrows;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.validation.annotation.Validated;

/**
 * CRUD Service
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 * @param <R>  Repositorys
 */
@Validated
public interface WriteService<M extends Persistable<ID>, D, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends
        HooksWriteService<M, D, ID>,
        ModelClassProvider<M>,
        RepositoryProvider<R> {

    @Override
    default void internalCreate(M entity) {
        getRepository().save(entity);
    }

    @Override
    default void internalUpdate(M entity) {
        getRepository().save(entity);
    }

    @Override
    default void internalDelete(M entity) {
        getRepository().delete(entity);
    }

    @Override
    @SneakyThrows
    default M newEntity() {
        return getEntityClass().getConstructor().newInstance();
    }
}
