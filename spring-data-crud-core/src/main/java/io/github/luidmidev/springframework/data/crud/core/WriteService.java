package io.github.luidmidev.springframework.data.crud.core;


import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.providers.EntityClassProvider;
import io.github.luidmidev.springframework.data.crud.core.providers.TransactionOperationsProvider;
import io.github.luidmidev.springframework.data.crud.core.hooks.WriteHooks;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.data.domain.Persistable;
import org.springframework.validation.annotation.Validated;

@Validated
public non-sealed interface WriteService<E extends Persistable<ID>, D, ID> extends
        Crud,
        EntityClassProvider<E>,
        TransactionOperationsProvider {

    default WriteHooks<E, D, ID> getHooks() {
        return WriteHooks.getDefault();
    }

    default E create(@Valid @NotNull D dto) {
        Crud.preProccess(this, CrudOperation.CREATE);

        var entity = newEntity();
        var hooks = getHooks();
        var transactionOperations = getTransactionOperations();

        return transactionOperations.execute(status -> {
            try {
                mapModel(dto, entity);
                hooks.onBeforeCreate(dto, entity);
                internalCreate(entity);
                hooks.onAfterCreate(dto, entity);
                return entity;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    default E update(@NotNull ID id, @Valid @NotNull D dto) throws NotFoundEntityException {
        Crud.preProccess(this, CrudOperation.UPDATE);

        var entity = internalFind(id);
        var hooks = getHooks();
        var transactionOperations = getTransactionOperations();

        return transactionOperations.execute(status -> {
            try {
                mapModel(dto, entity);
                hooks.onBeforeUpdate(dto, entity);
                internalUpdate(entity);
                hooks.onAfterUpdate(dto, entity);
                return entity;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    default void delete(@NotNull ID id) throws NotFoundEntityException {
        Crud.preProccess(this, CrudOperation.DELETE);

        var entity = internalFind(id);
        var hooks = getHooks();

        hooks.onBeforeDelete(entity);
        internalDelete(entity);
        hooks.onAfterDelete(entity);
    }


    @SneakyThrows
    default E newEntity() {
        return getEntityClass().getConstructor().newInstance();
    }

    E internalFind(ID id) throws NotFoundEntityException;

    void mapModel(D dto, E model);

    void internalCreate(E entity);

    void internalUpdate(E entity);

    void internalDelete(E entity);
}
