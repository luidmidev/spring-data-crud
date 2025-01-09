package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.services.ReadService;
import io.github.luidmidev.springframework.data.crud.jpa.utils.AdditionsSearch;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSmartSearch;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CRUD Service for JPA
 *
 * @param <M>  Model
 * @param <ID> ID
 * @param <R>  Repositorys
 */
public abstract class JpaReadService<M extends Persistable<ID>, ID, R extends JpaRepository<M, ID>> extends ReadService<M, ID, R> {

    protected final EntityManager entityManager;

    protected JpaReadService(R repository, Class<M> domainClass, EntityManager entityManager) {
        super(repository, domainClass);
        this.entityManager = entityManager;
    }

    @Override
    protected Page<M> search(String search, Pageable pageable) {
        return JpaSmartSearch.search(entityManager, search, pageable, domainClass);
    }

    protected Page<M> search(String search, Pageable pageable, AdditionsSearch<M> additionsSearch) {
        return JpaSmartSearch.search(entityManager, search, pageable, additionsSearch, domainClass);
    }
}
