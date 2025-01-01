package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.NotFoundProvider;
import io.github.luidmidev.springframework.data.crud.core.operations.WriteOperations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;
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
@RequiredArgsConstructor
public abstract class WriteService<M extends Persistable<ID>, D, ID, R extends CrudRepository<M, ID>> extends NotFoundProvider<ID> implements WriteOperations<M, D, ID> {

    protected final R repository;
    protected final Class<M> domainClass;

    @SneakyThrows
    public M create(@Valid @NotNull D dto) {
        var model = domainClass.getConstructor().newInstance();
        mapModel(dto, model);
        onBeforeCreate(dto, model);
        var created = repository.save(model);
        onAfterCreate(dto, created);
        return created;
    }

    public M update(@NotNull ID id, @Valid @NotNull D dto) {
        var model = repository.findById(id).orElseThrow(() -> notFoundModel(domainClass.getSimpleName(), id));
        mapModel(dto, model);
        onBeforeUpdate(dto, model);
        var updated = repository.save(model);
        onAfterUpdate(dto, updated);
        return updated;
    }

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
