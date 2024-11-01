package io.github.luidmidev.springframework.data.crud.core;


/**
 * Interface for models that can be used in CRUD operations.
 * @param <ID> ID
 */
public interface CRUDModel<ID> {

    ID getId();

    void setId(ID id);
}
