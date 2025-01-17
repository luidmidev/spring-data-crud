package io.github.luidmidev.springframework.data.crud.core.services.hooks;

import org.springframework.data.domain.Persistable;

public interface CrudServiceHooks<M extends Persistable<ID>, ID, D> extends ReadServiceHooks<M, ID> {

    CrudServiceHooks<?, ?, ?> DEFAULT = new CrudServiceHooks<>() {
    };

    @SuppressWarnings("unchecked")
    static <M extends Persistable<ID>, ID, D> CrudServiceHooks<M, ID, D> getDefault() {
        return (CrudServiceHooks<M, ID, D>) DEFAULT;
    }

    default void onBeforeCreate(D dto, M model) {
    }

    default void onBeforeUpdate(D dto, M model) {
    }

    default void onBeforeDelete(M model) {
    }

    default void onAfterCreate(D dto, M model) {
    }

    default void onAfterUpdate(D dto, M model) {
    }

    default void onAfterDelete(M model) {
    }

}
