package io.github.luidmidev.springframework.data.crud.core.service;

import io.github.luidmidev.springframework.data.crud.core.filters.Filter;
import io.github.luidmidev.springframework.data.crud.core.repositories.InMemoryRepository;
import io.github.luidmidev.springframework.data.crud.core.repositories.InMemoryRepository.IdStrategyGenerator;
import io.github.luidmidev.springframework.data.crud.core.services.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrudTestService extends CrudService<Person, PersonDto, Long, InMemoryRepository<Person, Long>> {

    public CrudTestService() {
        super(new InMemoryRepository<>(Person::getId, Person::setId, IdStrategyGenerator.sequential()), Person.class);
    }

    @Override
    protected void mapModel(PersonDto dto, Person model) {
        model.setName(dto.getName());
    }

    @Override
    protected List<Person> search(String search) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Page<Person> search(String search, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<Person> search(String search, Filter filter) {
        return List.of();
    }

    @Override
    protected Page<Person> search(String search, Pageable pageable, Filter filter) {
        return null;
    }

    public void reset() {
        repository.reset();
    }
}
