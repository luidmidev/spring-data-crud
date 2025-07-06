package io.github.luidmidev.springframework.data.crud.jpa;

import io.github.luidmidev.springframework.data.crud.core.CrudOperation;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationCombiner<E> {
    default Specification<E> combineSpecification(Specification<E> spec, CrudOperation operation) {
        return spec;
    }
}
