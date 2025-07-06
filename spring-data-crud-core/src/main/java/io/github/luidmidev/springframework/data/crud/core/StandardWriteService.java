package io.github.luidmidev.springframework.data.crud.core;


import io.github.luidmidev.springframework.data.crud.core.providers.RepositoryProvider;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface StandardWriteService<E extends Persistable<ID>, D, ID, R extends ListCrudRepository<E, ID> & PagingAndSortingRepository<E, ID>> extends
        WriteService<E, D, ID>,
        RepositoryProvider<R> {

    @Override
    default void internalCreate(E entity) {
        getRepository().save(entity);
    }

    @Override
    default void internalUpdate(E entity) {
        getRepository().save(entity);
    }

    @Override
    default void internalDelete(E entity) {
        getRepository().delete(entity);
    }
}
