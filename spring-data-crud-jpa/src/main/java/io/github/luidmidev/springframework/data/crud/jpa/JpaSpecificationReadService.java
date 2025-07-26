package io.github.luidmidev.springframework.data.crud.jpa;


import cz.jirutka.rsql.parser.ast.Node;
import io.github.luidmidev.omnisearch.core.OmniSearchBaseOptions;
import io.github.luidmidev.omnisearch.core.OmniSearchOptions;
import io.github.luidmidev.omnisearch.jpa.JpaOmniSearchPredicateBuilder;
import io.github.luidmidev.springframework.data.crud.core.CrudOperation;
import io.github.luidmidev.springframework.data.crud.core.providers.EntityClassProvider;
import io.github.luidmidev.springframework.data.crud.core.providers.RepositoryProvider;
import io.github.luidmidev.springframework.data.crud.core.exceptions.NotFoundEntityException;
import io.github.luidmidev.springframework.data.crud.core.ReadService;
import io.github.luidmidev.springframework.data.crud.jpa.providers.EntityManagerProvider;
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
 * @param <R>  Repository
 */
public interface JpaSpecificationReadService<E extends Persistable<ID>, ID, R extends JpaSpecificationExecutor<E>> extends
        ReadService<E, ID>,
        RepositoryProvider<R>,
        EntityManagerProvider,
        EntityClassProvider<E>,
        SpecificationCombiner<E> {

    @Override
    default Page<E> internalPage(Pageable pageable) {
        Specification<E> spec = (root, query, cb) -> null;
        return getRepository().findAll(combineSpecification(spec, CrudOperation.PAGE), pageable);
    }

    @Override
    default Page<E> internalSearch(String search, Pageable pageable) {
        return internalSearch(search, pageable, null);
    }

    @Override
    default Page<E> internalSearch(String search, Pageable pageable, Node query) {
        var options = toSearchOptions(search, pageable, query);
        Specification<E> spec = (root, q, cb) -> JpaOmniSearchPredicateBuilder.buildPredicate(
                getEntityManager(),
                cb,
                root,
                options
        );
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


    default long internalCount(String search) {
        return internalCount(search, null);
    }

    @Override
    default long internalCount(String search, Node query) {
        var options = toBaseSearchOptions(search, query);
        Specification<E> spec = (root, q, cb) -> JpaOmniSearchPredicateBuilder.buildPredicate(
                getEntityManager(),
                cb,
                root,
                options
        );
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

    default OmniSearchOptions toSearchOptions(String search, Pageable pageable, Node query) {
        return OmniSearchOptionsFactory.create(search, pageable, query);
    }

    default OmniSearchBaseOptions toBaseSearchOptions(String search, Node query) {
        return OmniSearchOptionsFactory.create(search, query);
    }
}
