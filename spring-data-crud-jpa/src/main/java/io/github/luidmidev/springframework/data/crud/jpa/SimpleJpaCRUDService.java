package io.github.luidmidev.springframework.data.crud.jpa;


import io.github.luidmidev.springframework.data.crud.core.CRUDModel;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
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
public abstract class SimpleJpaCRUDService<M extends CRUDModel<ID>, D, ID> extends JpaCRUDService<M, D, ID, SimpleJpaRepository<M, ID>> {

    protected SimpleJpaCRUDService(Class<M> domainClass, EntityManager entityManager) {
        super(new SimpleJpaRepository<>(domainClass, entityManager), domainClass, entityManager);
    }
}
