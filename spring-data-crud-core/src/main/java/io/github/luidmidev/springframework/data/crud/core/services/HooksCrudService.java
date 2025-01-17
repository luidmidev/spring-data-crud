package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import io.github.luidmidev.springframework.data.crud.core.services.hooks.CrudHooks;
import org.springframework.data.domain.Persistable;
import org.springframework.validation.annotation.Validated;

/**
 * Read Service
 *
 * @param <M>  Model
 * @param <ID> ID
 */
@Validated
public interface HooksCrudService<M extends Persistable<ID>, D, ID> extends
        HooksReadService<M, ID>,
        HooksWriteService<M, D, ID>,
        CrudOperations<M, D, ID> {

    @Override
    default CrudHooks<M, D, ID> getHooks() {
        return CrudHooks.getDefault();
    }

    @Override
    default M doFind(ID id) {
        return HooksReadService.super.doFind(id);
    }
}
