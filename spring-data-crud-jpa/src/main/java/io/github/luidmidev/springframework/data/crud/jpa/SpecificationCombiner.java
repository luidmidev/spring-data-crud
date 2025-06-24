package io.github.luidmidev.springframework.data.crud.jpa;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationCombiner<M> {
    default Specification<M> combineSpecification(Specification<M> spec) {
        return spec;
    }
}
