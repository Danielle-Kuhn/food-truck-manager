
package com.food_truck.controller;

import com.food_truck.model.Cliente;
import com.food_truck.service.ClienteService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;

public class ClienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPesquisa;

    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, Long> colunaId;
    @FXML private TableColumn<Cliente, String> colunaNome;
    @FXML private TableColumn<Cliente, String> colunaTelefone;
    @FXML private TableColumn<Cliente, String> colunaEmail;

    @FXML private Button btnSalvar;

    private final ClienteService service = new ClienteService();

    private final ObservableList<Cliente> clientes =
            FXCollections.observableArrayList();

    private Cliente clienteSelecionado;

    @FXML
    public void initialize() {

        colunaId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colunaNome.setCellValueFactory(
                new PropertyValueFactory<>("nome")
        );

        colunaTelefone.setCellValueFactory(
                new PropertyValueFactory<>("telefone")
        );

        colunaEmail.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        tabelaClientes.setItems(clientes);

        tabelaClientes.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, selecionado) -> {

                    if (selecionado != null) {

                        clienteSelecionado = selecionado;

                        txtNome.setText(selecionado.getNome());
                        txtTelefone.setText(selecionado.getTelefone());
                        txtEmail.setText(selecionado.getEmail());

                        btnSalvar.setText("Atualizar");
                    }
                });

        txtPesquisa.textProperty().addListener(
                (observable, anterior, atual) -> pesquisar()
        );

        carregarClientes();
    }

    @FXML
    private void salvar() {

        try {

            Cliente cliente;

            if (clienteSelecionado == null) {
                cliente = new Cliente();
            } else {
                cliente = clienteSelecionado;
            }

            cliente.setNome(txtNome.getText());
            cliente.setTelefone(txtTelefone.getText());
            cliente.setEmail(txtEmail.getText());

            if (clienteSelecionado == null) {

                service.cadastrar(cliente);

                mostrarMensagem(
                        AlertType.INFORMATION,
                        "Sucesso",
                        "Cliente cadastrado com sucesso."
                );

            } else {

                service.atualizar(cliente);

                mostrarMensagem(
                        AlertType.INFORMATION,
                        "Sucesso",
                        "Cliente atualizado com sucesso."
                );
            }

            limpar();
            carregarClientes();

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

        Cliente selecionado = tabelaClientes
                .getSelectionModel()
                .getSelectedItem();

        if (selecionado == null) {
            mostrarMensagem(
                    AlertType.WARNING,
                    "Atenção",
                    "Selecione um cliente para excluir."
            );
            return;
        }

        try {

            service.excluir(selecionado.getId());

            mostrarMensagem(
                    AlertType.INFORMATION,
                    "Sucesso",
                    "Cliente excluído com sucesso."
            );

            limpar();
            carregarClientes();

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
            clientes.setAll(
                    service.pesquisarPorNome(txtPesquisa.getText())
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
    private void limpar() {

        txtNome.clear();
        txtTelefone.clear();
        txtEmail.clear();
        txtPesquisa.clear();

        clienteSelecionado = null;

        tabelaClientes.getSelectionModel().clearSelection();

        btnSalvar.setText("Cadastrar");
    }

    @FXML
    private void voltarAoMenu() {

        try {
            javafx.scene.Parent tela =
                    javafx.fxml.FXMLLoader.load(
                            getClass().getResource("/view/principal.fxml")
                    );

            tabelaClientes.getScene().setRoot(tela);

        } catch (Exception e) {
            mostrarMensagem(
                    AlertType.ERROR,
                    "Erro",
                    "Não foi possível voltar ao menu."
            );
        }
    }

    private void carregarClientes() {
        clientes.setAll(service.listarTodos());
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
