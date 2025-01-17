package io.github.luidmidev.springframework.data.crud.core.services.hooks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;

public interface ReadServiceHooks<M extends Persistable<ID>, ID> {

    ReadServiceHooks<?, ?> DEFAULT = new ReadServiceHooks<>() {
    };

    @SuppressWarnings("unchecked")
    static <M extends Persistable<ID>, ID> ReadServiceHooks<M, ID> getDefault() {
        return (ReadServiceHooks<M, ID>) DEFAULT;
    }

    default void onFind(M entity) {
    }

    default void onFind(Iterable<M> entities, Iterable<ID> ids) {
    }

    default void onCount(long count) {
    }

    default void onExists(boolean exists, ID id) {
    }

    default void onPage(Page<M> page) {
    }
}