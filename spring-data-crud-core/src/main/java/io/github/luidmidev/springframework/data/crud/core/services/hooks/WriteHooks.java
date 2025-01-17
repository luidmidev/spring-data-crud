package io.github.luidmidev.springframework.data.crud.core.services.hooks;

import org.springframework.data.domain.Persistable;

public interface WriteHooks<M extends Persistable<ID>, D, ID> {

    WriteHooks<?, ?, ?> DEFAULT = new WriteHooks<>() {
    };

    @SuppressWarnings("unchecked")
    static <M extends Persistable<ID>, D, ID> WriteHooks<M, D, ID> getDefault() {
        return (WriteHooks<M, D, ID>) DEFAULT;
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
