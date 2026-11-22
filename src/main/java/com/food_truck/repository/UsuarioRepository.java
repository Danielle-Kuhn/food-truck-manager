package com.food_truck.repository;

import com.food_truck.model.Usuario;
import com.food_truck.persistencia.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Optional;

public class UsuarioRepository {
    public Optional<Usuario> buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE LOWER(u.nomeUsuario) = LOWER(:nome)", Usuario.class)
                    .setParameter("nome", nome).getResultList().stream().findFirst();
        } finally { em.close(); }
    }

    public void salvar(Usuario usuario) {
        EntityManager em = JPAUtil.getFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try { tx.begin(); em.persist(usuario); tx.commit(); }
        catch (Exception e) { if (tx.isActive()) tx.rollback(); throw new RuntimeException("Erro ao criar usuário.", e); }
        finally { em.close(); }
    }
}
