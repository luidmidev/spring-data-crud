package io.github.luidmidev.springframework.data.crud.core.service;

import io.github.luidmidev.springframework.data.crud.core.repositories.InMemoryRepository;
import io.github.luidmidev.springframework.data.crud.core.repositories.InMemoryRepository.IdStrategyGenerator;
import io.github.luidmidev.springframework.data.crud.core.services.CrudService;
import io.github.luidmidev.springframework.data.crud.core.services.ReadService;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public class CrudTestService extends CrudService<Person, PersonDto, Long, InMemoryRepository<Person, Long>> {

    public CrudTestService() {
        super(new InMemoryRepository<>(Person::getId, Person::setId, IdStrategyGenerator.sequential()));
    }

    @Override
    protected Page<Person> internalSearch(String search, Pageable pageable) {
        return null;
    }

    @Override
    protected Page<Person> internalSearch(String search, Pageable pageable, MultiValueMap<String, String> filters) {
        return null;
    }

    @Override
    protected Person newEntity() {
        return new Person();
    }

    @Override
    protected void mapModel(PersonDto dto, Person model) {
        model.setName(dto.getName());
    }

    public void reset() {
        repository.reset();
    }

}
