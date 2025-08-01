package io.github.luidmidev.springframework.data.crud.core;


public sealed interface Crud permits WriteService, ReadService, CrudService {

    static void preProccess(Crud crud, CrudOperation operation) {
        if (crud instanceof AuthorizedCrud authorizedCrud) {
            AuthorizedCrud.verifyAccess(authorizedCrud, operation);
        }
        // Aqui se van a agregar operaciones adicionales que deben invocarse
        // inmediatamente antes de ejecutar la operación CRUD.
    }
}
