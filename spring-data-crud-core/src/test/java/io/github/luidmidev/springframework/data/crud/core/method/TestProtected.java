package io.github.luidmidev.springframework.data.crud.core.method;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestProtected {

    @PreAuthorize("@aaa.process(this, 'ENUM')")
    public void call() {
        log.info("create");
    }
}
