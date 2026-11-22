
package com.food_truck.service;

import com.food_truck.model.Cliente;
import com.food_truck.repository.ClienteRepository;

import java.util.List;

public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService() {
        this.repository = new ClienteRepository();
    }

    // Cadastrar cliente
    public void cadastrar(Cliente cliente) {

        validarCliente(cliente);

        cliente.setNome(cliente.getNome().trim());

        if (cliente.getTelefone() != null) {
            cliente.setTelefone(cliente.getTelefone().trim());
        }

        if (cliente.getEmail() != null) {
            cliente.setEmail(cliente.getEmail().trim());
        }

        repository.salvar(cliente);
    }

    // Listar todos os clientes
    public List<Cliente> listarTodos() {
        return repository.listarTodos();
    }

    // Buscar cliente pelo ID
    public Cliente buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cliente não encontrado."
                        )
                );
    }

    // Atualizar cliente
    public void atualizar(Cliente cliente) {

        if (cliente == null || cliente.getId() == null) {
            throw new IllegalArgumentException(
                    "Cliente inválido para atualização."
            );
        }

        validarCliente(cliente);
        buscarPorId(cliente.getId());

        cliente.setNome(cliente.getNome().trim());

        if (cliente.getTelefone() != null) {
            cliente.setTelefone(cliente.getTelefone().trim());
        }

        if (cliente.getEmail() != null) {
            cliente.setEmail(cliente.getEmail().trim());
        }

        repository.atualizar(cliente);
    }

    // Excluir cliente
    public void excluir(Long id) {
        buscarPorId(id);
        repository.excluir(id);
    }

    // Pesquisar cliente pelo nome
    public List<Cliente> pesquisarPorNome(String nome) {

        if (nome == null || nome.isBlank()) {
            return listarTodos();
        }

        return repository.pesquisarPorNome(nome.trim());
    }

    // Validar dados do cliente
    private void validarCliente(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "O cliente não pode ser nulo."
            );
        }

        if (cliente.getNome() == null ||
                cliente.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o nome do cliente."
            );
        }

        if (cliente.getNome().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "O nome deve possuir no máximo 100 caracteres."
            );
        }

        if (cliente.getTelefone() != null &&
                cliente.getTelefone().length() > 20) {
            throw new IllegalArgumentException(
                    "O telefone deve possuir no máximo 20 caracteres."
            );
        }

        if (cliente.getEmail() != null &&
                !cliente.getEmail().isBlank() &&
                !cliente.getEmail().matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
                )) {
            throw new IllegalArgumentException(
                    "Informe um endereço de e-mail válido."
            );
        }

        if (cliente.getEmail() != null &&
                cliente.getEmail().length() > 120) {
            throw new IllegalArgumentException(
                    "O e-mail deve possuir no máximo 120 caracteres."
            );
        }
    }
}
