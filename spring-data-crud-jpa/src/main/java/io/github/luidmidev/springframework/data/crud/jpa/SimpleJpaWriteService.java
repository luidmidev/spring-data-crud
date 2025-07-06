package io.github.luidmidev.springframework.data.crud.jpa;


import jakarta.persistence.EntityManager;
import lombok.Getter;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

/**
 * CRUD Service for JPA without JpaRepository
 *
 * @param <E>  Entity
 * @param <D>  DTO
 * @param <ID> ID
 */
@Getter
public abstract class SimpleJpaWriteService<E extends Persistable<ID>, D, ID> implements
        JpaWriteService<E, D, ID, SimpleJpaRepository<E, ID>> {

    protected final Class<E> entityClass;
    protected final EntityManager entityManager;
    protected final SimpleJpaRepository<E, ID> repository;

    protected SimpleJpaWriteService(Class<E> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.repository = new SimpleJpaRepository<>(entityClass, entityManager);
    }
}
