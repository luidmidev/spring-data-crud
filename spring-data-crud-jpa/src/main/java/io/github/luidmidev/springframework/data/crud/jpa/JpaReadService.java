package io.github.luidmidev.springframework.data.crud.jpa;


import cz.jirutka.rsql.parser.ast.Node;
import io.github.luidmidev.omnisearch.core.OmniSearchBaseOptions;
import io.github.luidmidev.omnisearch.core.OmniSearchOptions;
import io.github.luidmidev.omnisearch.jpa.JpaOmniSearch;
import io.github.luidmidev.springframework.data.crud.core.StandardReadService;
import io.github.luidmidev.springframework.data.crud.jpa.providers.EntityManagerProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.support.PageableExecutionUtils;

/**
 * CRUD Service for JPA
 *
 * @param <E>  Entity
 * @param <ID> ID
 * @param <R>  Repository
 */
public interface JpaReadService<E extends Persistable<ID>, ID, R extends JpaRepository<E, ID>> extends
        StandardReadService<E, ID, R>,
        EntityManagerProvider {

    @Override
    default Page<E> internalSearch(String search, Pageable pageable) {
        return internalSearch(search, pageable, null);
    }

    @Override
    default Page<E> internalSearch(String search, Pageable pageable, Node query) {
        var options = toSearchOptions(search, pageable, query);
        var entityClass = getEntityClass();
        var omniSearch = new JpaOmniSearch(getEntityManager());
        return PageableExecutionUtils.getPage(
                omniSearch.search(entityClass, options),
                pageable,
                () -> omniSearch.count(entityClass, options)
        );
    }


    @Override
    default long internalCount(String search) {
        return internalCount(search, null);
    }

    @Override
    default long internalCount(String search, Node query) {
        var options = toBaseSearchOptions(search, query);
        var omniSearch = new JpaOmniSearch(getEntityManager());
        return omniSearch.count(getEntityClass(), options);
    }

    default OmniSearchOptions toSearchOptions(String search, Pageable pageable, Node query) {
        return OmniSearchOptionsFactory.create(search, pageable, query);
    }

    default OmniSearchBaseOptions toBaseSearchOptions(String search, Node query) {
        return OmniSearchOptionsFactory.create(search, query);
    }

}
