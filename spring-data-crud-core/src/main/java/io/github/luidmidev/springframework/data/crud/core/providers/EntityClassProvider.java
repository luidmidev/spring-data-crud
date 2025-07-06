package io.github.luidmidev.springframework.data.crud.core.providers;


public interface EntityClassProvider<E> {

    Class<E> getEntityClass();
}