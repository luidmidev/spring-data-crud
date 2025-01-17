package io.github.luidmidev.springframework.data.crud.jpa.utils;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Search Additions used for adding additional search criteria to the searchs
 * @param <M> Model
 */
@Getter(AccessLevel.PACKAGE)
public class AdditionsSearch<M> {

    private boolean hasInvoked = false;

    /**
     * Boolean operator to combine the original search with the additional search
     */
    private Predicate.BooleanOperator operator = Predicate.BooleanOperator.AND;

    /**
     * Specification to add to the search
     */
    private Specification<M> specification = (root, query, cb) -> null;

    /**
     * List of joins to add to the advanced search
     */
    private final List<String> joins = new ArrayList<>();


    /**
     * Set the operator to combine the original search with the additional search
     * @param operator Boolean operator
     * @return this
     */
    public AdditionsSearch<M> operator(@NotNull Predicate.BooleanOperator operator) {
        this.operator = operator;
        return this;
    }

    /**
     * Set the specification to add to the search
     * @param specification Specification
     * @return this
     */
    public AdditionsSearch<M> specification(@NotNull Specification<M> specification) {
        if (hasInvoked) {
            throw new IllegalStateException("Specification id already defined for you");
        }
        this.hasInvoked = true;
        this.specification = specification;
        return this;
    }

    /**
     * Set the operator to AND _and the specification to add to the search
     * @param specification Specification
     * @return this
     */
    public AdditionsSearch<M> and(@NotNull Specification<M> specification) {
        this.operator = Predicate.BooleanOperator.AND;
        return specification(specification);
    }

    /**
     * Set the operator to OR and the specification to add to the search
     * @param specification Specification
     * @return this
     */
    public AdditionsSearch<M> or(@NotNull Specification<M> specification) {
        this.operator = Predicate.BooleanOperator.AND;
        return specification(specification);
    }

    /**
     * Add a join to the advanced search
     * @param join Join
     * @return this
     */
    public AdditionsSearch<M> addJoin(@NotNull String join) {
        this.joins.add(join);
        return this;
    }

    /**
     * Add joins to the advanced search
     * @param joins Joins
     * @return this
     */
    public AdditionsSearch<M> addJoins(@NotNull String @NotNull ... joins) {
        Collections.addAll(this.joins, joins);
        return this;
    }

}
