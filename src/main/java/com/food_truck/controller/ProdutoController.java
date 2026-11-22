
package com.food_truck.controller;

import com.food_truck.model.Categoria;
import com.food_truck.model.Produto;
import com.food_truck.service.CategoriaService;
import com.food_truck.service.ProdutoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.StringConverter;

import java.math.BigDecimal;

public class ProdutoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtDescricao;
    @FXML private TextField txtPreco;
    @FXML private TextField txtEstoque;
    @FXML private TextField txtPesquisa;
    @FXML private ComboBox<Categoria> comboCategoria;
    @FXML private CheckBox checkDisponivel;

    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Long> colunaId;
    @FXML private TableColumn<Produto, String> colunaNome;
    @FXML private TableColumn<Produto, String> colunaDescricao;
    @FXML private TableColumn<Produto, BigDecimal> colunaPreco;
    @FXML private TableColumn<Produto, String> colunaCategoria;
    @FXML private TableColumn<Produto, String> colunaDisponivel;
    @FXML private TableColumn<Produto, String> colunaEstoque;

    @FXML private Button btnSalvar;

    private final ProdutoService produtoService = new ProdutoService();
    private final CategoriaService categoriaService = new CategoriaService();

    private final ObservableList<Produto> produtos =
            FXCollections.observableArrayList();

    private Produto produtoSelecionado;

    @FXML
    public void initialize() {

        colunaId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colunaNome.setCellValueFactory(
                new PropertyValueFactory<>("nome")
        );

        colunaDescricao.setCellValueFactory(
                new PropertyValueFactory<>("descricao")
        );

        colunaPreco.setCellValueFactory(
                new PropertyValueFactory<>("preco")
        );

        colunaCategoria.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getCategoria().getNome()
                )
        );

        colunaEstoque.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getEstoque()))
        );

        colunaDisponivel.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().isDisponivel() ? "Sim" : "Não"
                )
        );

        tabelaProdutos.setItems(produtos);

        comboCategoria.setItems(
                FXCollections.observableArrayList(
                        categoriaService.listarTodos()
                )
        );

        comboCategoria.setConverter(new StringConverter<>() {

            @Override
            public String toString(Categoria categoria) {
                return categoria == null ? "" : categoria.getNome();
            }

            @Override
            public Categoria fromString(String texto) {
                return null;
            }
        });

        tabelaProdutos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, selecionado) -> {

                    if (selecionado != null) {
                        produtoSelecionado = selecionado;

                        txtNome.setText(selecionado.getNome());
                        txtDescricao.setText(selecionado.getDescricao());
                        txtPreco.setText(selecionado.getPreco().toString());
                        txtEstoque.setText(String.valueOf(selecionado.getEstoque()));

                        comboCategoria.setValue(
                                selecionado.getCategoria()
                        );

                        checkDisponivel.setSelected(
                                selecionado.isDisponivel()
                        );

                        btnSalvar.setText("Atualizar");
                    }
                });

        txtPesquisa.textProperty().addListener(
                (observable, anterior, atual) -> pesquisar()
        );

        carregarProdutos();
    }

    @FXML
    private void salvar() {

        try {

            Categoria categoria = comboCategoria.getValue();

            if (categoria == null) {
                throw new IllegalArgumentException(
                        "Selecione uma categoria."
                );
            }

            BigDecimal preco = new BigDecimal(
                    txtPreco.getText().replace(",", ".")
            );

            Produto produto;

            if (produtoSelecionado == null) {
                produto = new Produto();
            } else {
                produto = produtoSelecionado;
            }

            produto.setNome(txtNome.getText());
            produto.setDescricao(txtDescricao.getText());
            produto.setPreco(preco);
            int estoque = Integer.parseInt(txtEstoque.getText().trim());
            if (estoque < 0) throw new IllegalArgumentException("O estoque não pode ser negativo.");
            produto.setEstoque(estoque);
            produto.setCategoria(categoria);
            produto.setDisponivel(checkDisponivel.isSelected());

            if (produtoSelecionado == null) {

                produtoService.cadastrar(produto);

                mostrarMensagem(
                        AlertType.INFORMATION,
                        "Sucesso",
                        "Produto cadastrado com sucesso."
                );

            } else {

                produtoService.atualizar(produto);

                mostrarMensagem(
                        AlertType.INFORMATION,
                        "Sucesso",
                        "Produto atualizado com sucesso."
                );
            }

            limpar();
            carregarProdutos();

        } catch (NumberFormatException e) {

            mostrarMensagem(
                    AlertType.WARNING,
                    "Preço ou estoque inválido",
                    "Informe um preço válido (ex.: 12.50) e uma quantidade inteira de estoque (ex.: 30)."
            );

        } catch (Exception e) {

            mostrarMensagem(
                    AlertType.ERROR,
                    "Erro",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void excluir() {

        Produto selecionado = tabelaProdutos
                .getSelectionModel()
                .getSelectedItem();

        if (selecionado == null) {
            mostrarMensagem(
                    AlertType.WARNING,
                    "Atenção",
                    "Selecione um produto para excluir."
            );
            return;
        }

        try {

            produtoService.excluir(selecionado.getId());

            mostrarMensagem(
                    AlertType.INFORMATION,
                    "Sucesso",
                    "Produto excluído com sucesso."
            );

            limpar();
            carregarProdutos();

        } catch (Exception e) {

            mostrarMensagem(
                    AlertType.ERROR,
                    "Erro",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void pesquisar() {

        try {
            produtos.setAll(
                    produtoService.pesquisarPorNome(
                            txtPesquisa.getText()
                    )
            );
        } catch (Exception e) {
            mostrarMensagem(
                    AlertType.ERROR,
                    "Erro na pesquisa",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void limpar() {

        txtNome.clear();
        txtDescricao.clear();
        txtPreco.clear();
        txtEstoque.clear();
        txtPesquisa.clear();

        comboCategoria.setValue(null);
        checkDisponivel.setSelected(true);

        produtoSelecionado = null;

        tabelaProdutos.getSelectionModel().clearSelection();

        btnSalvar.setText("Cadastrar");
    }

    @FXML
    private void voltarAoMenu() {

        try {
            javafx.scene.Parent tela =
                    javafx.fxml.FXMLLoader.load(
                            getClass().getResource("/view/principal.fxml")
                    );

            tabelaProdutos.getScene().setRoot(tela);

        } catch (Exception e) {
            mostrarMensagem(
                    AlertType.ERROR,
                    "Erro",
                    "Não foi possível voltar ao menu."
            );
        }
    }

    private void carregarProdutos() {
        produtos.setAll(produtoService.listarTodos());
    }

    private void mostrarMensagem(
            AlertType tipo,
            String titulo,
            String mensagem
    ) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
