package io.github.luidmidev.springframework.data.crud.core;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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

    /**
     * A list of query parameters to ignore in CRUD operations, such as pagination and sorting parameters.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PRIVATE)
    private static List<String> ignoreParams = List.of();

    private final SpringDataWebProperties springDataWebProperties;

    public SpringDataCrudAutoConfiguration(SpringDataWebProperties properties) {
        this.springDataWebProperties = properties;
        var pageable = properties.getPageable();
        var sort = properties.getSort();

        setIgnoreParams(List.of(
                pageable.getPageParameter(),
                pageable.getSizeParameter(),
                sort.getSortParameter(),
                "search"
        ));
    }

    @Bean
    @ConditionalOnClass(SpringDocConfiguration.class)
    @SuppressWarnings("unchecked")
    public OperationCustomizer pageableParameterCustomizer() {
        log.debug("Configuring pageable parameters for Spring Data CRUD operations");

        var pageable = springDataWebProperties.getPageable();
        var sort = springDataWebProperties.getSort();

        var pageParameter = new QueryParameter()
                .name(pageable.getPageParameter())
                .schema(new Schema<Integer>().type("integer")._default(0))
                .description("Page number to retrieve (0-based index)")
                .required(false);

        var sizeParameter = new QueryParameter()
                .name(pageable.getSizeParameter())
                .schema(new Schema<Integer>()
                        .type("integer")
                        ._default(pageable.getDefaultPageSize())
                        .minimum(BigDecimal.valueOf(1))
                        .maximum(BigDecimal.valueOf(pageable.getMaxPageSize())))
                .description("Number of items per page")
                .required(false);

        var sortParameter = new QueryParameter()
                .name(sort.getSortParameter())
                .schema(new ArraySchema().items(new Schema<String>().type("string")))
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

    // Kiss principle: Keep It Simple, Stupid
    public static void clearIgnoreParams(Map<String, ?> map) {
        ignoreParams.forEach(map::remove);
    }
}
