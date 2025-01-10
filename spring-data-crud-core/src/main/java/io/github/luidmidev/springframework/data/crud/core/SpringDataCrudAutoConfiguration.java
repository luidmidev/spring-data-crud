package io.github.luidmidev.springframework.data.crud.core;

import io.github.luidmidev.springframework.data.crud.core.security.AuthorizationCrudManager;
import io.github.luidmidev.springframework.data.crud.core.security.AuthorizeCrudConfigurer;
import io.github.luidmidev.springframework.data.crud.core.security.AuthorizeCrudConfigurer.AuthorizationManagerCrudMatcherRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;

import java.util.Optional;

@AutoConfiguration
@EnableConfigurationProperties(SpringDataWebProperties.class)
public class SpringDataCrudAutoConfiguration {

    @Bean
    public AuthorizationCrudManager authorizationCrudManager(ApplicationContext context, Optional<Customizer<AuthorizationManagerCrudMatcherRegistry>> customizer) {
        var configurer = new AuthorizeCrudConfigurer(context, customizer.orElse(defaultCustomizer()));
        return configurer.authorizationCrudManager();
    }

    @Bean
    public SpringDataCrudPostProcessor springDataCrudPostProcessor(SpringDataWebProperties properties) {
        return new SpringDataCrudPostProcessor(properties.getPageable(), properties.getSort());
    }

    @Contract(pure = true)
    private static @NotNull Customizer<AuthorizationManagerCrudMatcherRegistry> defaultCustomizer() {
        return registry -> registry.anyOperation().permitAll();
    }

}
