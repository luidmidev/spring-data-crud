package io.github.luidmidev.springframework.data.crud.core.operations;


import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

/**
 * CRUD Service
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 */
@Validated
public interface WriteOperations<M, D, ID> {


    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'CREATE')")
    M create(@Valid @NotNull D dto);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'UPDATE')")
    M update(@NotNull ID id, @Valid @NotNull D dto);

    @PreAuthorize("@authorizationCrudManager.canAccess(this, 'DELETE')")
    void delete(ID id);

}
