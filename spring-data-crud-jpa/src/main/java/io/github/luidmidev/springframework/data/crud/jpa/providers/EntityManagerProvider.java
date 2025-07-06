package io.github.luidmidev.springframework.data.crud.jpa.providers;

import jakarta.persistence.EntityManager;

public interface EntityManagerProvider {
    EntityManager getEntityManager();
}
