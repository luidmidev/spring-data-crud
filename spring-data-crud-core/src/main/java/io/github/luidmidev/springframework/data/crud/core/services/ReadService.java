package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.NotFoundProvider;
import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import io.github.luidmidev.springframework.data.crud.core.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Validated
@RequiredArgsConstructor
public abstract class ReadService<M extends Persistable<ID>, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends NotFoundProvider<ID> implements ReadOperations<M, ID> {

    protected final R repository;
    protected final Class<M> domainClass;

    @Override
    public List<M> list(String search) {
        var list = StringUtils.isNullOrEmpty(search) ? repository.findAll() : search(search);
        onList(list);
        return list;
    }

    @Override
    public Page<M> page(String search, Pageable pageable) {
        var page = StringUtils.isNullOrEmpty(search) ? repository.findAll(pageable) : search(search, pageable);
        onPage(page);
        return page;
    }

    @Override
    public M find(ID id) {
        var model = repository.findById(id).orElseThrow(() -> notFoundModel(domainClass.getSimpleName(), id));
        onFind(model);
        return model;
    }

    @Override
    public List<M> find(List<ID> ids) {
        var list = repository.findAllById(ids);
        onList(list);
        return list;
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public boolean exists(ID id) {
        return repository.existsById(id);
    }

    protected abstract List<M> search(String search);

    protected abstract Page<M> search(String search, Pageable pageable);

    protected void onFind(M model) {
    }

    protected void onList(List<M> models) {
    }

    protected void onPage(Page<M> page) {
    }
}
