package io.github.luidmidev.springframework.data.crud.core;


public sealed interface Crud permits WriteService, ReadService, CrudService {

    static void preProccess(Crud crud, CrudOperation operation) {
        if (crud instanceof AuthorizeddCrud authorizeddCrud) {
            AuthorizeddCrud.verifyAccess(authorizeddCrud, operation);
        }
        // Aqui se van a agregar operaciones adicionales que deben invocarse
        // inmediatamente antes de ejecutar la operación CRUD.
    }
}
