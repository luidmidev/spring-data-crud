package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.services.WriteService;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * CRUD Service for JPA
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 * @param <R>  Repositorys
 */
@Log4j2
@Transactional
@Validated
public abstract class JpaWriteService<M extends Persistable<ID>, D, ID, R extends JpaRepository<M, ID>> extends WriteService<M, D, ID, R> {


    protected final EntityManager entityManager;

    protected JpaWriteService(R repository, Class<M> domainClass, EntityManager entityManager) {
        super(repository, domainClass);
        this.entityManager = entityManager;
    }

}
