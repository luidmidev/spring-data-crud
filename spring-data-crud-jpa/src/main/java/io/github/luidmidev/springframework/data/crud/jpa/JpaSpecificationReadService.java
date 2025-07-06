package io.github.luidmidev.springframework.data.crud.jpa;


import io.github.luidmidev.springframework.data.crud.core.CrudOperation;
import io.github.luidmidev.springframework.data.crud.core.providers.EntityClassProvider;
import io.github.luidmidev.springframework.data.crud.core.providers.RepositoryProvider;
import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.ReadService;
import io.github.luidmidev.springframework.data.crud.jpa.utils.JpaSearchOptions;
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
 * @param <E>  Entity
 * @param <ID> ID
 * @param <R>  Repositorys
 */
public interface JpaSpecificationReadService<E extends Persistable<ID>, ID, R extends JpaSpecificationExecutor<E>> extends
        ReadService<E, ID>,
        RepositoryProvider<R>,
        EntityClassProvider<E>,
        SpecificationCombiner<E> {

    @Override
    default Page<E> internalPage(Pageable pageable) {
        Specification<E> spec = (root, query, cb) -> null;
        return getRepository().findAll(combineSpecification(spec, CrudOperation.PAGE), pageable);
    }

    @Override
    default Page<E> internalSearch(String search, Pageable pageable) {
        return internalSearch(search, pageable, (JpaSearchOptions<E>) null);
    }

    default Page<E> internalSearch(String search, Pageable pageable, JpaSearchOptions<E> jpaSearchOptions) {
        Specification<E> spec = (root, query, cb) -> JpaSearch.getPredicate(search, jpaSearchOptions, cb, query, root, getEntityClass());
        return getRepository().findAll(combineSpecification(spec, CrudOperation.PAGE), pageable);
    }

    @Override
    default E internalFind(ID id) {
        Specification<E> spec = (root, query, cb) -> cb.equal(root.get(getIdFieldName()), id);
        return getRepository().findOne(combineSpecification(spec, CrudOperation.FIND)).orElseThrow(() -> new NotFoundEntityException(getEntityClass(), id));
    }

    @Override
    default List<E> internalFind(List<ID> ids) {
        Specification<E> spec = (root, query, cb) -> root.get(getIdFieldName()).in(ids);
        return getRepository().findAll(combineSpecification(spec, CrudOperation.FIND));
    }

    @Override
    default long internalCount() {
        Specification<E> spec = (root, query, cb) -> null;
        return getRepository().count(combineSpecification(spec, CrudOperation.COUNT));
    }

    @Override
    default boolean internalExists(ID id) {
        Specification<E> spec = (root, query, cb) -> cb.equal(root.get(getIdFieldName()), id);
        return getRepository().exists(combineSpecification(spec, CrudOperation.EXISTS));
    }

    default String getIdFieldName() {
        return "id";
    }
}
