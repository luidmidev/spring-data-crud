package io.github.luidmidev.springframework.data.crud.jpa;


import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.CrudService;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * CRUD Service for JPA
 *
 * @param <E>  Entity
 * @param <D>  DTO
 * @param <ID> ID
 * @param <R>  Repositorys
 */
public interface JpaSpecificationCrudService<E extends Persistable<ID>, D, ID, R extends JpaRepository<E, ID> & JpaSpecificationExecutor<E>> extends
        JpaWriteService<E, D, ID, R>,
        JpaSpecificationReadService<E, ID, R>,
        CrudService<E, D, ID> {

    @Override
    default E internalFind(ID id) throws NotFoundEntityException {
        return JpaSpecificationReadService.super.internalFind(id);
    }
}
