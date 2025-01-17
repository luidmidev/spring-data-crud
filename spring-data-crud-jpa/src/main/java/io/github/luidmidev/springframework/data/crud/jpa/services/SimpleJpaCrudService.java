package io.github.luidmidev.springframework.data.crud.jpa.services;


import jakarta.persistence.EntityManager;
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
public abstract class SimpleJpaCrudService<M extends Persistable<ID>, D, ID> extends JpaCrudService<M, D, ID, SimpleJpaRepository<M, ID>> {


    protected SimpleJpaCrudService(EntityManager entityManager, Class<M> entityClass) {
        super(new SimpleJpaRepository<>(entityClass, entityManager), entityManager, entityClass);
    }
}
