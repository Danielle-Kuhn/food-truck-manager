package com.food_truck.repository;

import com.food_truck.model.Categoria;
import com.food_truck.persistencia.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class CategoriaRepository {

    public void salvar(Categoria categoria) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();
            em.persist(categoria);
            transacao.commit();

        } catch (Exception e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RuntimeException("Erro ao salvar categoria.", e);

        } finally {
            em.close();
        }
    }

    public List<Categoria> listarTodos() {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return em.createQuery(
                    "SELECT c FROM Categoria c ORDER BY c.nome",
                    Categoria.class
            ).getResultList();

        } finally {
            em.close();
        }
    }

    public Optional<Categoria> buscarPorId(Long id) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();

        try {
            return Optional.ofNullable(em.find(Categoria.class, id));

        } finally {
            em.close();
        }
    }

    public void atualizar(Categoria categoria) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();
            em.merge(categoria);
            transacao.commit();

        } catch (Exception e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RuntimeException("Erro ao atualizar categoria.", e);

        } finally {
            em.close();
        }
    }

    public void excluir(Long id) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();

            Categoria categoria = em.find(Categoria.class, id);

            if (categoria == null) {
                throw new IllegalArgumentException("Categoria não encontrada.");
            }

            em.remove(categoria);
            transacao.commit();

        } catch (Exception e) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw new RuntimeException("Erro ao excluir categoria.", e);

        } finally {
            em.close();
        }
    }
}
