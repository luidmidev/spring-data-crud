package io.github.luidmidev.springframework.data.crud.core.method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestMethodSecurity {

    @Autowired
    TestProtected testProtected;

    @Test
    void testProtected() {
        testProtected.call();
        Assertions.assertTrue(true);
    }
}
