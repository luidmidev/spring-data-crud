package io.github.luidmidev.springframework.data.crud.core;


import io.github.luidmidev.springframework.data.crud.core.providers.EntityClassProvider;
import io.github.luidmidev.springframework.data.crud.core.providers.RepositoryProvider;
import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface StandardReadService<E extends Persistable<ID>, ID, R extends ListCrudRepository<E, ID> & PagingAndSortingRepository<E, ID>> extends
        ReadService<E, ID>,
        EntityClassProvider<E>,
        RepositoryProvider<R> {

    @Override
    default Page<E> internalPage(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    default E internalFind(ID id) {
        return getRepository().findById(id).orElseThrow(() -> new NotFoundEntityException(getEntityClass(), id));
    }

    @Override
    default List<E> internalFind(List<ID> ids) {
        return getRepository().findAllById(ids);
    }


    @Override
    default long internalCount() {
        return getRepository().count();
    }

    @Override
    default boolean internalExists(ID id) {
        return getRepository().existsById(id);
    }
}
