package io.github.luidmidev.springframework.data.crud.core;


import io.github.luidmidev.springframework.data.crud.core.utils.PageableUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
public abstract class CRUDController<M extends CRUDModel<ID>, D, ID, S extends CRUDService<M, D, ID, ?>> {

    @Getter
    protected final S service;
    protected final CRUDMessagesResolver<ID> messagesResolver;
    protected final String simpleName = getClass().getSimpleName();

    protected CRUDController(S service) {
        this(service, new CRUDMessagesResolver<>());
    }

    protected CRUDController(S service, @NotNull CRUDMessagesResolver<ID> messagesResolver) {
        this.service = service;
        this.messagesResolver = messagesResolver;
        this.service.setNotFoundMessageResolver(messagesResolver.getNotFound());
    }

    @PostMapping
    public ResponseEntity<M> create(@RequestBody D dto) {
        log.debug("Creating via {}: {}", simpleName, dto);
        M model = service.create(dto);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<M> update(@PathVariable("id") ID id, @RequestBody D dto) {
        log.debug("Updating via {}: {}", simpleName, dto);
        var model = service.update(id, dto);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<Page<M>> page(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "properties", required = false) List<String> properties,
            @RequestParam(name = "direction", required = false) Sort.Direction direction
    ) {
        log.debug("Paging via {}: search={}, size={}, page={}, sort={}, order={}", simpleName, search, size, page, properties, direction);
        var pageable = PageableUtils.resolvePage(size, page, direction, properties);
        return ResponseEntity.ok(search == null
                ? service.page(pageable)
                : service.search(search, pageable)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<M>> list(
            @RequestParam(name = "search", required = false) String search
    ) {
        log.debug("Listing all via {}: search={}", simpleName, search);
        return ResponseEntity.ok(search == null
                ? service.list()
                : service.search(search)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<M> find(@PathVariable("id") ID id) {
        log.debug("Finding via {}: {}", simpleName, id);
        M model = service.find(id);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") ID id) {
        log.debug("Deleting via {}: {}", simpleName, id);
        service.delete(id);
        return ResponseEntity.ok(messagesResolver.getDeleted().get());
    }

    @PostMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestBody ID id) {
        log.debug("Checking existence via {}: {}", simpleName, id);
        return ResponseEntity.ok(service.exists(id));
    }

    @PostMapping("/exists-all")
    public ResponseEntity<Boolean> existsAll(@RequestBody List<ID> ids) {
        log.debug("Checking existence of all via {}: {}", simpleName, ids);
        return ResponseEntity.ok(service.existsAll(ids));
    }
}
