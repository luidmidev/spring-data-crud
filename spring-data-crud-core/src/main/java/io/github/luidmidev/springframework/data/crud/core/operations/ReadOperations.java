package io.github.luidmidev.springframework.data.crud.core.operations;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    List<M> list(String search);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'PAGE')")
    Page<M> page(String search, Pageable pageable);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'FIND')")
    M find(ID id);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'COUNT')")
    long count();

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'EXISTS')")
    boolean exists(ID id);


}
