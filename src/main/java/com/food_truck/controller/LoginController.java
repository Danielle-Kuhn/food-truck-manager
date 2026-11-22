package com.food_truck.controller;

import com.food_truck.service.AutenticacaoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtSenha;
    private final AutenticacaoService autenticacao = new AutenticacaoService();

    @FXML private void entrar() {
        try {
            autenticacao.autenticar(txtUsuario.getText(), txtSenha.getText());
            Parent tela = FXMLLoader.load(getClass().getResource("/view/principal.fxml"));
            txtUsuario.getScene().setRoot(tela);
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Falha no login"); alerta.setHeaderText(null);
            alerta.setContentText(e.getMessage()); alerta.showAndWait();
            txtSenha.clear();
        }
    }
}
