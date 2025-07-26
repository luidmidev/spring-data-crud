package io.github.luidmidev.springframework.data.crud.jpa.services;

import io.github.luidmidev.springframework.data.crud.jpa.JpaCrudService;
import io.github.luidmidev.springframework.data.crud.jpa.models.Foo;
import io.github.luidmidev.springframework.data.crud.jpa.dto.FooDto;
import io.github.luidmidev.springframework.data.crud.jpa.repositories.FooRepostory;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
public class FooService implements JpaCrudService<Foo, FooDto, Long, FooRepostory> {

    private final FooRepostory repository;
    private final EntityManager entityManager;

    @Override
    public void mapModel(FooDto dto, Foo model) {
        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
        model.setEmail(dto.getEmail());
        model.setDate(dto.getDate());
    }

    @Override
    public Class<Foo> getEntityClass() {
        return Foo.class;
    }
}
