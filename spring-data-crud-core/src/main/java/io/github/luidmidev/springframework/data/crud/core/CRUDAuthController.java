package io.github.luidmidev.springframework.data.crud.core;


import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD Controller
 *
 * @param <M>  Model
 * @param <D>  DTO
 * @param <ID> ID
 * @param <S>  Service
 */
@Log4j2
public abstract class CRUDAuthController<M extends CRUDModel<ID>, D, ID, S extends CRUDService<M, D, ID, ?>> extends CRUDController<M, D, ID, S> {

    protected CRUDAuthController(S service) {
        this(service, new CRUDMessagesResolver<>());
    }

    protected CRUDAuthController(S service, @NotNull CRUDMessagesResolver<ID> messagesResolver) {
        super(service, messagesResolver);
    }

    @Override
    @PostMapping
    @PreAuthorize("@authorityManager.isPermitAllCreate() ? permitAll() : hasAnyAuthority(@authorityManager.getCreate())")
    public ResponseEntity<M> create(@RequestBody D dto) {
        return super.create(dto);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("@authorityManager.isPermitAllUpdate() ? permitAll() : hasAnyAuthority(@authorityManager.getUpdate())")
    public ResponseEntity<M> update(@PathVariable("id") ID id, @RequestBody D dto) {
        return super.update(id, dto);
    }


    @Override
    @GetMapping
    @PreAuthorize("@authorityManager.isPermitAllRead() ? permitAll() : hasAnyAuthority(@authorityManager.getRead())")
    public ResponseEntity<Page<M>> page(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "properties", required = false) List<String> properties,
            @RequestParam(name = "direction", required = false) Sort.Direction direction
    ) {
        return super.page(search, size, page, properties, direction);
    }


    @Override
    @GetMapping("/all")
    @PreAuthorize("@authorityManager.isPermitAllRead() ? permitAll() : hasAnyAuthority(@authorityManager.getRead())")
    public ResponseEntity<List<M>> list(
            @RequestParam(name = "search", required = false) String search
    ) {
        return super.list(search);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("@authorityManager.isPermitAllRead() ? permitAll() : hasAnyAuthority(@authorityManager.getRead())")
    public ResponseEntity<M> find(@PathVariable("id") ID id) {
        return super.find(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("@authorityManager.isPermitAllDelete() ? permitAll() : hasRole(@authorityManager.getDelete())")
    public ResponseEntity<String> delete(@PathVariable("id") ID id) {
        return super.delete(id);
    }
}
