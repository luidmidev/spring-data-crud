package io.github.luidmidev.springframework.data.crud.core;

import io.github.luidmidev.springframework.data.crud.core.service.CrudTestService;
import io.github.luidmidev.springframework.data.crud.core.service.PersonDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "logging.level.io.github.luidmidev.springframework.data.crud.core.security=trace",
})
class CrudTest {

    @Autowired
    private CrudTestService crudService;

    @BeforeEach
    void beforeEach() {
        crudService.reset();
    }


    @Test
    void create() {
        var dto = new PersonDto();
        dto.setName("juan");

        var model = crudService.create(dto);
        Assertions.assertEquals("juan", model.getName());
        Assertions.assertNotNull(model.getId());
    }

    @Test
    void update() {
        // primero crea el modelo y luego lo actualiza
        var dto = new PersonDto();
        dto.setName("juan");
        var model = crudService.create(dto);

        dto.setName("pedro");
        var id = model.getId();
        Assertions.assertEquals("juan", model.getName());
        Assertions.assertNotNull(id);

        crudService.update(id, dto);

        var updatedModel = crudService.find(model.getId());
        Assertions.assertEquals("pedro", updatedModel.getName());
    }

    @Test
    void delete() {
        var dto = new PersonDto();
        dto.setName("juan");
        var model = crudService.create(dto);

        var id = model.getId();
        Assertions.assertNotNull(id);
        Assertions.assertTrue(crudService.exists(id));

        crudService.delete(id);
        Assertions.assertFalse(crudService.exists(id));
    }

    @Test
    void find() {
        var dto = new PersonDto();
        dto.setName("juan");
        var model = crudService.create(dto);

        var id = model.getId();
        Assertions.assertNotNull(id);

        var foundModel = crudService.find(id);
        Assertions.assertEquals(model, foundModel);
    }

    @Test
    void exists() {
        var dto = new PersonDto();
        dto.setName("juan");
        var model = crudService.create(dto);

        var id = model.getId();

        Assertions.assertNotNull(id);
        Assertions.assertTrue(crudService.exists(id));
    }

    @Test
    void page() {
        var dto = new PersonDto();
        dto.setName("juan");
        crudService.create(dto);

        var dto2 = new PersonDto();
        dto2.setName("pedro");
        crudService.create(dto2);

        var page = crudService.page(null, Pageable.unpaged(), null);
        Assertions.assertEquals(2, page.getTotalElements());
    }

}
