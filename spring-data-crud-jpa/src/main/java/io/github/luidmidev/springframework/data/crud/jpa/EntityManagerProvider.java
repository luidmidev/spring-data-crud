package io.github.luidmidev.springframework.data.crud.jpa;

import jakarta.persistence.EntityManager;

public interface EntityManagerProvider {

    EntityManager getEntityManager();
}
