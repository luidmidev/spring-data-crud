package io.github.luidmidev.springframework.data.crud.core;

import org.springframework.data.domain.Persistable;

/**
 * Interface for entities that support an "enabled" state.
 * <p>
 * This interface extends {@link Persistable} and adds functionality for managing
 * an "enabled" state of the entity. Entities implementing this interface can
 * specify whether they are enabled or not, providing flexibility for use cases
 * where entities can be logically disabled without being removed from storage.
 * </p>
 *
 * @param <ID> The type of the entity's ID.
 */
public interface EnabledStatePersistable<ID> extends Persistable<ID> {

    /**
     * Checks if the entity is enabled.
     *
     * @return true if the entity is enabled, false otherwise.
     */
    boolean isEnabled();

    /**
     * Sets the enabled state of the entity.
     *
     * @param enabled true to enable the entity, false to disable it.
     */
    void setEnabled(boolean enabled);
}