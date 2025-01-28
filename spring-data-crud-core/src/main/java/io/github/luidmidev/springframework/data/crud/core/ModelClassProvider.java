package io.github.luidmidev.springframework.data.crud.core;

public interface ModelClassProvider<M> {
    Class<M> getEntityClass();
}
