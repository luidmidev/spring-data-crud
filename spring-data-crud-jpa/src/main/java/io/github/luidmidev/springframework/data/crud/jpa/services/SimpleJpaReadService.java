package io.github.luidmidev.springframework.data.crud.jpa.services;


import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Read Service for JPA without JpaRepository
 *
 * @param <M>  Model
 * @param <ID> ID
 */
@Transactional
@Validated
public abstract class SimpleJpaReadService<M extends Persistable<ID>, ID> extends JpaReadService<M, ID, SimpleJpaRepository<M, ID>> {

    protected SimpleJpaReadService(Class<M> domainClass, EntityManager entityManager) {
        super(new SimpleJpaRepository<>(domainClass, entityManager), domainClass, entityManager);
    }
}
