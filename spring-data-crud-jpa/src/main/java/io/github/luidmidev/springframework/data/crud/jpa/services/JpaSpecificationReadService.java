package io.github.luidmidev.springframework.data.crud.jpa.services;


import io.github.luidmidev.springframework.data.crud.core.providers.EntityClassProvider;
import io.github.luidmidev.springframework.data.crud.core.providers.RepositoryProvider;
import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.services.ReadService;
import io.github.luidmidev.springframework.data.crud.jpa.SpecificationCombiner;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSearchExtension;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSearch;
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
        ReadService<M, ID>,
        RepositoryProvider<R>,
        EntityClassProvider<M>,
        SpecificationCombiner<M> {

    @Override
    default Page<M> internalPage(Pageable pageable) {
        Specification<M> spec = (root, query, cb) -> null;
        return getRepository().findAll(combineSpecification(spec), pageable);
    }

    @Override
    default Page<M> internalSearch(String search, Pageable pageable) {
        return internalSearch(search, pageable, (JpaSearchExtension<M>) null);
    }

    default Page<M> internalSearch(String search, Pageable pageable, JpaSearchExtension<M> jpaSearchExtension) {
        Specification<M> spec = (root, query, cb) -> JpaSearch.getPredicate(search, jpaSearchExtension, cb, query, root, getEntityClass());
        return getRepository().findAll(combineSpecification(spec), pageable);
    }

    @Override
    default M internalFind(ID id) {
        Specification<M> spec = (root, query, cb) -> cb.equal(root.get(getIdFieldName()), id);
        return getRepository().findOne(combineSpecification(spec)).orElseThrow(() -> new NotFoundEntityException(getEntityClass(), id));
    }

    @Override
    default List<M> internalFind(List<ID> ids) {
        Specification<M> spec = (root, query, cb) -> root.get(getIdFieldName()).in(ids);
        return getRepository().findAll(combineSpecification(spec));
    }

    @Override
    default long internalCount() {
        Specification<M> spec = (root, query, cb) -> null;
        return getRepository().count(combineSpecification(spec));
    }

    @Override
    default boolean internalExists(ID id) {
        Specification<M> spec = (root, query, cb) -> cb.equal(root.get(getIdFieldName()), id);
        return getRepository().exists(combineSpecification(spec));
    }

    default String getIdFieldName() {
        return "id";
    }
}
