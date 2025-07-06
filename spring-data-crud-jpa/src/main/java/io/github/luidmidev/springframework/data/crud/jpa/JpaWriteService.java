package io.github.luidmidev.springframework.data.crud.jpa;


import io.github.luidmidev.springframework.data.crud.core.StandardWriteService;
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
public interface JpaWriteService<E extends Persistable<ID>, D, ID, R extends JpaRepository<E, ID>> extends
        StandardWriteService<E, D, ID, R> {
}
