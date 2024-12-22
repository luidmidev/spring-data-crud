package io.github.luidmidev.springframework.data.crud.core.filters;

import org.springframework.util.Assert;

import java.util.function.Supplier;

public class FilterProcessor {

    @SafeVarargs
    public static <T> T process(Filter filter, Supplier<T> noFilterProcessor, FilterMatcherResolver<T>... resolvers) {
        for (var resolver : resolvers) {
            var values = extractMatchingValues(filter, resolver.criteria());
            if (values.length == 0) continue;
            return resolver.processor().process(values);
        }
        return noFilterProcessor.get();
    }

    private static Object[] extractMatchingValues(Filter filter, FilterMatcher[] criteria) {
        var values = new Object[criteria.length];
        for (int i = 0; i < criteria.length; i++) {
            var criterion = criteria[i];
            var optionalCriteria = filter.get(criterion.property());
            if (optionalCriteria.isEmpty() || optionalCriteria.get().operator() != criterion.operator()) {
                return new Object[0];
            }
            values[i] = optionalCriteria.get().value();
        }
        return values;
    }

    @FunctionalInterface
    public interface FilterValuesProcessor<T> {
        T process(Object... values);
    }

    public record FilterMatcher(String property, FilterOperator operator) {
    }

    @SuppressWarnings("java:S6218")
    public record FilterMatcherResolver<T>(FilterMatcher[] criteria, FilterValuesProcessor<T> processor) {
        public FilterMatcherResolver {
            Assert.notEmpty(criteria, "Criteria must not be empty");
        }
    }

    @SuppressWarnings("java:S6218")
    public record FilterCriteriaGroup(FilterMatcher... criteria) {
        public FilterCriteriaGroup {
            Assert.notEmpty(criteria, "Criteria must not be empty");
        }

        public <T> FilterMatcherResolver<T> resolve(FilterValuesProcessor<T> processor) {
            return new FilterMatcherResolver<>(criteria, processor);
        }
    }

    public static FilterCriteriaGroup of(FilterMatcher... criteria) {
        return new FilterCriteriaGroup(criteria);
    }
}
