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

    private static Object[] extractMatchingValues(Filter filter, FilterMatcher[] matchers) {
        var values = new Object[matchers.length];
        for (int i = 0; i < matchers.length; i++) {
            var matcher = matchers[i];
            var criteria = filter.get(matcher.property());
            if (criteria == null || criteria.operator() != matcher.operator()) {
                return new Object[0];
            }
            values[i] = criteria.value();
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
