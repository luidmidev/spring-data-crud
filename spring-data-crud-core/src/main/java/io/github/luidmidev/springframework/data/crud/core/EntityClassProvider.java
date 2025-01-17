package io.github.luidmidev.springframework.data.crud.core;

public interface EntityClassProvider<M> {

    Class<M> getEntityClass();

}
