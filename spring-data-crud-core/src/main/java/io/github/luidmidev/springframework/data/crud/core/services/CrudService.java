package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
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
public abstract class CrudService<M extends Persistable<ID>, D, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends ReadService<M, ID, R> implements CrudOperations<M, D, ID> {


    protected CrudService(R repository, Class<M> domainClass) {
        super(repository, domainClass);
    }

    @SneakyThrows
    public M create(@Valid @NotNull D dto) {
        var model = domainClass.getConstructor().newInstance();
        mapModel(dto, model);
        onBeforeCreate(dto, model);
        var created = repository.save(model);
        onAfterCreate(dto, created);
        return created;
    }

    @Override
    public M update(@NotNull ID id, @Valid @NotNull D dto) {
        var model = repository.findById(id).orElseThrow(() -> notFoundModel(domainClass.getSimpleName(), id));
        mapModel(dto, model);
        onBeforeUpdate(dto, model);
        var updated = repository.save(model);
        onAfterUpdate(dto, updated);
        return updated;
    }

    @Override
    public void delete(ID id) {
        var model = repository.findById(id).orElseThrow(() -> notFoundModel(domainClass.getSimpleName(), id));
        onBeforeDelete(model);
        repository.delete(model);
        onAfterDelete(model);
    }

    protected abstract void mapModel(D dto, M model);

    protected void onBeforeCreate(D dto, M model) {
    }

    protected void onBeforeUpdate(D dto, M model) {
    }

    protected void onBeforeDelete(M model) {
    }

    protected void onAfterCreate(D dto, M model) {
    }

    protected void onAfterUpdate(D dto, M model) {
    }

    protected void onAfterDelete(M model) {
    }
}
