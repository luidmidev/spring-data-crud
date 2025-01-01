package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.services.CrudService;
import io.github.luidmidev.springframework.data.crud.jpa.utils.AdvanceSearch;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

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

    protected JpaCrudService(R repository, Class<M> domainClass, EntityManager entityManager) {
        super(repository, domainClass);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public M create(@NotNull D dto) {
        return super.create(dto);
    }

    @Override
    @Transactional
    public M update(@NotNull ID id, @NotNull D dto) {
        return super.update(id, dto);
    }

    @Override
    protected Page<M> search(String search, Pageable pageable) {
        return AdvanceSearch.search(entityManager, search, pageable, domainClass);
    }

}
