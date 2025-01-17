package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.services.BaseReadService;
import io.github.luidmidev.springframework.data.crud.jpa.utils.AdditionsSearch;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSmartSearch;
import io.github.luidmidev.springframework.web.problemdetails.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * CRUD Service for JPA
 *
 * @param <M>  Model
 * @param <ID> ID
 * @param <R>  Repositorys
 */
@RequiredArgsConstructor
public abstract class JpaSpecificationReadService<M extends Persistable<ID>, ID, R extends JpaSpecificationExecutor<M>> extends BaseReadService<M, ID> {

    protected final R repository;
    protected final Class<M> entityClass;

    @Override
    protected Page<M> internalPage(Pageable pageable) {
        var spec = Specification.<M>where(null);
        combineSpecification(spec);
        return repository.findAll(spec, pageable);
    }

    @Override
    protected Page<M> internalSearch(String search, Pageable pageable) {
        return internalSearch(search, pageable, (AdditionsSearch<M>) null);
    }

    protected Page<M> internalSearch(String search, Pageable pageable, AdditionsSearch<M> additionsSearch) {
        var spec = Specification.<M>where((root, query, cb) -> JpaSmartSearch.getPredicate(search, additionsSearch, cb, query, root, entityClass));
        combineSpecification(spec);
        return repository.findAll(spec, pageable);
    }

    @Override
    protected M internalFind(ID id) {
        var spec = Specification.<M>where((root, query, cb) -> cb.equal(root.get("id"), id));
        combineSpecification(spec);
        return repository.findOne(spec).orElseThrow(ApiError::notFound);
    }

    @Override
    protected List<M> internalFind(List<ID> ids) {
        var spec = Specification.<M>where((root, query, cb) -> root.get("id").in(ids));
        combineSpecification(spec);
        return repository.findAll(spec);
    }

    @Override
    protected long internalCount() {
        var spec = Specification.<M>where(null);
        combineSpecification(spec);
        return repository.count(spec);
    }

    @Override
    protected boolean internalExists(ID id) {
        var spec = Specification.<M>where((root, query, cb) -> cb.equal(root.get("id"), id));
        combineSpecification(spec);
        return repository.exists(spec);
    }


    protected void combineSpecification(Specification<M> spec) {
    }
}
