package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import io.github.luidmidev.springframework.data.crud.core.services.hooks.ReadServiceHooks;
import io.github.luidmidev.springframework.data.crud.core.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Read Service
 *
 * @param <M>  Model
 * @param <ID> ID
 */
@Validated
public abstract class BaseReadService<M extends Persistable<ID>, ID> implements ReadOperations<M, ID> {

    private final ReadServiceHooks<M, ID> hooks;

    protected BaseReadService() {
        this.hooks = initializeHooks();
    }

    @Override
    public final Page<M> page(String search, Pageable pageable, MultiValueMap<String, String> params) {
        return doPage(search, pageable, params);
    }

    @Override
    public final M find(ID id) {
        return doFind(id);
    }

    @Override
    public final List<M> find(List<ID> ids) {
        return doFind(ids);
    }

    @Override
    public final long count() {
        return doCount();
    }

    @Override
    public final boolean exists(ID id) {
        return doExists(id);
    }

    protected final Page<M> doPage(String search, Pageable pageable, MultiValueMap<String, String> filters) {
        var normalized = StringUtils.normalize(search);
        var page = resolvePage(normalized, pageable, filters);
        hooks.onPage(page);
        return page;
    }

    protected final M doFind(ID id) {
        var model = internalFind(id);
        hooks.onFind(model);
        return model;
    }

    protected final List<M> doFind(List<ID> ids) {
        var list = internalFind(ids);
        hooks.onFind(list, ids);
        return list;
    }

    protected final long doCount() {
        var count = internalCount();
        hooks.onCount(count);
        return count;
    }

    protected final boolean doExists(ID id) {
        var exists = internalExists(id);
        hooks.onExists(exists, id);
        return exists;
    }

    protected abstract Page<M> internalPage(Pageable pageable);

    protected abstract Page<M> internalSearch(String search, Pageable pageable);

    protected abstract Page<M> internalSearch(String search, Pageable pageable, MultiValueMap<String, String> filters);

    protected abstract M internalFind(ID id);

    protected abstract List<M> internalFind(List<ID> ids);

    protected abstract long internalCount();

    protected abstract boolean internalExists(ID id);

    protected ReadServiceHooks<M, ID> initializeHooks() {
        return ReadServiceHooks.getDefault();
    }

    private Page<M> resolvePage(String search, Pageable pageable, MultiValueMap<String, String> filters) {
        if (filters == null || filters.isEmpty()) {
            if (search == null) {
                return internalPage(pageable);
            }
            return internalSearch(search, pageable);
        } else {
            return internalSearch(search, pageable, filters);
        }
    }
}
