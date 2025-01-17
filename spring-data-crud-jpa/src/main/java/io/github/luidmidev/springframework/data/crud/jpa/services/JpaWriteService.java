package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.services.WriteService;
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
public interface JpaWriteService<M extends Persistable<ID>, D, ID, R extends JpaRepository<M, ID>> extends
        WriteService<M, D, ID, R> {
}
