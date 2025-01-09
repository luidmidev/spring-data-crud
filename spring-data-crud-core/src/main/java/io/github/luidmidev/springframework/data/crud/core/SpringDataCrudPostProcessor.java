package io.github.luidmidev.springframework.data.crud.core;

import io.github.luidmidev.springframework.data.crud.core.controllers.ReadController;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SpringDataCrudPostProcessor implements BeanPostProcessor {


    private final List<String> ignoreParams;

    public SpringDataCrudPostProcessor(SpringDataWebProperties.Pageable pageable, SpringDataWebProperties.Sort sort) {
        ignoreParams = new ArrayList<>();
        ignoreParams.add(pageable.getPageParameter());
        ignoreParams.add(pageable.getSizeParameter());
        ignoreParams.add(sort.getSortParameter());
        ignoreParams.add("search");
    }

    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) {
        log.debug("Post processing bean type: {}, name: {}", bean.getClass().getName(), beanName);
        if (bean instanceof ReadController<?, ?, ?> readService) {
            readService.setIgnoreParams(ignoreParams);
        }
        return bean;
    }
}
