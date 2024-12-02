package io.github.luidmidev.springframework.data.crud.core.repositories;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class InMemoryRepository<T, ID> implements ListCrudRepository<T, ID>, ListPagingAndSortingRepository<T, ID> {

    @FunctionalInterface
    public interface IdGetter<T, ID> {
        ID getId(T entity);
    }

    @FunctionalInterface
    public interface IdSetter<T, ID> {
        void setId(T entity, ID id);
    }


    private final Map<ID, T> store = new ConcurrentHashMap<>();
    private final IdGetter<T, ID> idGetter;
    private final IdSetter<T, ID> idSetter;
    private final IdStrategyGenerator<ID> idStrategyGenerator;

    @Override
    public <S extends T> @NotNull S save(@NotNull S entity) {
        ID id = idGetter.getId(entity);
        if (id == null) {
            id = idStrategyGenerator.nextId();
            idSetter.setId(entity, id);
        }
        store.put(id, entity);
        return entity;
    }

    @Override
    public <S extends T> @NotNull List<S> saveAll(@NotNull Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public @NotNull Optional<T> findById(@NotNull ID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsById(@NotNull ID id) {
        return store.containsKey(id);
    }

    @Override
    public @NotNull List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public @NotNull List<T> findAllById(@NotNull Iterable<ID> ids) {
        List<T> result = new ArrayList<>();
        for (ID id : ids) {
            T entity = store.get(id);
            if (entity != null) {
                result.add(entity);
            }
        }
        return result;
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void deleteById(@NotNull ID id) {
        store.remove(id);
    }

    @Override
    public void delete(@NotNull T entity) {
        ID id = idGetter.getId(entity);
        store.remove(id);
    }

    @Override
    public void deleteAllById(@NotNull Iterable<? extends ID> ids) {
        for (ID id : ids) {
            store.remove(id);
        }
    }

    @Override
    public void deleteAll(@NotNull Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    @Override
    public void deleteAll() {
        store.clear();
    }

    @Override
    public @NotNull List<T> findAll(@NotNull Sort sort) {
        throw new UnsupportedOperationException("Sorting is not supported for in-memory repository.");
    }

    @Override
    public @NotNull Page<T> findAll(@NotNull Pageable pageable) {
        if (pageable.isUnpaged()) {
            return PageableExecutionUtils.getPage(new ArrayList<>(store.values()), pageable, this::count);
        }

        List<T> content = new ArrayList<>(store.values());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), content.size());
        List<T> paginatedContent = content.subList(start, end);
        return PageableExecutionUtils.getPage(paginatedContent, pageable, this::count);
    }

    public void reset() {
        deleteAll();
        idStrategyGenerator.reset();
    }

    public abstract static class IdStrategyGenerator<ID> {
        private final AtomicReference<ID> lastGeneratedId = new AtomicReference<>();

        public IdStrategyGenerator() {
            initialize(getInitial());
        }

        protected abstract ID generateId(ID lastGeneratedId);

        public ID nextId() {
            ID current = lastGeneratedId.get();
            ID next = generateId(current);
            lastGeneratedId.set(next);
            return next;
        }

        public ID getInitial() {
            return null;
        }

        private void initialize(ID initialId) {
            lastGeneratedId.set(initialId);
        }

        public static IdStrategyGenerator<Long> sequential() {
            return new IdStrategyGenerator<>() {
                @Override
                public Long getInitial() {
                    return 0L;
                }

                @Override
                protected Long generateId(Long lastGeneratedId) {
                    return lastGeneratedId + 1;
                }
            };
        }

        public static IdStrategyGenerator<String> uuidString() {
            return new IdStrategyGenerator<>() {
                @Override
                protected String generateId(String lastGeneratedId) {
                    return UUID.randomUUID().toString();
                }
            };
        }

        public static IdStrategyGenerator<UUID> uuid() {
            return new IdStrategyGenerator<>() {
                @Override
                protected UUID generateId(UUID lastGeneratedId) {
                    return UUID.randomUUID();
                }
            };
        }

        public void reset() {
            initialize(getInitial());
        }
    }
}
