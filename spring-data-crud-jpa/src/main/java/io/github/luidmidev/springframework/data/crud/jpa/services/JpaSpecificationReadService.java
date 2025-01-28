package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.ModelClassProvider;
import io.github.luidmidev.springframework.data.crud.core.RepositoryProvider;
import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundModelException;
import io.github.luidmidev.springframework.data.crud.core.services.HooksReadService;
import io.github.luidmidev.springframework.data.crud.jpa.SpecificationCombiner;
import io.github.luidmidev.springframework.data.crud.jpa.utils.AdditionsSearch;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSmartSearch;
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
public interface JpaSpecificationReadService<M extends Persistable<ID>, ID, R extends JpaSpecificationExecutor<M>> extends
        HooksReadService<M, ID>,
        RepositoryProvider<R>,
        ModelClassProvider<M>,
        SpecificationCombiner<M> {

    @Override
    default Page<M> internalPage(Pageable pageable) {
        var spec = Specification.<M>where(null);
        return getRepository().findAll(processSpecification(spec), pageable);
    }

    @Override
    default Page<M> internalSearch(String search, Pageable pageable) {
        return internalSearch(search, pageable, (AdditionsSearch<M>) null);
    }

    default Page<M> internalSearch(String search, Pageable pageable, AdditionsSearch<M> additionsSearch) {
        var spec = Specification.<M>where((root, query, cb) -> JpaSmartSearch.getPredicate(search, additionsSearch, cb, query, root, getEntityClass()));
        return getRepository().findAll(processSpecification(spec), pageable);
    }

    @Override
    default M internalFind(ID id) {
        var spec = Specification.<M>where((root, query, cb) -> cb.equal(root.get("id"), id));
        return getRepository().findOne(processSpecification(spec)).orElseThrow(() -> new NotFoundModelException(getEntityClass(), id));
    }

    @Override
    default List<M> internalFind(List<ID> ids) {
        var spec = Specification.<M>where((root, query, cb) -> root.get("id").in(ids));
        return getRepository().findAll(processSpecification(spec));
    }

    @Override
    default long internalCount() {
        var spec = Specification.<M>where(null);
        return getRepository().count(processSpecification(spec));
    }

    @Override
    default boolean internalExists(ID id) {
        var spec = Specification.<M>where((root, query, cb) -> cb.equal(root.get("id"), id));
        return getRepository().exists(processSpecification(spec));
    }

}
