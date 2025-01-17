package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.operations.WriteOperations;
import io.github.luidmidev.springframework.data.crud.core.services.hooks.WriteHooks;
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
public interface HooksWriteService<M extends Persistable<ID>, D, ID> extends WriteOperations<M, D, ID> {

    default WriteHooks<M, D, ID> getHooks() {
        return WriteHooks.getDefault();
    }

    @Override
    @Transactional
    default M create(@Valid @NotNull D dto) {
        return doCreate(dto);
    }

    @Override
    @Transactional
    default M update(@NotNull ID id, @Valid @NotNull D dto) {
        return doUpdate(id, dto);
    }

    @Override
    @Transactional
    default void delete(ID id) {
        doDelete(id);
    }

    @SneakyThrows
    default M doCreate(@Valid @NotNull D dto) {
        var entity = newEntity();
        mapModel(dto, entity);
        getHooks().onBeforeCreate(dto, entity);
        internalCreate(entity);
        getHooks().onAfterCreate(dto, entity);
        return entity;
    }

    @SneakyThrows
    default M doUpdate(@NotNull ID id, @Valid @NotNull D dto) {
        var entity = doFind(id);
        mapModel(dto, entity);
        getHooks().onBeforeUpdate(dto, entity);
        internalUpdate(entity);
        getHooks().onAfterUpdate(dto, entity);
        return entity;
    }


    default void doDelete(@NotNull ID id) {
        var entity = doFind(id);
        getHooks().onBeforeDelete(entity);
        internalDelete(entity);
        getHooks().onAfterDelete(entity);
    }


    M newEntity();

    M doFind(ID id);

    void mapModel(D dto, M model);

    void internalCreate(M entity);

    void internalUpdate(M entity);

    void internalDelete(M entity);
}
