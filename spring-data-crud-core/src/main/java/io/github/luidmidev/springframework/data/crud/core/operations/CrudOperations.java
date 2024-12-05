package io.github.luidmidev.springframework.data.crud.core.operations;


import org.springframework.data.domain.Persistable;

/**
 * Crud Operations
 *
 * @param <M>  Model
 * @param <D> DTO
 * @param <ID> ID
 */
public non-sealed interface CrudOperations<M extends Persistable<ID>, D, ID> extends ReadOperations<M, ID>, WriteOperations<M, D, ID>, Crud {

}
