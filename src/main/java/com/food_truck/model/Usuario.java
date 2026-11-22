package com.food_truck.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nomeUsuario;

    @Column(nullable = false, length = 200)
    private String hashSenha;

    @Column(nullable = false, length = 100)
    private String saltSenha;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, length = 30)
    private String perfil = "ADMIN";

    public Usuario() { }
    public Long getId() { return id; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    public String getHashSenha() { return hashSenha; }
    public void setHashSenha(String hashSenha) { this.hashSenha = hashSenha; }
    public String getSaltSenha() { return saltSenha; }
    public void setSaltSenha(String saltSenha) { this.saltSenha = saltSenha; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
}
