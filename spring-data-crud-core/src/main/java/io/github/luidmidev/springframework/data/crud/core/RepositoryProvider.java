package io.github.luidmidev.springframework.data.crud.core;

public interface RepositoryProvider<R> {
    R getRepository();
}
