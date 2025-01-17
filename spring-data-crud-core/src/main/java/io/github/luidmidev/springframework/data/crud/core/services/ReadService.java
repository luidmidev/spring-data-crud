package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.RepositoryProvider;
import io.github.luidmidev.springframework.web.problemdetails.ApiError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Read Service
 *
 * @param <M>  Model
 * @param <ID> ID
 * @param <R>  Repositorys
 */
@Validated
public interface ReadService<M extends Persistable<ID>, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends
        HooksReadService<M, ID>,
        RepositoryProvider<R> {

    @Override
    default Page<M> internalPage(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    default M internalFind(ID id) {
        return getRepository().findById(id).orElseThrow(ApiError::notFound);
    }

    @Override
    default List<M> internalFind(List<ID> ids) {
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
