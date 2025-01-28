package io.github.luidmidev.springframework.data.crud.core.http.controllers;

import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import org.springframework.data.domain.Persistable;

public interface CrudController<M extends Persistable<ID>, D, ID, S extends CrudOperations<M, D, ID>> extends
        WriteController<M, D, ID, S>,
        ReadController<M, ID, S> {
}
