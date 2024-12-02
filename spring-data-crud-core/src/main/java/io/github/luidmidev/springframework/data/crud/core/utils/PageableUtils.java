package io.github.luidmidev.springframework.data.crud.core.utils;

import io.github.luidmidev.springframework.web.problemdetails.ApiError;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableUtils {

    private PageableUtils() {
    }

    public static Pageable resolvePage(int size, int page, Sort.Direction direction, String... properties) {
        if (size < 0) throw ApiError.badRequest("Size must be greater than or equal to 0 for pagination");
        if (size == 0) return Pageable.unpaged();
        if (direction != null && properties != null && properties.length > 0) {
            return PageRequest.of(page, size, direction, properties);
        }
        return PageRequest.of(page, size);
    }
}
