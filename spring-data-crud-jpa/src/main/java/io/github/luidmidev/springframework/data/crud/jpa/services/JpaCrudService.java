package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.services.CrudService;
import io.github.luidmidev.springframework.data.crud.jpa.utils.AdditionsSearch;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSmartSearch;
import jakarta.persistence.EntityManager;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CRUD Service for JPA
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 * @param <R>  Repositorys
 */
public abstract class JpaCrudService<M extends Persistable<ID>, D, ID, R extends JpaRepository<M, ID>> extends CrudService<M, D, ID, R> {

    protected final EntityManager entityManager;
    protected final Class<M> entityClass;

    protected JpaCrudService(R repository, EntityManager entityManager, Class<M> entityClass) {
        super(repository);
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    @Override
    @SneakyThrows
    protected M newEntity() {
        return entityClass.getConstructor().newInstance();
    }


    @Override
    protected Page<M> internalSearch(String search, Pageable pageable) {
        return JpaSmartSearch.search(entityManager, search, pageable, entityClass);
    }

    protected Page<M> internalSearch(String search, Pageable pageable, AdditionsSearch<M> additionsSearch) {
        return JpaSmartSearch.search(entityManager, search, pageable, additionsSearch, entityClass);
    }
}
