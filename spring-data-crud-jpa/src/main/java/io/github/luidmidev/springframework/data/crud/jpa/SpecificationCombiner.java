package io.github.luidmidev.springframework.data.crud.jpa;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationCombiner<M> {
    default Specification<M> processSpecification(Specification<M> spec) {
        return spec;
    }
}
