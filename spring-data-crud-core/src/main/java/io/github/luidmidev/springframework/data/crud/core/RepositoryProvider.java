package io.github.luidmidev.springframework.data.crud.core;

/**
 * Interface for providing access to a repository.
 * <p>
 * This interface is intended to be implemented by classes that need to provide access
 * to a specific repository. The implementing class should define the logic to return
 * the repository instance.
 * </p>
 *
 * @param <R> The type of the repository.
 */
public interface RepositoryProvider<R> {

    /**
     * Retrieves the repository instance.
     *
     * @return The repository instance.
     */
    R getRepository();
}