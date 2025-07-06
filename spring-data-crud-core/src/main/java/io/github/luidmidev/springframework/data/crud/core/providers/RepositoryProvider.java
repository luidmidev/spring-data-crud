package io.github.luidmidev.springframework.data.crud.core.providers;


public interface RepositoryProvider<R> {

    R getRepository();
}