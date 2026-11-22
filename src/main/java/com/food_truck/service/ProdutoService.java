
package com.food_truck.service;

import com.food_truck.model.Categoria;
import com.food_truck.model.Produto;
import com.food_truck.repository.CategoriaRepository;
import com.food_truck.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService() {
        this.produtoRepository = new ProdutoRepository();
        this.categoriaRepository = new CategoriaRepository();
    }

    // Cadastrar produto
    public void cadastrar(Produto produto) {

        validarProduto(produto);

        Categoria categoria = categoriaRepository
                .buscarPorId(produto.getCategoria().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Categoria não encontrada."
                        )
                );

        produto.setNome(produto.getNome().trim());
        produto.setCategoria(categoria);

        produtoRepository.salvar(produto);
    }

    // Listar todos os produtos
    public List<Produto> listarTodos() {
        return produtoRepository.listarTodos();
    }

    // Buscar produto pelo ID
    public Produto buscarPorId(Long id) {
        return produtoRepository.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado."
                        )
                );
    }

    // Atualizar produto
    public void atualizar(Produto produto) {

        if (produto == null || produto.getId() == null) {
            throw new IllegalArgumentException(
                    "Produto inválido para atualização."
            );
        }

        validarProduto(produto);

        buscarPorId(produto.getId());

        Categoria categoria = categoriaRepository
                .buscarPorId(produto.getCategoria().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Categoria não encontrada."
                        )
                );

        produto.setNome(produto.getNome().trim());
        produto.setCategoria(categoria);

        produtoRepository.atualizar(produto);
    }

    // Excluir produto
    public void excluir(Long id) {
        buscarPorId(id);
        produtoRepository.excluir(id);
    }

    // Pesquisar por nome
    public List<Produto> pesquisarPorNome(String nome) {

        if (nome == null) {
            throw new IllegalArgumentException(
                    "Informe um nome para pesquisar."
            );
        }

        return produtoRepository.pesquisarPorNome(nome.trim());
    }

    // Listar produtos disponíveis
    public List<Produto> listarDisponiveis() {
        return produtoRepository.listarDisponiveis();
    }

    // Validar os dados do produto
    private void validarProduto(Produto produto) {

        if (produto == null) {
            throw new IllegalArgumentException(
                    "O produto não pode ser nulo."
            );
        }

        if (produto.getNome() == null ||
                produto.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o nome do produto."
            );
        }

        if (produto.getNome().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "O nome deve possuir no máximo 100 caracteres."
            );
        }

        if (produto.getEstoque() < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo.");
        }

        if (produto.getPreco() == null ||
                produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior que zero."
            );
        }

        if (produto.getDescricao() != null &&
                produto.getDescricao().length() > 300) {
            throw new IllegalArgumentException(
                    "A descrição deve possuir no máximo 300 caracteres."
            );
        }

        if (produto.getCategoria() == null ||
                produto.getCategoria().getId() == null) {
            throw new IllegalArgumentException(
                    "Selecione uma categoria para o produto."
            );
        }
    }
}
