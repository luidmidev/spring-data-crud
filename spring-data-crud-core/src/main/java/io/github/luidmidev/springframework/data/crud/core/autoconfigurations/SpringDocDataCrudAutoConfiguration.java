package io.github.luidmidev.springframework.data.crud.core.autoconfigurations;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;

import java.math.BigDecimal;
import java.util.*;

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
@EnableConfigurationProperties(SpringDataWebProperties.class)
@ConditionalOnWebApplication
public class SpringDataCrudAutoConfiguration {


    private final SpringDataWebProperties springDataWebProperties;

    public SpringDataCrudAutoConfiguration(SpringDataWebProperties properties) {
        this.springDataWebProperties = properties;
    }

    @Bean
    @ConditionalOnClass(SpringDocConfiguration.class)
    public OperationCustomizer pageableParameterCustomizer() {
        log.debug("Configuring pageable parameters for Spring Data CRUD operations");

        var pageable = springDataWebProperties.getPageable();
        var sort = springDataWebProperties.getSort();

        var pageParameter = new QueryParameter()
                .name(pageable.getPageParameter())
                .schema(new IntegerSchema()._default(0))
                .description("Page number to retrieve (0-based index)")
                .required(false);

        var sizeParameter = new QueryParameter()
                .name(pageable.getSizeParameter())
                .schema(new IntegerSchema()
                        ._default(pageable.getDefaultPageSize())
                        .minimum(BigDecimal.valueOf(1))
                        .maximum(BigDecimal.valueOf(pageable.getMaxPageSize()))
                )
                .description("Number of items per page")
                .required(false);

        var sortParameter = new QueryParameter()
                .name(sort.getSortParameter())
                .schema(new ArraySchema().items(new StringSchema()))
                .description("Sorting criteria in the format: property,(asc|desc). Multiple properties can be specified using commas.")
                .required(false);

        return (operation, handlerMethod) -> {
            var pageableParameterName = resolvePageableParameterName(handlerMethod);
            if (pageableParameterName.isEmpty()) {
                return operation; // No pageable parameter found, return operation as is
            }

            var parameters = operation.getParameters();
            if (parameters == null) {
                parameters = new ArrayList<>();
                operation.setParameters(parameters);
            }

            parameters.removeIf(param -> pageableParameterName.get().equals(param.getName()));

            parameters.add(pageParameter);
            parameters.add(sizeParameter);
            parameters.add(sortParameter);

            return operation;
        };
    }

    @Bean
    @ConditionalOnClass(SpringDocConfiguration.class)
    public OperationCustomizer nodeParameterCustomizer() {
        log.debug("Configuring RSQL query parameter for Spring Data CRUD operations");

        return (operation, handlerMethod) -> {
            var requestParamName = resolveRequestParamName(handlerMethod);
            if (requestParamName.isEmpty()) {
                return operation; // No query parameter found, return operation as is
            }

            var parameterName = requestParamName.get();

            var parameters = operation.getParameters();
            if (parameters == null) {
                parameters = new ArrayList<>();
                operation.setParameters(parameters);
            }

            parameters.removeIf(param -> parameterName.name.equals(param.getName()));
            parameters.add(new QueryParameter()
                    .name(parameterName.name)
                    .schema(new StringSchema())
                    .description("RSQL query string to filter results")
                    .required(parameterName.required)
            );

            return operation;
        };
    }

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

    private static Optional<ParameterName> resolveRequestParamName(HandlerMethod handlerMethod) {
        for (var parameter : handlerMethod.getMethodParameters()) {
            var requestParam = parameter.getParameterAnnotation(RequestParam.class);
            if (requestParam != null && !requestParam.value().isEmpty()) {
                return Optional.of(new ParameterName(requestParam.value(), requestParam.required()));
            }
            var parameterName = parameter.getParameterName();
            if (parameterName != null) {
                return Optional.of(new ParameterName(parameterName, true));
            }
        }
        return Optional.empty();
    }

    private static Optional<String> resolvePageableParameterName(HandlerMethod handlerMethod) {
        for (var parameter : handlerMethod.getMethodParameters()) {
            if (Pageable.class.isAssignableFrom(parameter.getParameterType())) {
                var requestParam = parameter.getParameterAnnotation(RequestParam.class);
                if (requestParam != null && !requestParam.value().isEmpty()) {
                    return Optional.of(requestParam.value());
                }
                var parameterName = parameter.getParameterName();
                return Optional.of(Objects.requireNonNullElse(parameterName, "pageable"));
            }
        }
        return Optional.empty();
    }

    private record ParameterName(String name, boolean required) {
    }
}
