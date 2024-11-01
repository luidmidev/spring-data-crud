package io.github.luidmidev.springframework.data.crud.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Resolver for CRUD messages
 *
 * @param <ID> ID
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CRUDMessagesResolver<ID> {

    @Builder.Default
    private Supplier<String> deleted = () -> "Deleted";

    @Builder.Default
    private Function<ID, String> notFound = id -> "Not found with id: " + id;

}