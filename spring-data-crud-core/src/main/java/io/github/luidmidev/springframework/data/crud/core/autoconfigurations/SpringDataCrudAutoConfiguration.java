package io.github.luidmidev.springframework.data.crud.core.autoconfigurations;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;

/**
 * Autoconfiguration class for setting up Spring Data CRUD operations with custom authorization.
 * <p>
 * This configuration class automatically registers beans necessary for CRUD operations
 * and sets up authorization settings based on the provided {@link SpringDataWebProperties}.
 * It also defines parameters to ignore for pagination, sorting, and search in query parameters.
 * </p>
 */
@Slf4j
@AutoConfiguration
public class SpringDataCrudAutoConfiguration {

    @Bean
    public Converter<String, Node> rsqlQueryConverter() {
        final var parser = new RSQLParser();
        return source -> {
            if (source.isBlank()) {
                return null; // Return null for empty or null input
            }
            try {
                return parser.parse(source);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid RSQL query: " + source, e);
            }
        };
    }
}
