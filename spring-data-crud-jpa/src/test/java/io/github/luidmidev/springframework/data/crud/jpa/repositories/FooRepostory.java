package io.github.luidmidev.springframework.data.crud.jpa.repositories;

import io.github.luidmidev.springframework.data.crud.jpa.models.Foo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FooRepostory extends JpaRepository<Foo, Long> {
}
