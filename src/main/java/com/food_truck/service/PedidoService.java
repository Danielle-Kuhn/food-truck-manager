
package com.food_truck.service;

import com.food_truck.model.*;
import com.food_truck.repository.PedidoRepository;

import java.util.List;

public class PedidoService {

    private final PedidoRepository repository;

    public PedidoService() {
        this.repository = new PedidoRepository();
    }

    public void cadastrar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException(
                    "O pedido não pode ser nulo."
            );
        }

        if (pedido.getCliente() == null ||
                pedido.getCliente().getId() == null) {
            throw new IllegalArgumentException(
                    "Selecione um cliente."
            );
        }

        if (pedido.getItens() == null ||
                pedido.getItens().isEmpty()) {
            throw new IllegalArgumentException(
                    "Adicione pelo menos um produto ao pedido."
            );
        }

        for (ItemPedido item : pedido.getItens()) {
            if (item.getProduto() == null ||
                    item.getProduto().getId() == null) {
                throw new IllegalArgumentException(
                        "Existe um produto inválido no pedido."
                );
            }

            if (item.getQuantidade() == null ||
                    item.getQuantidade() <= 0) {
                throw new IllegalArgumentException(
                        "A quantidade deve ser maior que zero."
                );
            }

            if (!item.getProduto().isDisponivel()) {
                throw new IllegalArgumentException(
                        "O produto " + item.getProduto().getNome()
                        + " não está disponível."
                );
            }
        }

        pedido.atualizarTotal();
        repository.salvar(pedido);
    }

    public List<Pedido> listarTodos() {
        return repository.listarTodos();
    }

    public Pedido buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Pedido não encontrado."
                        )
                );
    }

    public List<Pedido> buscarPorStatus(StatusPedido status) {
        return repository.buscarPorStatus(status);
    }

    public void atualizarStatus(Long id, StatusPedido status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Selecione um status."
            );
        }

        repository.atualizarStatus(id, status);
    }
}
