package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.services.ReadService;
import io.github.luidmidev.springframework.data.crud.jpa.utils.AdvanceSearch;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD Service for JPA
 *
 * @param <M>  Model
 * @param <ID> ID
 * @param <R>  Repositorys
 */
@Transactional
public abstract class JpaReadService<M extends Persistable<ID>, ID, R extends JpaRepository<M, ID>> extends ReadService<M, ID, R> {

    protected final EntityManager entityManager;

    protected JpaReadService(R repository, Class<M> domainClass, EntityManager entityManager) {
        super(repository, domainClass);
        this.entityManager = entityManager;
    }

    @Override
    protected Page<M> search(String search, Pageable pageable) {
        return AdvanceSearch.search(entityManager, search, pageable, domainClass);
    }

}
