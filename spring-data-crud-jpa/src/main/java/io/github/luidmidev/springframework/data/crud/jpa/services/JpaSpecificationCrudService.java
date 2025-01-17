package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.services.HooksCrudService;
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
        HooksCrudService<M, D, ID> {
}
