package io.github.luidmidev.springframework.data.crud.jpa;

import cz.jirutka.rsql.parser.ast.Node;
import io.github.luidmidev.omnisearch.core.OmniSearchBaseOptions;
import io.github.luidmidev.omnisearch.core.OmniSearchOptions;
import io.github.luidmidev.omnisearch.core.schemas.Pagination;
import io.github.luidmidev.omnisearch.core.schemas.Sort;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;

import static io.github.luidmidev.omnisearch.core.schemas.Pagination.unpaginated;
import static io.github.luidmidev.omnisearch.core.schemas.Sort.unsorted;

@UtilityClass
class OmniSearchOptionsFactory {

    static OmniSearchOptions create(String search, Pageable pageable, Node query) {
        var sort = pageable.getSort();
        return new OmniSearchOptions()
                .search(search)
                .conditions(query)
                .pagination(pageable.isUnpaged()
                        ? unpaginated()
                        : new Pagination(pageable.getPageNumber(), pageable.getPageSize()))
                .sort(sort.isUnsorted()
                        ? unsorted()
                        : new Sort(sort.stream()
                        .map(order -> new Sort.Order(order.getProperty(), order.isAscending()))
                        .toList()));
    }

    static OmniSearchBaseOptions create(String search, Node query) {
        return new OmniSearchBaseOptions()
                .search(search)
                .conditions(query);
    }
}
