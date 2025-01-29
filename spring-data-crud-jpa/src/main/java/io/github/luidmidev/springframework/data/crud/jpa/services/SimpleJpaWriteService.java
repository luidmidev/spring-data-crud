package io.github.luidmidev.springframework.data.crud.jpa.services;


import jakarta.persistence.EntityManager;
import lombok.Getter;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * CRUD Service for JPA without JpaRepository
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 */
@Transactional
@Validated
@Getter
public abstract class SimpleJpaWriteService<M extends Persistable<ID>, D, ID> implements
        JpaWriteService<M, D, ID, SimpleJpaRepository<M, ID>> {

    protected final Class<M> entityClass;
    protected final EntityManager entityManager;
    protected final SimpleJpaRepository<M, ID> repository;

    protected SimpleJpaWriteService(Class<M> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.repository = new SimpleJpaRepository<>(entityClass, entityManager);
    }
}
