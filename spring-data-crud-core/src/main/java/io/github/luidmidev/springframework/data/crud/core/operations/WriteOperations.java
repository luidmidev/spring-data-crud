package io.github.luidmidev.springframework.data.crud.core.operations;


import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Persistable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Crud Operations
 *
 * @param <M>  Model
 * @param <D> DTO
 * @param <ID> ID
 */
public non-sealed interface WriteOperations<M extends Persistable<ID>, D, ID> extends Crud {
    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'CREATE')")
    M create(@Valid @NotNull D dto);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'UPDATE')")
    M update(@NotNull ID id, @Valid @NotNull D dto);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'DELETE')")
    void delete(ID id);
}
