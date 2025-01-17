package io.github.luidmidev.springframework.data.crud.jpa;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationCombiner<M> {
    default void combineSpecification(Specification<M> spec) {
    }
}
