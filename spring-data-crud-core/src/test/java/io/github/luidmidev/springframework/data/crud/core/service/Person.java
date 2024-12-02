package io.github.luidmidev.springframework.data.crud.core.service;

import lombok.Data;
import org.springframework.data.domain.Persistable;

@Data
public class Person implements Persistable<Long> {
    private Long id;
    private String name;

    @Override
    public boolean isNew() {
        return id == null;
    }
}