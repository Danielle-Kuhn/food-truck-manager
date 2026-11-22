
package com.food_truck.repository;

import com.food_truck.model.*;
import com.food_truck.persistencia.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class PedidoRepository {

    public void salvar(Pedido pedido) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Cliente cliente = em.find(
                    Cliente.class,
                    pedido.getCliente().getId()
            );

            if (cliente == null) {
                throw new IllegalArgumentException(
                        "Cliente não encontrado."
                );
            }

            pedido.setCliente(cliente);

            for (ItemPedido item : pedido.getItens()) {
                Produto produto = em.find(
                        Produto.class,
                        item.getProduto().getId()
                );

                if (produto == null) {
                    throw new IllegalArgumentException(
                            "Um dos produtos do pedido não foi encontrado."
                    );
                }

                if (!produto.isDisponivel()) {
                    throw new IllegalArgumentException("Produto indisponível: " + produto.getNome());
                }
                if (produto.getEstoque() < item.getQuantidade()) {
                    throw new IllegalArgumentException("Estoque insuficiente para " + produto.getNome()
                            + ". Disponível: " + produto.getEstoque());
                }
                item.setProduto(produto);
                item.setPedido(pedido);
                produto.setEstoque(produto.getEstoque() - item.getQuantidade());
                if (produto.getEstoque() == 0) {
                    produto.setDisponivel(false);
                }
            }

            pedido.atualizarTotal();

            em.persist(pedido);

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(
                    "Erro ao salvar pedido: " + e.getMessage(), e
            );
        } finally {
            em.close();
        }
    }

    public List<Pedido> listarTodos() {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p " +
                    "JOIN FETCH p.cliente " +
                    "LEFT JOIN FETCH p.itens i " +
                    "LEFT JOIN FETCH i.produto " +
                    "ORDER BY p.dataHora DESC",
                    Pedido.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    public Optional<Pedido> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            List<Pedido> resultado = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p " +
                    "JOIN FETCH p.cliente " +
                    "LEFT JOIN FETCH p.itens i " +
                    "LEFT JOIN FETCH i.produto " +
                    "WHERE p.id = :id",
                    Pedido.class
            )
            .setParameter("id", id)
            .getResultList();

            return resultado.stream().findFirst();

        } finally {
            em.close();
        }
    }

    public List<Pedido> buscarPorStatus(StatusPedido status) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Pedido p " +
                    "JOIN FETCH p.cliente " +
                    "WHERE p.status = :status " +
                    "ORDER BY p.dataHora DESC",
                    Pedido.class
            )
            .setParameter("status", status)
            .getResultList();

        } finally {
            em.close();
        }
    }

    public void atualizarStatus(Long id, StatusPedido novoStatus) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Pedido pedido = em.createQuery(
                    "SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.itens i " +
                    "LEFT JOIN FETCH i.produto WHERE p.id = :id", Pedido.class)
                    .setParameter("id", id).getSingleResult();

            if (pedido == null) {
                throw new IllegalArgumentException("Pedido não encontrado.");
            }

            if (pedido.getStatus() == StatusPedido.CANCELADO && novoStatus != StatusPedido.CANCELADO) {
                throw new IllegalArgumentException("Um pedido cancelado não pode ser reaberto.");
            }

            if (novoStatus == StatusPedido.CANCELADO && pedido.getStatus() != StatusPedido.CANCELADO) {
                for (ItemPedido item : pedido.getItens()) {
                    Produto produto = item.getProduto();
                    produto.setEstoque(produto.getEstoque() + item.getQuantidade());
                    produto.setDisponivel(true);
                }
            }

            pedido.setStatus(novoStatus);

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(
                    "Erro ao atualizar pedido: " + e.getMessage(), e
            );
        } finally {
            em.close();
        }
    }
}
