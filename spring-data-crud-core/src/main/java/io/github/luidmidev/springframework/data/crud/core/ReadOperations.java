package io.github.luidmidev.springframework.data.crud.core;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Read Operations
 *
 * @param <M>  Model
 * @param <ID> ID
 */
public interface ReadOperations<M, ID> {

    List<M> list();

    Page<M> page(Pageable pageable);

    M find(ID id);

    Page<M> search(String search, Pageable pageable);

    List<M> search(String search);

}
