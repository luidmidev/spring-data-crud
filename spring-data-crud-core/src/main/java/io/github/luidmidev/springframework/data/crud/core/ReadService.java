package io.github.luidmidev.springframework.data.crud.core;


import cz.jirutka.rsql.parser.ast.Node;
import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.hooks.ReadHooks;
import io.github.luidmidev.springframework.data.crud.core.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Validated
public non-sealed interface ReadService<E extends Persistable<ID>, ID> extends Crud {

    default ReadHooks<E, ID> getHooks() {
        return ReadHooks.getDefault();
    }

    default Page<E> page(String search, Pageable pageable, Node query) {
        Crud.preProccess(this, CrudOperation.PAGE);

        var normalized = StringUtils.normalize(search);
        var page = resolvePage(normalized, pageable, query);
        var hooks = getHooks();

        hooks.onPage(page);
        return page;
    }


    default E find(ID id) {
        Crud.preProccess(this, CrudOperation.FIND);

        var model = internalFind(id);
        var hooks = getHooks();

        hooks.onFind(model);
        return model;
    }

    default List<E> find(List<ID> ids) {
        Crud.preProccess(this, CrudOperation.FIND);

        var list = internalFind(ids);
        var hooks = getHooks();

        hooks.onFind(list, ids);
        return list;
    }

    default long count(String search, Node query) {
        Crud.preProccess(this, CrudOperation.COUNT);

        var count = resolveCount(search, query);
        var hooks = getHooks();

        hooks.onCount(count);
        return count;
    }

    default boolean exists(ID id) {
        Crud.preProccess(this, CrudOperation.EXISTS);

        var exists = internalExists(id);
        var hooks = getHooks();

        hooks.onExists(exists, id);
        return exists;
    }

    Page<E> internalPage(Pageable pageable);

    Page<E> internalSearch(String search, Pageable pageable);

    Page<E> internalSearch(String search, Pageable pageable, Node query);

    E internalFind(ID id) throws NotFoundEntityException;

    List<E> internalFind(List<ID> ids);

    long internalCount();

    long internalCount(String search);

    long internalCount(String search, Node query);

    boolean internalExists(ID id);

    private Page<E> resolvePage(String search, Pageable pageable, Node query) {
        if (query == null) {
            if (search == null || search.isBlank()) {
                return internalPage(pageable);
            }
            return internalSearch(search, pageable);
        } else {
            return internalSearch(search, pageable, query);
        }
    }

    private long resolveCount(String search, Node query) {
        if (query == null) {
            if (search == null || search.isBlank()) {
                return internalCount();
            }
            return internalCount(search);
        } else {
            return internalCount(search, query);
        }
    }
}

