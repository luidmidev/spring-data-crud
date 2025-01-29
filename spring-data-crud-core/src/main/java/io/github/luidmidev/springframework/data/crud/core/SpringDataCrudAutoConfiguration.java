package io.github.luidmidev.springframework.data.crud.core;

import io.github.luidmidev.springframework.data.crud.core.security.AuthorizationCrudManager;
import io.github.luidmidev.springframework.data.crud.core.security.AuthorizeCrudConfigurer;
import io.github.luidmidev.springframework.data.crud.core.security.AuthorizeCrudConfigurer.AuthorizationManagerCrudMatcherRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Autoconfiguration class for setting up Spring Data CRUD operations with custom authorization.
 * <p>
 * This configuration class automatically registers beans necessary for CRUD operations
 * and sets up authorization settings based on the provided {@link SpringDataWebProperties}.
 * It also defines parameters to ignore for pagination, sorting, and search in query parameters.
 * </p>
 */
@AutoConfiguration
@EnableConfigurationProperties(SpringDataWebProperties.class)
public class SpringDataCrudAutoConfiguration {

    /**
     * A list of query parameters to ignore in CRUD operations, such as pagination and sorting parameters.
     */
    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PRIVATE)
    private static List<String> ignoreParams;

    /**
     * Constructs the autoconfiguration, initializing the list of parameters to ignore.
     * This includes pagination, sorting, and the "search" parameter.
     *
     * @param properties the Spring Data web properties containing pagination and sorting configuration
     */
    public SpringDataCrudAutoConfiguration(SpringDataWebProperties properties) {
        var pageable = properties.getPageable();
        var sort = properties.getSort();
        setIgnoreParams(List.of(
                pageable.getPageParameter(),
                pageable.getSizeParameter(),
                sort.getSortParameter(),
                "search"
        ));
    }

    /**
     * Registers the {@link AuthorizationCrudManager} bean with custom authorization settings.
     * <p>
     * This bean is responsible for managing authorization for CRUD operations and uses the
     * {@link AuthorizeCrudConfigurer} to apply the necessary security settings.
     * </p>
     *
     * @param context   the {@link ApplicationContext} to provide context for the configuration
     * @param customizer an optional {@link Customizer} for the {@link AuthorizationManagerCrudMatcherRegistry}
     * @return the configured {@link AuthorizationCrudManager}
     */
    @Bean
    public AuthorizationCrudManager authorizationCrudManager(ApplicationContext context, Optional<Customizer<AuthorizationManagerCrudMatcherRegistry>> customizer) {
        var configurer = new AuthorizeCrudConfigurer(context, customizer.orElse(defaultCustomizer()));
        return configurer.authorizationCrudManager();
    }

    /**
     * Provides the default customizer for the {@link AuthorizationManagerCrudMatcherRegistry}.
     * <p>
     * The default behavior is to permit all operations, allowing open access to all CRUD operations.
     * </p>
     *
     * @return the default customizer for the authorization manager registry
     */
    @Contract(pure = true)
    private static @NotNull Customizer<AuthorizationManagerCrudMatcherRegistry> defaultCustomizer() {
        return registry -> registry.anyOperation().permitAll();
    }

    public static void clearIgnoreParams(Map<String, ?> map) {
        ignoreParams.forEach(map::remove);
    }
}
