package io.github.luidmidev.springframework.data.crud.core.operations;


import io.github.luidmidev.springframework.data.crud.core.filters.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Read Operations
 *
 * @param <M>  Model
 * @param <ID> ID
 */
public non-sealed interface ReadOperations<M, ID> extends Crud {


    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'LIST')")
    Iterable<M> all(String search, Sort sort, Filter filter);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'PAGE')")
    Page<M> page(String search, Pageable pageable, Filter filter);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'FIND')")
    M find(ID id);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'FIND')")
    List<M> find(List<ID> ids);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'COUNT')")
    long count();

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'EXISTS')")
    boolean exists(ID id);
}
