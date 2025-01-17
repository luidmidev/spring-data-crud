package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.EntityClassProvider;
import io.github.luidmidev.springframework.data.crud.core.services.ReadService;
import io.github.luidmidev.springframework.data.crud.jpa.EntityManagerProvider;
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
public interface JpaReadService<M extends Persistable<ID>, ID, R extends JpaRepository<M, ID>> extends
        ReadService<M, ID, R>,
        EntityClassProvider<M>,
        EntityManagerProvider {

    @Override
    default Page<M> internalSearch(String search, Pageable pageable) {
        return JpaSmartSearch.search(getEntityManager(), search, pageable, getEntityClass());
    }

    default Page<M> internalSearch(String search, Pageable pageable, AdditionsSearch<M> additionsSearch) {
        return JpaSmartSearch.search(getEntityManager(), search, pageable, additionsSearch, getEntityClass());
    }
}
