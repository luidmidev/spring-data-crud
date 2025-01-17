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
import java.util.Optional;

@AutoConfiguration
@EnableConfigurationProperties(SpringDataWebProperties.class)
public class SpringDataCrudAutoConfiguration {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PRIVATE)
    private static List<String> ignoreParams;

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


    @Bean
    public AuthorizationCrudManager authorizationCrudManager(ApplicationContext context, Optional<Customizer<AuthorizationManagerCrudMatcherRegistry>> customizer) {
        var configurer = new AuthorizeCrudConfigurer(context, customizer.orElse(defaultCustomizer()));
        return configurer.authorizationCrudManager();
    }


    @Contract(pure = true)
    private static @NotNull Customizer<AuthorizationManagerCrudMatcherRegistry> defaultCustomizer() {
        return registry -> registry.anyOperation().permitAll();
    }

}
