package com.food_truck.service;

import com.food_truck.model.Usuario;
import com.food_truck.repository.UsuarioRepository;
import com.food_truck.security.SenhaUtil;

public class AutenticacaoService {
    private final UsuarioRepository repository = new UsuarioRepository();

    public void garantirAdministradorInicial() {
        if (repository.buscarPorNome("admin").isEmpty()) {
            String salt = SenhaUtil.gerarSalt();
            Usuario admin = new Usuario();
            admin.setNomeUsuario("admin");
            admin.setSaltSenha(salt);
            admin.setHashSenha(SenhaUtil.gerarHash("admin123", salt));
            admin.setPerfil("ADMIN");
            admin.setAtivo(true);
            repository.salvar(admin);
        }
    }

    public Usuario autenticar(String nome, String senha) {
        if (nome == null || nome.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Informe usuário e senha.");
        }
        Usuario usuario = repository.buscarPorNome(nome.trim())
                .orElseThrow(() -> new IllegalArgumentException("Usuário ou senha inválidos."));
        if (!usuario.isAtivo() || !SenhaUtil.verificar(senha, usuario.getSaltSenha(), usuario.getHashSenha())) {
            throw new IllegalArgumentException("Usuário ou senha inválidos.");
        }
        return usuario;
    }
}
