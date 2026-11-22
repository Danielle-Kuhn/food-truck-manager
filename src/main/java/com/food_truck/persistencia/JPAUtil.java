package com.food_truck.persistencia;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory FACTORY =
            Persistence.createEntityManagerFactory("foodtruckPU");

    public static EntityManagerFactory getFactory() {
        return FACTORY;
    }

    public static void fecharConexao() {
        if (FACTORY.isOpen()) {
            FACTORY.close();
        }
    }
}
