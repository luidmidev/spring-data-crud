package io.github.luidmidev.springframework.data.crud.core.providers;

/**
 * Interface for providing the entity class type.
 * <p>
 * This interface is used for retrieving the class type of an entity. Implementations of this interface
 * should provide the specific class type of the entity model {@code M}.
 * </p>
 *
 * @param <M> The type of the entity model.
 */
public interface EntityClassProvider<M> {

    /**
     * Retrieves the class type of the entity model {@code M}.
     * <p>
     * This method allows access to the entity class, which can be used for various operations like
     * reflection or type-safe operations in repositories or services.
     * </p>
     *
     * @return The {@link Class} type of the entity model {@code M}.
     */
    Class<M> getEntityClass();
}