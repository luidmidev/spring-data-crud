package io.github.luidmidev.springframework.data.crud.core.operations;

public sealed interface Crud permits WriteOperations, ReadOperations, CrudOperations {
}
