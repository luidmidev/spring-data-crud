package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.NotFoundProvider;
import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import io.github.luidmidev.springframework.data.crud.core.utils.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
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
public abstract class CrudService<M extends Persistable<ID>, D, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends NotFoundProvider<ID> implements CrudOperations<M, D, ID> {

    protected final R repository;
    protected final Class<M> domainClass;

    @SneakyThrows
    public M create(@Valid @NotNull D dto) {
        var model = domainClass.getConstructor().newInstance();
        mapModel(dto, model);
        onBeforeCreate(model);
        var created = repository.save(model);
        onAfterCreate(created);
        return created;
    }

    @Override
    public M update(@NotNull ID id, @Valid @NotNull D dto) {
        var model = repository.findById(id).orElseThrow(() -> notFoundModel(domainClass.getSimpleName(), id));
        mapModel(dto, model);
        onBeforeUpdate(model);
        var updated = repository.save(model);
        onAfterUpdate(updated);
        return updated;
    }

    @Override
    public void delete(ID id) {
        var model = repository.findById(id).orElseThrow(() -> notFoundModel(domainClass.getSimpleName(), id));
        onBeforeDelete(model);
        repository.delete(model);
        onAfterDelete(model);
    }

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
        return repository.findAllById(ids);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public boolean exists(ID id) {
        return repository.existsById(id);
    }

    protected abstract void mapModel(D dto, M model);

    protected abstract List<M> search(String search);

    protected abstract Page<M> search(String search, Pageable pageable);

    protected void onBeforeCreate(M model) {
    }

    protected void onBeforeUpdate(M model) {
    }

    protected void onBeforeDelete(M model) {
    }

    protected void onAfterCreate(M model) {
    }

    protected void onAfterUpdate(M model) {
    }

    protected void onAfterDelete(M model) {
    }

    protected void onFind(M model) {
    }

    protected void onList(List<M> models) {
    }

    protected void onPage(Page<M> page) {
    }

}
