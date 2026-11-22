package com.food_truck;

import com.food_truck.persistencia.JPAUtil;
import com.food_truck.service.AutenticacaoService;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        try {
            JPAUtil.getFactory();
            System.out.println("Hibernate e SQLite inicializados!");

            new AutenticacaoService().garantirAdministradorInicial();

            FXMLLoader fxmlLoader = new FXMLLoader(
                    App.class.getResource("/view/login.fxml")
            );

            Scene scene = new Scene(fxmlLoader.load(), 900, 700);

            stage.setTitle("Food Truck Manager | Acesso");
            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(event ->
                    JPAUtil.fecharConexao()
            );

        } catch (Exception e) {
            e.printStackTrace();

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro de inicialização");
            alerta.setHeaderText("Não foi possível iniciar o sistema.");
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();

            JPAUtil.fecharConexao();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
