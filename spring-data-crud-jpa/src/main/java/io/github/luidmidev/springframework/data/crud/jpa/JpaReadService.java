package io.github.luidmidev.springframework.data.crud.jpa;


import io.github.luidmidev.springframework.data.crud.core.StandardReadService;
import io.github.luidmidev.springframework.data.crud.jpa.providers.EntityManagerProvider;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSearchOptions;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CRUD Service for JPA
 *
 * @param <E>  Entity
 * @param <ID> ID
 * @param <R>  Repositorys
 */
public interface JpaReadService<E extends Persistable<ID>, ID, R extends JpaRepository<E, ID>> extends
        StandardReadService<E, ID, R>,
        EntityManagerProvider {

    @Override
    default Page<E> internalSearch(String search, Pageable pageable) {
        return JpaSearch.search(getEntityManager(), search, pageable, getEntityClass());
    }

    default Page<E> internalSearch(String search, Pageable pageable, JpaSearchOptions<E> jpaSearchOptions) {
        return JpaSearch.search(getEntityManager(), search, pageable, jpaSearchOptions, getEntityClass());
    }
}
