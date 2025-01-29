package io.github.luidmidev.springframework.data.crud.core.providers;

/**
 * Interface for providing access to a service.
 *
 * <p>
 * This interface is intended to be implemented by classes that need to provide access
 * to a specific service. The implementing class should define the logic to return
 * the service instance.
 * </p>
 *
 * @param <S> The type of the service.
 */
public interface ServiceProvider<S> {

    /**
     * Retrieves the service instance.
     *
     * @return The service instance.
     */
    S getService();
}