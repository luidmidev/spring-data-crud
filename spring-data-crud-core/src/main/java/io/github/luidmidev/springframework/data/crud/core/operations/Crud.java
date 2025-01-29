package io.github.luidmidev.springframework.data.crud.core.operations;

/**
 * Marker interface for CRUD operations.
 * <p>
 * This interface serves as a base for defining the basic operations related to entities in the system.
 * It is used to impose restrictions on the types that are allowed to perform CRUD (Create, Read, Update, Delete) operations.
 * Specifically, this interface is inherited by {@link WriteOperations}, {@link ReadOperations}, and {@link CrudOperations}.
 * </p>
 * <p>
 * The {@code Crud} interface does not provide methods itself but ensures that any class implementing it can participate in the
 * CRUD functionality defined by the specific operation interfaces.
 * </p>
 */
public sealed interface Crud permits WriteOperations, ReadOperations, CrudOperations {
}
