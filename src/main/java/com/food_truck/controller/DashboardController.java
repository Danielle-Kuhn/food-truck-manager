package com.food_truck.controller;

import com.food_truck.model.Pedido;
import com.food_truck.model.Produto;
import com.food_truck.repository.PedidoRepository;
import com.food_truck.repository.ProdutoRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML private Label lblPedidos, lblFaturamento, lblClientes, lblEstoqueBaixo;
    @FXML private BarChart<String, Number> graficoVendas;
    private final PedidoRepository pedidosRepo = new PedidoRepository();
    private final ProdutoRepository produtosRepo = new ProdutoRepository();

    @FXML public void initialize() { carregarDashboard(); }

    private void carregarDashboard() {
        try {
            List<Pedido> pedidos = pedidosRepo.listarTodos();
            List<Produto> produtos = produtosRepo.listarTodos();
            List<Pedido> validos = pedidos.stream()
                    .filter(p -> p.getStatus() != com.food_truck.model.StatusPedido.CANCELADO).toList();
            List<Pedido> concluidos = pedidos.stream()
                    .filter(p -> p.getStatus() == com.food_truck.model.StatusPedido.FINALIZADO).toList();
            BigDecimal faturamento = concluidos.stream().map(Pedido::getValorTotal)
                    .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            long clientes = validos.stream().map(p -> p.getCliente().getId()).distinct().count();
            long estoqueBaixo = produtos.stream().filter(p -> p.getEstoque() <= 5).count();
            lblPedidos.setText(String.valueOf(validos.size()));
            lblFaturamento.setText("R$ " + faturamento.setScale(2, java.math.RoundingMode.HALF_UP).toString().replace('.', ','));
            lblClientes.setText(String.valueOf(clientes));
            lblEstoqueBaixo.setText(String.valueOf(estoqueBaixo));

            Map<java.time.LocalDate, BigDecimal> porDia = concluidos.stream().collect(Collectors.groupingBy(
                    p -> p.getDataHora().toLocalDate(),
                    TreeMap::new, Collectors.mapping(Pedido::getValorTotal,
                            Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName("Faturamento (R$)");
            porDia.forEach((dia, valor) -> serie.getData().add(
                    new XYChart.Data<>(dia.format(DateTimeFormatter.ofPattern("dd/MM")), valor.doubleValue())));
            graficoVendas.setData(FXCollections.observableArrayList(serie));
        } catch (Exception e) { mostrarErro(e.getMessage()); }
    }

    @FXML private void atualizar() { carregarDashboard(); }
    @FXML private void voltarAoMenu() { navegar("/view/principal.fxml"); }
    @FXML private void abrirRelatorios() { navegar("/view/relatorios.fxml"); }
    private void navegar(String caminho) {
        try { Parent p = FXMLLoader.load(getClass().getResource(caminho)); graficoVendas.getScene().setRoot(p); }
        catch (Exception e) { mostrarErro("Não foi possível abrir a tela: " + e.getMessage()); }
    }
    private void mostrarErro(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR); a.setTitle("Erro"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
