package io.github.luidmidev.springframework.data.crud.core.services.hooks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Persistable;

public interface ReadHooks<M extends Persistable<ID>, ID> {

    ReadHooks<?, ?> DEFAULT = new ReadHooks<>() {
    };

    @SuppressWarnings("unchecked")
    static <M extends Persistable<ID>, ID> ReadHooks<M, ID> getDefault() {
        return (ReadHooks<M, ID>) DEFAULT;
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