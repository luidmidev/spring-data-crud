package io.github.luidmidev.springframework.data.crud.core.method;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component("aaa")
public class TestBean {

    public enum TestEnum {
        TEST, ENUM
    }

    public boolean process(Object target, TestEnum permission) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Processing target: {} with permission: {}, Authentication: {}", target, permission, authentication);
        return true;
    }
}
