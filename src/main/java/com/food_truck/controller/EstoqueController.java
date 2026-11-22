package com.food_truck.controller;

import com.food_truck.model.Produto;
import com.food_truck.service.ProdutoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import java.math.BigDecimal;

public class EstoqueController {
    @FXML private TableView<Produto> tabela;
    @FXML private TableColumn<Produto,String> colProduto,colCategoria,colQuantidade,colSituacao;
    @FXML private TextField txtQuantidade;
    @FXML private CheckBox chkBaixo;
    private final ProdutoService service = new ProdutoService();
    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();

    @FXML public void initialize() {
        colProduto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria().getNome()));
        colQuantidade.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getEstoque())));
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstoque() <= 5 ? "Estoque baixo" : "Normal"));
        tabela.setItems(produtos);
        tabela.getSelectionModel().selectedItemProperty().addListener((o,a,p) -> { if (p != null) txtQuantidade.setText(String.valueOf(p.getEstoque())); });
        carregar();
    }
    private void carregar() { produtos.setAll(service.listarTodos()); if (chkBaixo != null && chkBaixo.isSelected()) filtrarBaixo(); }
    @FXML private void filtrarBaixo() { if (chkBaixo.isSelected()) produtos.setAll(service.listarTodos().stream().filter(p -> p.getEstoque() <= 5).toList()); else carregar(); }
    @FXML private void salvar() {
        Produto p = tabela.getSelectionModel().getSelectedItem();
        if (p == null) { aviso(Alert.AlertType.WARNING,"Selecione um produto."); return; }
        try { int qtd = Integer.parseInt(txtQuantidade.getText().trim()); if (qtd < 0) throw new IllegalArgumentException("Quantidade não pode ser negativa.");
            p.setEstoque(qtd); if (qtd > 0 && !p.isDisponivel()) p.setDisponivel(true); service.atualizar(p); aviso(Alert.AlertType.INFORMATION,"Estoque atualizado."); carregar();
        } catch (NumberFormatException e) { aviso(Alert.AlertType.WARNING,"Informe uma quantidade inteira válida."); }
        catch (Exception e) { aviso(Alert.AlertType.ERROR,e.getMessage()); }
    }
    @FXML private void voltarAoMenu() { try { Parent p=FXMLLoader.load(getClass().getResource("/view/principal.fxml")); tabela.getScene().setRoot(p); } catch(Exception e) { aviso(Alert.AlertType.ERROR,e.getMessage()); } }
    private void aviso(Alert.AlertType t,String m) { Alert a=new Alert(t); a.setTitle("Controle de estoque"); a.setHeaderText(null); a.setContentText(m); a.showAndWait(); }
}
