package io.github.luidmidev.springframework.data.crud.jpa;


import io.github.luidmidev.springframework.data.crud.core.StandardCrudService;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CRUD Service for JPA
 *
 * @param <E>  Entity
 * @param <D>  DTO
 * @param <ID> ID
 * @param <R>  Repositorys
 */
public interface JpaCrudService<E extends Persistable<ID>, D, ID, R extends JpaRepository<E, ID>> extends
        JpaReadService<E, ID, R>,
        JpaWriteService<E, D, ID, R>,
        StandardCrudService<E, D, ID, R> {
}
