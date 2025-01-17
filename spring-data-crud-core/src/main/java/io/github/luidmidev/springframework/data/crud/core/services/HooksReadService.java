package io.github.luidmidev.springframework.data.crud.core.services;


import io.github.luidmidev.springframework.data.crud.core.operations.ReadOperations;
import io.github.luidmidev.springframework.data.crud.core.services.hooks.ReadHooks;
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
public interface HooksReadService<M extends Persistable<ID>, ID> extends ReadOperations<M, ID> {


    default ReadHooks<M, ID> getHooks() {
        return ReadHooks.getDefault();
    }

    @Override
    default Page<M> page(String search, Pageable pageable, MultiValueMap<String, String> params) {
        return doPage(search, pageable, params);
    }

    @Override
    default M find(ID id) {
        return doFind(id);
    }

    @Override
    default List<M> find(List<ID> ids) {
        return doFind(ids);
    }

    @Override
    default long count() {
        return doCount();
    }

    @Override
    default boolean exists(ID id) {
        return doExists(id);
    }

    default Page<M> doPage(String search, Pageable pageable, MultiValueMap<String, String> filters) {
        var normalized = StringUtils.normalize(search);
        var page = resolvePage(normalized, pageable, filters);
        getHooks().onPage(page);
        return page;
    }

    default M doFind(ID id) {
        var model = internalFind(id);
        getHooks().onFind(model);
        return model;
    }

    default List<M> doFind(List<ID> ids) {
        var list = internalFind(ids);
        getHooks().onFind(list, ids);
        return list;
    }

    default long doCount() {
        var count = internalCount();
        getHooks().onCount(count);
        return count;
    }

    default boolean doExists(ID id) {
        var exists = internalExists(id);
        getHooks().onExists(exists, id);
        return exists;
    }

    Page<M> internalPage(Pageable pageable);

    Page<M> internalSearch(String search, Pageable pageable);

    Page<M> internalSearch(String search, Pageable pageable, MultiValueMap<String, String> filters);

    M internalFind(ID id);

    List<M> internalFind(List<ID> ids);

    long internalCount();

    boolean internalExists(ID id);

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
