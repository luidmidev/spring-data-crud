package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.services.CrudService;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * CRUD Service for JPA
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 * @param <R>  Repositorys
 */
public interface JpaSpecificationCrudService<M extends Persistable<ID>, D, ID, R extends JpaRepository<M, ID> & JpaSpecificationExecutor<M>> extends
        JpaWriteService<M, D, ID, R>,
        JpaSpecificationReadService<M, ID, R>,
        CrudService<M, D, ID> {

    @Override
    default M internalFind(ID id) throws NotFoundEntityException {
        return JpaSpecificationReadService.super.internalFind(id);
    }
}
