package io.github.luidmidev.springframework.data.crud.core.services;


import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.validation.annotation.Validated;

/**
 * Read Service
 *
 * @param <M>  Model
 * @param <ID> ID
 * @param <R>  Repositorys
 */
@Validated
public interface CrudService<M extends Persistable<ID>, D, ID, R extends ListCrudRepository<M, ID> & PagingAndSortingRepository<M, ID>> extends
        ReadService<M, ID, R>,
        WriteService<M, D, ID, R>,
        HooksCrudService<M, D, ID> {

}
