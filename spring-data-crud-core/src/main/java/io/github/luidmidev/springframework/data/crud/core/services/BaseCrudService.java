package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import io.github.luidmidev.springframework.data.crud.core.services.hooks.CrudServiceHooks;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Persistable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * CRUD Service
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 */
@Validated
public abstract class BaseCrudService<M extends Persistable<ID>, D, ID> extends BaseReadService<M, ID> implements CrudOperations<M, D, ID> {

    private final CrudServiceHooks<M, ID, D> hooks;

    protected BaseCrudService() {
        this.hooks = initializeHooks();
    }

    @Override
    @Transactional
    public M create(@Valid @NotNull D dto) {
        return doCreate(dto);
    }

    @Override
    @Transactional
    public M update(@NotNull ID id, @Valid @NotNull D dto) {
        return doUpdate(id, dto);
    }

    @Override
    @Transactional
    public void delete(ID id) {
        doDelete(id);
    }

    @SneakyThrows
    protected M doCreate(@Valid @NotNull D dto) {
        var entity = newEntity();
        mapModel(dto, entity);
        hooks.onBeforeCreate(dto, entity);
        internalCreate(entity);
        hooks.onAfterCreate(dto, entity);
        return entity;
    }

    @SneakyThrows
    protected M doUpdate(@NotNull ID id, @Valid @NotNull D dto) {
        var entity = doFind(id);
        mapModel(dto, entity);
        hooks.onBeforeUpdate(dto, entity);
        internalUpdate(entity);
        hooks.onAfterUpdate(dto, entity);
        return entity;
    }


    protected void doDelete(@NotNull ID id) {
        var entity = doFind(id);
        hooks.onBeforeDelete(entity);
        internalDelete(entity);
        hooks.onAfterDelete(entity);
    }


    protected abstract M newEntity();

    protected abstract void mapModel(D dto, M model);

    protected abstract void internalCreate(M entity);

    protected abstract void internalUpdate(M entity);

    protected abstract void internalDelete(M entity);

    @Override
    protected CrudServiceHooks<M, ID, D> initializeHooks() {
        return CrudServiceHooks.getDefault();
    }
}
