package io.github.luidmidev.springframework.data.crud.core.utils;

import io.github.luidmidev.springframework.web.problemdetails.ApiError;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;

public final class CrudUtils {

    private CrudUtils() {
    }

    public static Pageable resolvePage(int size, int page, Sort.Direction direction, String... properties) {
        if (size < 0) throw ApiError.badRequest("Size must be greater than or equal to 0 for pagination");
        if (size == 0) return Pageable.unpaged();
        if (direction != null && properties != null && properties.length > 0) {
            return PageRequest.of(page, size, direction, properties);
        }
        return PageRequest.of(page, size);
    }

    public static Sort resolveSort(Sort.Direction direction, String... properties) {
        if (direction != null && properties != null && properties.length > 0) {
            return Sort.by(direction, properties);
        }
        return Sort.unsorted();
    }

    public static void cleanParams(MultiValueMap<String, String> params) {
        if (params != null && !params.isEmpty()) {
            params.remove("search");
            params.remove("size");
            params.remove("page");
            params.remove("properties");
            params.remove("direction");
        }
    }
}
