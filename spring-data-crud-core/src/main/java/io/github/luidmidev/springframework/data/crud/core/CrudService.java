package io.github.luidmidev.springframework.data.crud.core;


import io.github.luidmidev.springframework.data.crud.core.hooks.CrudHooks;
import org.springframework.data.domain.Persistable;

public non-sealed interface CrudService<E extends Persistable<ID>, D, ID> extends
        Crud,
        ReadService<E, ID>,
        WriteService<E, D, ID> {

    @Override
    default CrudHooks<E, D, ID> getHooks() {
        return CrudHooks.getDefault();
    }
}
