package io.github.luidmidev.springframework.data.crud.jpa;


import io.github.luidmidev.springframework.data.crud.core.CRUDModel;
import io.github.luidmidev.springframework.data.crud.core.CRUDService;
import io.github.luidmidev.springframework.data.crud.jpa.utils.AdvanceSearch;
import io.github.luidmidev.springframework.data.crud.jpa.utils.AdditionsSearch;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

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
public abstract class JpaCRUDService<M extends CRUDModel<ID>, D, ID, R extends JpaRepository<M, ID>> extends CRUDService<M, D, ID, R> {


    protected final EntityManager entityManager;

    protected JpaCRUDService(R repository, Class<M> domainClass, EntityManager entityManager) {
        super(repository, domainClass);
        this.entityManager = entityManager;
    }

    @Override
    public Page<M> search(String search, Pageable pageable) {
        return search(search, pageable, null);
    }

    @Override
    public List<M> search(String search) {
        return search(search, (AdditionsSearch<M>) null);
    }

    protected Page<M> search(String search, Pageable pageable, AdditionsSearch<M> additions) {
        return AdvanceSearch.search(entityManager, search, pageable, additions, domainClass);
    }

    protected List<M> search(String search, AdditionsSearch<M> additions) {
        return AdvanceSearch.search(entityManager, search, additions, domainClass);
    }
}
