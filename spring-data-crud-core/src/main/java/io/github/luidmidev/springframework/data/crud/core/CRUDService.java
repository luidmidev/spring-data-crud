package io.github.luidmidev.springframework.data.crud.core;


import io.github.luidmidev.springframework.web.problemdetails.ApiError;
import io.github.luidmidev.springframework.web.problemdetails.ProblemDetailsException;
import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.function.Function;

/**
 * CRUD Service
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 * @param <R>  Repositorys
 */
@Log4j2
@Validated
public abstract class CRUDService<M extends CRUDModel<ID>, D, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> implements ReadOperations<M, ID> {

    protected final R repository;

    protected final Class<M> domainClass;

    @Setter(AccessLevel.PROTECTED)
    private Function<ID, String> notFoundMessageResolver = id -> "Not found with id: " + id;


    protected CRUDService(R repository, Class<M> domainClass) {
        this.repository = repository;
        this.domainClass = domainClass;
    }

    protected abstract void mapModel(D dto, M model, MapAction action);

    @SneakyThrows
    public M create(@Valid @NotNull D dto) {
        var model = domainClass.getConstructor().newInstance();
        mapModel(dto, model, MapAction.CREATE);
        onBeforeCreate(model);
        var created = repository.save(model);
        onAfterCreate(created);
        return created;
    }

    public M update(@NotNull ID id, @Valid @NotNull D dto) {
        var model = repository.findById(id).orElseThrow(() -> notFoundModel(id));
        mapModel(dto, model, MapAction.UPDATE);
        onBeforeUpdate(model);
        var updated = repository.save(model);
        onAfterUpdate(updated);
        return updated;
    }

    public List<M> list() {
        return repository.findAll();
    }

    public Page<M> page(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void delete(ID id) {
        var model = repository.findById(id).orElseThrow(() -> notFoundModel(id));
        onBeforeDelete(model);
        repository.delete(model);
        onAfterDelete(model);
    }

    public M find(ID id) {
        var model = repository.findById(id).orElseThrow(() -> notFoundModel(id));
        onFind(model);
        return model;
    }


    public abstract Page<M> search(String search, Pageable pageable);

    public abstract List<M> search(String search);


    public boolean exists(ID id) {
        return repository.existsById(id);
    }

    public boolean existsAll(List<ID> ids) {
        var notFoundIds = ids.stream().filter(id -> !repository.existsById(id)).toList();
        return notFoundIds.isEmpty();
    }

    @SuppressWarnings("unused")
    protected void onBeforeCreate(M model) {
    }

    @SuppressWarnings("unused")
    protected void onBeforeUpdate(M model) {
    }

    @SuppressWarnings("unused")
    protected void onBeforeDelete(M model) {
    }

    @SuppressWarnings("unused")
    protected void onAfterCreate(M model) {
    }

    @SuppressWarnings("unused")
    protected void onAfterUpdate(M model) {
    }

    @SuppressWarnings("unused")
    protected void onAfterDelete(M model) {
    }

    @SuppressWarnings("unused")
    protected void onFind(M model) {
    }


    protected ProblemDetailsException notFoundModel(ID id) {
        return ApiError.notFound(notFoundMessageResolver.apply(id));
    }


    protected enum MapAction {
        CREATE, UPDATE
    }
}
