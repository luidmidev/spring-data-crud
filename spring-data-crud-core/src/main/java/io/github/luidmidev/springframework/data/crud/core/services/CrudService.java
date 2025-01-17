package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.web.problemdetails.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * CRUD Service
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 * @param <R>  Repositorys
 */
@Validated
@RequiredArgsConstructor
public abstract class CrudService<M extends Persistable<ID>, D, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends BaseCrudService<M, D, ID> {

    protected final R repository;

    @Override
    protected Page<M> internalPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    protected M internalFind(ID id) {
        return repository.findById(id).orElseThrow(ApiError::notFound);
    }

    @Override
    protected List<M> internalFind(List<ID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    protected long internalCount() {
        return repository.count();
    }

    @Override
    protected boolean internalExists(ID id) {
        return repository.existsById(id);
    }

    @Override
    protected void internalCreate(M entity) {
        repository.save(entity);
    }

    @Override
    protected void internalUpdate(M entity) {
        repository.save(entity);
    }

    @Override
    protected void internalDelete(M entity) {
        repository.delete(entity);
    }
}
