
package com.food_truck.controller;

import com.food_truck.model.Categoria;
import com.food_truck.service.CategoriaService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategoriaController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtDescricao;

    @FXML
    private TableView<Categoria> tabelaCategorias;

    @FXML
    private TableColumn<Categoria, Long> colunaId;

    @FXML
    private TableColumn<Categoria, String> colunaNome;

    @FXML
    private TableColumn<Categoria, String> colunaDescricao;

    @FXML
    private Button btnSalvar;

    private final CategoriaService service = new CategoriaService();

    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList();

    private Categoria categoriaSelecionada;

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

        tabelaCategorias.setItems(categorias);

        tabelaCategorias.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, selecionada) -> {

                    if (selecionada != null) {
                        categoriaSelecionada = selecionada;
                        txtNome.setText(selecionada.getNome());
                        txtDescricao.setText(selecionada.getDescricao());
                        btnSalvar.setText("Atualizar");
                    }
                });

        carregarCategorias();
    }


    @FXML
    private void voltarAoMenu() {

        try {

            javafx.scene.Parent tela = javafx.fxml.FXMLLoader.load(
                    getClass().getResource("/view/principal.fxml")
            );

            tabelaCategorias.getScene().setRoot(tela);

        } catch (Exception e) {

            mostrarMensagem(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível voltar ao menu."
            );

            e.printStackTrace();
        }
    }

    
    @FXML
    private void salvar() {

        try {

            if (categoriaSelecionada == null) {

                Categoria categoria = new Categoria(
                        txtNome.getText(),
                        txtDescricao.getText()
                );

                service.cadastrar(categoria);

                mostrarMensagem(
                        Alert.AlertType.INFORMATION,
                        "Sucesso",
                        "Categoria cadastrada com sucesso."
                );

            } else {

                categoriaSelecionada.setNome(txtNome.getText());
                categoriaSelecionada.setDescricao(txtDescricao.getText());

                service.atualizar(categoriaSelecionada);

                mostrarMensagem(
                        Alert.AlertType.INFORMATION,
                        "Sucesso",
                        "Categoria atualizada com sucesso."
                );
            }

            limpar();
            carregarCategorias();

        } catch (Exception e) {

            mostrarMensagem(
                    Alert.AlertType.ERROR,
                    "Erro",
                    e.getMessage()
            );
        }
    }


    @FXML
    private void excluir() {

        Categoria selecionada = tabelaCategorias
                .getSelectionModel()
                .getSelectedItem();

        if (selecionada == null) {
            mostrarMensagem(
                    Alert.AlertType.WARNING,
                    "Atenção",
                    "Selecione uma categoria para excluir."
            );
            return;
        }

        try {

            service.excluir(selecionada.getId());

            mostrarMensagem(
                    Alert.AlertType.INFORMATION,
                    "Sucesso",
                    "Categoria excluída com sucesso."
            );

            limpar();
            carregarCategorias();

        } catch (Exception e) {

            mostrarMensagem(
                    Alert.AlertType.ERROR,
                    "Erro",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void limpar() {

        txtNome.clear();
        txtDescricao.clear();

        categoriaSelecionada = null;

        tabelaCategorias.getSelectionModel().clearSelection();

        btnSalvar.setText("Cadastrar");
    }

    private void carregarCategorias() {

        categorias.setAll(service.listarTodos());
    }

    private void mostrarMensagem(
            Alert.AlertType tipo,
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
