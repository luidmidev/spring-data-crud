package io.github.luidmidev.springframework.data.crud.jpa.utils;

import jakarta.persistence.Embedded;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Transient;
import jakarta.persistence.criteria.*;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Utility class for advanced search in JPA
 */
@Log4j2
@Component
public class AdvanceSearch {

    /**
     * List of FromString to convert String to numeric types
     */
    private static final List<FromString<? extends Number>> FROM_STRINGS = List.of(
            FromString.of(Integer.class, Integer::parseInt),
            FromString.of(Long.class, Long::parseLong),
            FromString.of(Double.class, Double::parseDouble),
            FromString.of(Float.class, Float::parseFloat),
            FromString.of(Short.class, Short::parseShort),
            FromString.of(Byte.class, Byte::parseByte)
    );

    private static <M> Predicate searchInAllColumns(@NotNull String search, Root<M> root, CriteriaBuilder cb, Class<M> domainClass, String... joinColumns) {

        var predicates = new ArrayList<>(getSearchPredicates(search, root, cb, domainClass));

        for (var joinColumn : joinColumns) {
            var join = root.join(joinColumn);
            predicates.addAll(getSearchPredicates(search, join, cb, join.getJavaType()));
        }

        return cb.or(predicates.toArray(Predicate[]::new));
    }

    private static List<Predicate> getSearchPredicates(String search, Path<?> path, CriteriaBuilder cb, Class<?> domainClass) {

        var predicates = new ArrayList<Predicate>();

        for (var field : domainClass.getDeclaredFields()) {

            if (field.isAnnotationPresent(Transient.class)) continue;
            if (field.isAnnotationPresent(Embedded.class)) {
                predicates.addAll(getSearchPredicates(search, path.get(field.getName()), cb, field.getType()));
            }

            try {

                var type = field.getType();
                addPredicatesFromField(search, path, cb, field, type, predicates);

            } catch (IllegalArgumentException e) {
                log.info("Field {} not found in {}", field.getName(), domainClass.getName());
            }

        }
        return predicates;
    }

    private static void addPredicatesFromField(String search, Path<?> path, CriteriaBuilder cb, Field field, Class<?> type, ArrayList<Predicate> predicates) {

        if (String.class.isAssignableFrom(type)) {
            var pathAttribute = path.<String>get(field.getName());
            predicates.add(cb.like(cb.lower(pathAttribute), "%" + search.toLowerCase() + "%"));
        }

        if (Number.class.isAssignableFrom(type)) {
            FROM_STRINGS.forEach(fromString -> addNumberPredicate(predicates, search, fromString, cb, path, field));
        }

        if (Boolean.class.isAssignableFrom(type) && search.matches("true|false")) {
            var pathAttribute = path.<Boolean>get(field.getName());
            predicates.add(cb.equal(pathAttribute, Boolean.parseBoolean(search)));
        }

        if (Year.class.isAssignableFrom(type) && search.matches("\\d{4}")) {
            predicates.add(cb.equal(path.get(field.getName()), Year.parse(search)));
        }
    }


    private static <T extends Number, M> void addNumberPredicate(List<Predicate> predicates, String search, FromString<T> fromString, CriteriaBuilder cb, Path<M> root, Field field) {
        var type = fromString.type();
        var converter = fromString.converter();

        if (field.getType().isAssignableFrom(type) && search.matches("\\d+")) {
            var path = root.<T>get(field.getName());
            predicates.add(cb.equal(path, converter.apply(search)));
        }

    }

    private static boolean isNullOrEmpty(String search) {
        return search == null || search.isBlank();
    }

    public static <M> Page<M> search(EntityManager em, String search, Pageable pageable, AdditionsSearch<M> additions, Class<M> domainClass) {
        return dispatchSearch(em, search, additions, domainClass, (cb, query, root) -> {

            if (pageable.getSort().isSorted()) {
                query.orderBy(resolveOrders(pageable.getSort(), cb, root));
            }

            var results = em
                    .createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            return new PageImpl<>(results, pageable, countBySearch(em, search, additions, domainClass));
        });
    }

    public static <M> List<M> search(EntityManager em, String search, AdditionsSearch<M> additions, Class<M> domainClass) {
        return dispatchSearch(em, search, additions, domainClass, (cb, query, root) -> em.createQuery(query).getResultList());
    }

    public static <M> long countBySearch(EntityManager em, String search, AdditionsSearch<M> additions, Class<M> domainClass) {
        return dispatchSearch(em, search, additions, Long.class, domainClass, (cb, query, root) -> {
            query.select(cb.count(root));
            return em.createQuery(query).getSingleResult();
        });
    }

    private static <M, E> E dispatchSearch(EntityManager em, String search, AdditionsSearch<M> additions, Class<M> entityClass, InitQueryReturn<M, M, E> function) {
        return dispatchSearch(em, search, additions, entityClass, entityClass, function);
    }

    private static <Q, M, E> E dispatchSearch(EntityManager em, String search, AdditionsSearch<M> additions, Class<Q> resultClass, Class<M> entityClass, InitQueryReturn<Q, M, E> function) {

        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(resultClass);
        var root = query.from(entityClass);

        var predicate = getPredicatesSearchPredicate(search, additions, cb, query, root, entityClass);

        query.where(predicate);

        return function.apply(cb, query, root);
    }


    private static List<Order> resolveOrders(Sort sort, CriteriaBuilder cb, Root<?> root) {
        var orders = new ArrayList<Order>();
        for (var order : sort) {
            orders.add(switch (order.getDirection()) {
                case ASC -> cb.asc(root.get(order.getProperty()));
                case DESC -> cb.desc(root.get(order.getProperty()));
            });
        }
        return orders;
    }


    private static <M> Predicate getPredicatesSearchPredicate(String search, AdditionsSearch<M> additions, CriteriaBuilder cb, CriteriaQuery<?> query, Root<M> root, Class<M> domainClass) {

        var isNullOrEmpty = isNullOrEmpty(search);

        if (isNullOrEmpty) {
            if (additions == null) return cb.conjunction();
        } else {
            if (additions == null) return searchInAllColumns(search, root, cb, domainClass);
        }

        var predicate = additions.getSpecification().toPredicate(root, query, cb);

        if (isNullOrEmpty) {
            return predicate == null ? cb.conjunction() : predicate;
        }

        var predicates = searchInAllColumns(search, root, cb, domainClass, additions.getJoins().toArray(String[]::new));

        if (predicate == null) return predicates;

        return switch (additions.getOperator()) {
            case AND -> cb.and(predicates, predicate);
            case OR -> cb.or(predicates, predicate);
        };
    }

    private record FromString<T>(Class<T> type, Function<String, T> converter) {
        static <T> FromString<T> of(Class<T> type, Function<String, T> converter) {
            return new FromString<>(type, converter);
        }
    }

    @FunctionalInterface
    private interface InitQueryReturn<Q, M, E> {
        E apply(CriteriaBuilder criteriaBuilder, CriteriaQuery<Q> criteriaQuery, Root<M> root);
    }

}
