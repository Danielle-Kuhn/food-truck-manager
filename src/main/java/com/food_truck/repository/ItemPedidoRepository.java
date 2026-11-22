
package com.food_truck.repository;

import com.food_truck.model.ItemPedido;
import com.food_truck.persistencia.JPAUtil;

import jakarta.persistence.EntityManager;

import java.util.List;

public class ItemPedidoRepository {

    // Listar os itens de um pedido
    public List<ItemPedido> listarPorPedido(Long pedidoId) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT i FROM ItemPedido i " +
                    "JOIN FETCH i.produto " +
                    "WHERE i.pedido.id = :pedidoId",
                    ItemPedido.class
            )
            .setParameter("pedidoId", pedidoId)
            .getResultList();

        } finally {
            em.close();
        }
    }

    // Buscar item pelo ID
    public ItemPedido buscarPorId(Long id) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.find(ItemPedido.class, id);

        } finally {
            em.close();
        }
    }
}
