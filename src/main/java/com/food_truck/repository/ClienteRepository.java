
package com.food_truck.repository;

import com.food_truck.model.Cliente;
import com.food_truck.persistencia.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    // Cadastrar cliente
    public void salvar(Cliente cliente) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();
            em.persist(cliente);
            transacao.commit();

        } catch (Exception e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RuntimeException("Erro ao cadastrar cliente.", e);

        } finally {
            em.close();
        }
    }

    // Listar todos os clientes
    public List<Cliente> listarTodos() {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Cliente c ORDER BY c.nome",
                    Cliente.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // Buscar cliente pelo ID
    public Optional<Cliente> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return Optional.ofNullable(em.find(Cliente.class, id));

        } finally {
            em.close();
        }
    }

    // Atualizar cliente
    public void atualizar(Cliente cliente) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();
            em.merge(cliente);
            transacao.commit();

        } catch (Exception e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RuntimeException("Erro ao atualizar cliente.", e);

        } finally {
            em.close();
        }
    }

    // Excluir cliente
    public void excluir(Long id) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();

            Cliente cliente = em.find(Cliente.class, id);

            if (cliente == null) {
                throw new IllegalArgumentException("Cliente não encontrado.");
            }

            em.remove(cliente);
            transacao.commit();

        } catch (Exception e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RuntimeException("Erro ao excluir cliente.", e);

        } finally {
            em.close();
        }
    }

    // Pesquisar clientes pelo nome
    public List<Cliente> pesquisarPorNome(String nome) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Cliente c " +
                    "WHERE LOWER(c.nome) LIKE LOWER(:nome) " +
                    "ORDER BY c.nome",
                    Cliente.class
            )
            .setParameter("nome", "%" + nome + "%")
            .getResultList();

        } finally {
            em.close();
        }
    }
}
