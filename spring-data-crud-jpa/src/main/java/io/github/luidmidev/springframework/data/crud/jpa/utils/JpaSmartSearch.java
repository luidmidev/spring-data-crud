package io.github.luidmidev.springframework.data.crud.jpa.utils;

import io.github.luidmidev.springframework.data.crud.core.utils.StringUtils;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.Year;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Utility class for advanced search in JPA
 */
@Slf4j
@Component
public class JpaSmartSearch {

    /**
     * List of FromString to convert String to numeric types
     */

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private static final List<FromString<? extends Number>> FROM_STRINGS = List.of(
            FromString.of(Integer.class, Integer::parseInt),
            FromString.of(Long.class, Long::parseLong),
            FromString.of(Double.class, Double::parseDouble),
            FromString.of(Float.class, Float::parseFloat),
            FromString.of(Short.class, Short::parseShort),
            FromString.of(Byte.class, Byte::parseByte),
            FromString.of(BigDecimal.class, BigDecimal::new),
            FromString.of(int.class, Integer::parseInt),
            FromString.of(long.class, Long::parseLong),
            FromString.of(double.class, Double::parseDouble),
            FromString.of(float.class, Float::parseFloat),
            FromString.of(short.class, Short::parseShort),
            FromString.of(byte.class, Byte::parseByte)
    );

    private static Class<?> getWrapperType(Class<?> primitiveType) {
        if (primitiveType == int.class) return Integer.class;
        if (primitiveType == long.class) return Long.class;
        if (primitiveType == double.class) return Double.class;
        if (primitiveType == float.class) return Float.class;
        if (primitiveType == boolean.class) return Boolean.class;
        if (primitiveType == char.class) return Character.class;
        if (primitiveType == byte.class) return Byte.class;
        if (primitiveType == short.class) return Short.class;
        return primitiveType;
    }

    public static <M> Page<M> search(EntityManager em, String search, Pageable pageable, Class<M> domainClass) {
        if (log.isDebugEnabled()) {
            log.debug("Searching page {} with search: {}", domainClass.getName(), search);
        }
        return search(em, search, pageable, null, domainClass);
    }

    public static <M> List<M> search(EntityManager em, String search, Sort sort, Class<M> domainClass) {
        if (log.isDebugEnabled()) {
            log.debug("Searching {} with search: {}", domainClass.getName(), search);
        }
        return search(em, search, sort, null, domainClass);
    }


    public static <M> Page<M> search(EntityManager em, String search, Pageable pageable, AdditionsSearch<M> additions, Class<M> domainClass) {

        if (log.isDebugEnabled()) {
            log.debug("Searching page {} with search: {} and additions: {}", domainClass.getName(), search, additions);
        }

        return queryExecutor(em, search, additions, domainClass, (CriteriaBuilder cb, CriteriaQuery<M> query, Root<M> root) -> {

            if (pageable.getSort().isSorted()) {
                query.orderBy(resolveOrders(pageable.getSort(), cb, root));
            }

            if (pageable.isUnpaged()) {
                var results = em.createQuery(query).getResultList();
                return new PageImpl<>(results);
            }

            var results = em
                    .createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            return PageableExecutionUtils.getPage(
                    results,
                    pageable,
                    () -> countBySearch(em, search, additions, domainClass)
            );
        });
    }

    public static <M> List<M> search(EntityManager em, String search, Sort sort, AdditionsSearch<M> additions, Class<M> domainClass) {
        if (log.isDebugEnabled()) {
            log.debug("Searching {} with search: {} and additions: {}", domainClass.getName(), search, additions);
        }

        return queryExecutor(em, search, additions, domainClass, (cb, query, root) -> {

            if (sort.isSorted()) {
                query.orderBy(resolveOrders(sort, cb, root));
            }

            return em.createQuery(query).getResultList();
        });
    }

    public static <M> long countBySearch(EntityManager em, String search, AdditionsSearch<M> additions, Class<M> domainClass) {
        return queryExecutor(em, search, additions, Long.class, domainClass, (cb, query, root) -> {
            query.select(cb.count(root));
            return em.createQuery(query).getSingleResult();
        });
    }

    private static <M> Predicate searchInAllColumns(@NotNull String search, Root<M> root, CriteriaBuilder cb, Class<M> domainClass, String... joinColumns) {

        var predicates = new ArrayList<>(getSearchPredicates(search, root, cb, domainClass));

        for (var joinColumn : joinColumns) {
            var join = root.join(joinColumn, JoinType.LEFT);
            predicates.addAll(getSearchPredicates(search, join, cb, join.getJavaType()));
        }

        return cb.or(predicates.toArray(Predicate[]::new));
    }

    @SuppressWarnings({"java:S135", "java:S3776"})
    private static Collection<Predicate> getSearchPredicates(String search, Path<?> path, CriteriaBuilder cb, Class<?> domainClass) {

        var predicates = new ArrayList<Predicate>();

        for (var field : getAllFields(domainClass)) {

            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }

            if (field.isAnnotationPresent(Convert.class)) {
                addPredicateFromConvertableField(predicates, search, path, cb, field);
                continue;
            }

            if (field.isAnnotationPresent(Embedded.class)) {
                var embedded = path.get(field.getName());
                predicates.addAll(getSearchPredicates(search, embedded, cb, field.getType()));
                continue;
            }

            if (field.isAnnotationPresent(Enumerated.class)) {
                addPredicateEnums(predicates, search, path, field);
                continue;
            }

            if (field.isAnnotationPresent(ElementCollection.class) && path instanceof From<?, ?> from) {
                addElementCollectionPredicates(predicates, search, from, cb, field);
                continue;
            }

            try {
                addBasicPredicates(predicates, search, path, cb, field);
            } catch (IllegalArgumentException e) {
                log.info("Field {} not found in {}", field.getName(), domainClass.getName());
            }

        }
        return predicates;
    }

    @SuppressWarnings("unchecked")
    private static void addElementCollectionPredicates(Collection<Predicate> predicates, String search, From<?, ?> from, CriteriaBuilder cb, Field field) {
        var elementType = getFirstGenericTypeClass(field);

        if (elementType.isEnum()) {
            var candidates = searchEnumCandidates((Class<? extends Enum<?>>) elementType, search);
            if (candidates.isEmpty()) return;
            var joined = from.join(field.getName(), JoinType.LEFT);
            predicates.add(joined.in(candidates));
        }

        if (elementType.isAnnotationPresent(Embeddable.class)) {
            var joined = from.join(field.getName(), JoinType.LEFT);
            predicates.addAll(getSearchPredicates(search, joined, cb, elementType));
            return;
        }

        if (String.class.isAssignableFrom(elementType)) {
            var joined = from.<Object, String>join(field.getName(), JoinType.LEFT);
            predicates.add(cb.like(cb.lower(joined), "%" + search.toLowerCase() + "%"));
            return;
        }

        if (UUID.class.isAssignableFrom(elementType) && UUID_PATTERN.matcher(search).matches()) {
            var joined = from.<Object, UUID>join(field.getName(), JoinType.LEFT);
            predicates.add(cb.equal(joined, UUID.fromString(search)));
        }
    }

    @SuppressWarnings("unchecked")
    private static void addPredicateEnums(Collection<Predicate> predicates, String search, Path<?> path, Field field) {
        Expression<?> expression = null;
        Collection<Enum<?>> candidates = null;
        if (field.isAnnotationPresent(ElementCollection.class) && path instanceof From<?, ?> from) {
            var elementType = getFirstGenericTypeClass(field);
            if (elementType.isEnum()) {
                expression = from.join(field.getName());
                candidates = searchEnumCandidates((Class<? extends Enum<?>>) elementType, search);
            }
        } else {
            var elementType = field.getType();
            if (elementType.isEnum()) {
                expression = path.get(field.getName());
                candidates = searchEnumCandidates((Class<? extends Enum<?>>) elementType, search);
            }
        }

        if (candidates == null || candidates.isEmpty() || expression == null) return;
        predicates.add(expression.in(candidates));
    }

    private static Collection<Enum<?>> searchEnumCandidates(Class<? extends Enum<?>> enumType, String value) {
        var set = new HashSet<Enum<?>>();
        var constants = enumType.getEnumConstants();
        for (var constant : constants) {
            if (constant.name().toLowerCase().contains(value.toLowerCase())) {
                set.add(constant);
                continue;
            }
            if (constant instanceof JpaEnumCandidate enumCandidate && enumCandidate.isCandidate(value)) {
                set.add(constant);
            }
        }
        return set;
    }

    private static Class<?> getFirstGenericTypeClass(Field field) {
        var genericType = (ParameterizedType) field.getGenericType();
        return (Class<?>) genericType.getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    private static void addPredicateFromConvertableField(Collection<Predicate> predicates, String search, Path<?> path, CriteriaBuilder cb, Field field) {
        var convert = field.getAnnotation(Convert.class);
        var conveter = convert.converter();
        if (conveter == null) return;
        if (!AttributeConverter.class.isAssignableFrom(conveter)) return;

        if (isStringDatabaseValue(conveter)) {
            var pathAttribute = path.get(field.getName()).as(String.class);
            predicates.add(cb.like(cb.lower(pathAttribute), "%" + search.toLowerCase() + "%"));
        }
    }

    @SuppressWarnings("java:S3776")
    private static void addBasicPredicates(Collection<Predicate> predicates, String search, Path<?> path, CriteriaBuilder cb, Field field) {
        var type = field.getType();
        var attributeName = field.getName();

        if (String.class.isAssignableFrom(type)) {
            var pathAttribute = path.<String>get(field.getName());
            predicates.add(cb.like(cb.lower(pathAttribute), "%" + search.toLowerCase() + "%"));
        }

        if (UUID.class.isAssignableFrom(type) && UUID_PATTERN.matcher(search).matches()) {
            var pathAttribute = path.<UUID>get(field.getName());
            predicates.add(cb.equal(pathAttribute, UUID.fromString(search)));
        }

        var isNumber = Number.class.isAssignableFrom(type.isPrimitive() ? getWrapperType(type) : type);
        if (isNumber) {
            for (var fromString : FROM_STRINGS) {
                var numberType = fromString.type();
                var converter = fromString.converter();
                if (type.isAssignableFrom(numberType) && search.matches("\\d+")) {
                    var number = path.get(attributeName);
                    var converted = converter.apply(search);
                    predicates.add(cb.equal(number, converted));
                }
            }
            return;
        }

        var isBoolean = Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type);
        if (isBoolean && search.matches("true|false")) {
            var pathAttribute = path.<Boolean>get(attributeName);
            predicates.add(cb.equal(pathAttribute, Boolean.parseBoolean(search)));
            return;
        }

        if (Year.class.isAssignableFrom(type) && search.matches("\\d{4}")) {
            var pathAttribute = path.<Year>get(attributeName);
            predicates.add(cb.equal(pathAttribute, Year.parse(search)));
        }
    }

    private static <M, E> E queryExecutor(
            EntityManager em,
            String search,
            AdditionsSearch<M> additions,
            Class<M> entityClass,
            Executor<M, M, E> executor
    ) {
        return queryExecutor(em, search, additions, entityClass, entityClass, executor);
    }

    private static <Q, M, E> E queryExecutor(
            EntityManager em,
            String search,
            AdditionsSearch<M> additions,
            Class<Q> resultClass,
            Class<M> entityClass,
            Executor<Q, M, E> executor
    ) {

        var criteriaBuilder = em.getCriteriaBuilder();
        var query = criteriaBuilder.createQuery(resultClass);
        var root = query.from(entityClass);

        var predicate = getPredicate(search, additions, criteriaBuilder, query, root, entityClass);

        query.where(predicate);
        return executor.apply(criteriaBuilder, query, root);
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


    public static <M> Predicate getPredicate(
            String search,
            CriteriaBuilder cb,
            CriteriaQuery<?> query,
            Root<M> root,
            Class<M> entityClass
    ) {
        return getPredicate(search, null, cb, query, root, entityClass);
    }

    public static <M> Predicate getPredicate(
            String search,
            AdditionsSearch<M> additions,
            CriteriaBuilder cb,
            CriteriaQuery<?> query,
            Root<M> root,
            Class<M> entityClass
    ) {

        var isNullOrEmpty = StringUtils.isEmpty(search);

        if (isNullOrEmpty) {
            if (additions == null) return cb.conjunction();
        } else {
            if (additions == null) return searchInAllColumns(search, root, cb, entityClass);
        }

        var predicate = additions.getSpecification().toPredicate(root, query, cb);

        if (isNullOrEmpty) {
            return predicate == null ? cb.conjunction() : predicate;
        }

        var predicates = searchInAllColumns(search, root, cb, entityClass, additions.getJoins().toArray(String[]::new));

        if (predicate == null) return predicates;

        return switch (additions.getOperator()) {
            case AND -> cb.and(predicates, predicate);
            case OR -> cb.or(predicates, predicate);
        };
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        // Obtiene campos de la clase actual
        var fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

        // Obtiene campos de la clase padre recursivamente
        if (clazz.getSuperclass() != null) {
            fields.addAll(getAllFields(clazz.getSuperclass()));
        }

        return fields;
    }

    private static boolean isStringDatabaseValue(Class<? extends AttributeConverter<?, ?>> converter) {
        if (converter.getGenericInterfaces().length == 0) return false;
        var genericInterface = (ParameterizedType) converter.getGenericInterfaces()[0];
        var databaseType = (Class<?>) genericInterface.getActualTypeArguments()[1];
        return String.class.equals(databaseType);
    }

    private record FromString<T>(Class<T> type, Function<String, T> converter) {
        static <T> FromString<T> of(Class<T> type, Function<String, T> converter) {
            return new FromString<>(type, converter);
        }
    }

    @FunctionalInterface
    private interface Executor<Q, M, E> {
        E apply(CriteriaBuilder criteriaBuilder, CriteriaQuery<Q> criteriaQuery, Root<M> root);
    }

}
