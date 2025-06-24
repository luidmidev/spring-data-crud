package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.services.StandardReadService;
import io.github.luidmidev.springframework.data.crud.jpa.providers.EntityManagerProvider;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSearchExtension;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSearch;
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
public interface JpaReadService<M extends Persistable<ID>, ID, R extends JpaRepository<M, ID>> extends
        StandardReadService<M, ID, R>,
        EntityManagerProvider {

    @Override
    default Page<M> internalSearch(String search, Pageable pageable) {
        return JpaSearch.search(getEntityManager(), search, pageable, getEntityClass());
    }

    default Page<M> internalSearch(String search, Pageable pageable, JpaSearchExtension<M> jpaSearchExtension) {
        return JpaSearch.search(getEntityManager(), search, pageable, jpaSearchExtension, getEntityClass());
    }
}
