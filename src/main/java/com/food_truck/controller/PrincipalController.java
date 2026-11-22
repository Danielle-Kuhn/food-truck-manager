
package com.food_truck.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PrincipalController {

    @FXML
    private BorderPane painelPrincipal;

    @FXML
    private void abrirCategorias() {
        abrirTela("/view/categorias.fxml");
    }

    @FXML
    private void abrirProdutos() {
        abrirTela("/view/produtos.fxml");
    }

    @FXML
    private void abrirClientes() {
        abrirTela("/view/clientes.fxml");
    }

    @FXML
    private void abrirPedidos() {
        abrirTela("/view/pedidos.fxml");
    }


    @FXML
    private void abrirDashboard() { abrirTela("/view/dashboard.fxml"); }

    @FXML
    private void abrirRelatorios() { abrirTela("/view/relatorios.fxml"); }

    @FXML
    private void abrirEstoque() { abrirTela("/view/estoque.fxml"); }

    @FXML
    private void sair() {
        try {
            Parent tela = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            painelPrincipal.getScene().setRoot(tela);
        } catch (IOException e) {
            mostrarErro("Não foi possível voltar à tela de login.");
        }
    }

    private void abrirTela(String caminho) {

        try {

            Parent tela = FXMLLoader.load(
                    getClass().getResource(caminho)
            );

            painelPrincipal.getScene().setRoot(tela);

        } catch (IOException | NullPointerException e) {

            mostrarErro(
                    "Não foi possível abrir a tela solicitada."
            );

            e.printStackTrace();
        }
    }

    private void mostrarErro(String mensagem) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    @FXML
    private void initialize() {
        // Hint: initialize() will be called when the associated FXML has been completely loaded.
    }
}
