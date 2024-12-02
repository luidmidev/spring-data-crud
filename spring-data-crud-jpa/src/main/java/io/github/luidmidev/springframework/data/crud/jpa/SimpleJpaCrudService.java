package io.github.luidmidev.springframework.data.crud.jpa;


import io.github.luidmidev.springframework.data.crud.jpa.services.JpaCrudService;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
@Transactional
@Validated
public abstract class SimpleJpaCrudService<M extends Persistable<ID>, D, ID> extends JpaCrudService<M, D, ID, SimpleJpaRepository<M, ID>> {

    protected SimpleJpaCrudService(Class<M> domainClass, EntityManager entityManager) {
        super(new SimpleJpaRepository<>(domainClass, entityManager), domainClass, entityManager);
    }
}
