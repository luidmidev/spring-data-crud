package io.github.luidmidev.springframework.data.crud.core.services.hooks;

import org.springframework.data.domain.Persistable;

public interface CrudHooks<M extends Persistable<ID>, D, ID> extends ReadHooks<M, ID>, WriteHooks<M, D, ID> {

    CrudHooks<?, ?, ?> DEFAULT = new CrudHooks<>() {
    };

    @SuppressWarnings("unchecked")
    static <M extends Persistable<ID>, D, ID> CrudHooks<M, D, ID> getDefault() {
        return (CrudHooks<M, D, ID>) DEFAULT;
    }

}
