package io.github.luidmidev.springframework.data.crud.core;


import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface StandardCrudService<E extends Persistable<ID>, D, ID, R extends ListCrudRepository<E, ID> & PagingAndSortingRepository<E, ID>> extends
        StandardReadService<E, ID, R>,
        StandardWriteService<E, D, ID, R>,
        CrudService<E, D, ID> {

    @Override
    default E internalFind(ID id) {
        return StandardReadService.super.internalFind(id);
    }
}
