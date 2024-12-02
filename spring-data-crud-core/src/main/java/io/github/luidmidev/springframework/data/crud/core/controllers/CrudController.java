package io.github.luidmidev.springframework.data.crud.core.controllers;

import io.github.luidmidev.springframework.data.crud.core.operations.CrudOperations;
import org.springframework.data.domain.Persistable;

public interface CrudController<M extends Persistable<ID>, D, ID, S extends CrudOperations<M, D, ID>> extends ReadController<M, ID, S>, WriteController<M, D, ID, S> {
}
