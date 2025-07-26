package io.github.luidmidev.springframework.data.crud.jpa.controllers;

import io.github.luidmidev.springframework.data.crud.jpa.models.Foo;
import io.github.luidmidev.springframework.data.crud.jpa.dto.FooDto;
import io.github.luidmidev.springframework.data.crud.core.web.controllers.CrudController;
import io.github.luidmidev.springframework.data.crud.jpa.services.FooService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Getter
@RequiredArgsConstructor
@RestController
@RequestMapping("/foos")
public class FooController implements CrudController<Foo, FooDto, Long, FooService> {

    private final FooService service;

}