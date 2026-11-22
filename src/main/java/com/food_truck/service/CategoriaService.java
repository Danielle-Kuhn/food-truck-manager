
package com.food_truck.service;

import com.food_truck.model.Categoria;
import com.food_truck.repository.CategoriaRepository;

import java.util.List;

public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService() {
        this.repository = new CategoriaRepository();
    }

    public void cadastrar(Categoria categoria) {

        validarCategoria(categoria);

        List<Categoria> categorias = repository.listarTodos();

        boolean existe = categorias.stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(
                        categoria.getNome().trim()
                ));

        if (existe) {
            throw new IllegalArgumentException(
                    "Já existe uma categoria com esse nome."
            );
        }

        categoria.setNome(categoria.getNome().trim());

        repository.salvar(categoria);
    }

    public List<Categoria> listarTodos() {
        return repository.listarTodos();
    }

    public Categoria buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Categoria não encontrada."
                        )
                );
    }

    public void atualizar(Categoria categoria) {

        validarCategoria(categoria);

        Categoria existente = buscarPorId(categoria.getId());

        List<Categoria> categorias = repository.listarTodos();

        boolean duplicada = categorias.stream()
                .anyMatch(c ->
                        !c.getId().equals(existente.getId()) &&
                        c.getNome().equalsIgnoreCase(
                                categoria.getNome().trim()
                        )
                );

        if (duplicada) {
            throw new IllegalArgumentException(
                    "Já existe outra categoria com esse nome."
            );
        }

        categoria.setNome(categoria.getNome().trim());

        repository.atualizar(categoria);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        repository.excluir(id);
    }

    private void validarCategoria(Categoria categoria) {

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "A categoria não pode ser nula."
            );
        }

        if (categoria.getNome() == null ||
                categoria.getNome().isBlank()) {
            throw new IllegalArgumentException(
                    "Informe o nome da categoria."
            );
        }

        if (categoria.getNome().trim().length() > 80) {
            throw new IllegalArgumentException(
                    "O nome deve possuir no máximo 80 caracteres."
            );
        }

        if (categoria.getDescricao() != null &&
                categoria.getDescricao().length() > 200) {
            throw new IllegalArgumentException(
                    "A descrição deve possuir no máximo 200 caracteres."
            );
        }
    }
    


}
