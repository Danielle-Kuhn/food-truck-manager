
package com.food_truck.repository;

import com.food_truck.model.Produto;
import com.food_truck.persistencia.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class ProdutoRepository {

    // Cadastrar produto
    public void salvar(Produto produto) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();
            em.persist(produto);
            transacao.commit();

        } catch (Exception e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RuntimeException("Erro ao cadastrar produto.", e);

        } finally {
            em.close();
        }
    }

    // Listar todos os produtos
    public List<Produto> listarTodos() {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Produto p " +
                    "JOIN FETCH p.categoria " +
                    "ORDER BY p.nome",
                    Produto.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    // Buscar produto pelo ID
    public Optional<Produto> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            List<Produto> resultado = em.createQuery(
                    "SELECT p FROM Produto p " +
                    "JOIN FETCH p.categoria " +
                    "WHERE p.id = :id",
                    Produto.class
            )
            .setParameter("id", id)
            .getResultList();

            return resultado.stream().findFirst();

        } finally {
            em.close();
        }
    }

    // Atualizar produto
    public void atualizar(Produto produto) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();
            em.merge(produto);
            transacao.commit();

        } catch (Exception e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RuntimeException("Erro ao atualizar produto.", e);

        } finally {
            em.close();
        }
    }

    // Excluir produto
    public void excluir(Long id) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();

            Produto produto = em.find(Produto.class, id);

            if (produto == null) {
                throw new IllegalArgumentException("Produto não encontrado.");
            }

            em.remove(produto);
            transacao.commit();

        } catch (Exception e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RuntimeException("Erro ao excluir produto.", e);

        } finally {
            em.close();
        }
    }

    // Pesquisar produtos pelo nome
    public List<Produto> pesquisarPorNome(String nome) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Produto p " +
                    "JOIN FETCH p.categoria " +
                    "WHERE LOWER(p.nome) LIKE LOWER(:nome) " +
                    "ORDER BY p.nome",
                    Produto.class
            )
            .setParameter("nome", "%" + nome + "%")
            .getResultList();

        } finally {
            em.close();
        }
    }

    // Listar somente produtos disponíveis
    public List<Produto> listarDisponiveis() {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT p FROM Produto p " +
                    "JOIN FETCH p.categoria " +
                    "WHERE p.disponivel = true AND p.estoque > 0 " +
                    "ORDER BY p.nome",
                    Produto.class
            ).getResultList();

        } finally {
            em.close();
        }
    }
}
