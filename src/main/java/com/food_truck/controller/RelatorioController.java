package com.food_truck.controller;

import com.food_truck.model.Pedido;
import com.food_truck.model.StatusPedido;
import com.food_truck.repository.PedidoRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class RelatorioController {
    @FXML private DatePicker dataInicio, dataFim;
    @FXML private TextField txtCliente;
    @FXML private ComboBox<String> comboStatus;
    @FXML private TableView<Pedido> tabela;
    @FXML private TableColumn<Pedido,String> colId,colCliente,colData,colStatus,colTotal;
    @FXML private Label lblResumo;
    private final PedidoRepository repository = new PedidoRepository();
    private final ObservableList<Pedido> exibidos = FXCollections.observableArrayList();
    private List<Pedido> todos = List.of();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML public void initialize() {
        comboStatus.setItems(FXCollections.observableArrayList("Todos", "ABERTO", "EM_PREPARACAO", "PRONTO", "FINALIZADO", "CANCELADO"));
        comboStatus.setValue("Todos");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente().getNome()));
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDataHora().format(fmt)));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus().name()));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty("R$ " + c.getValue().getValorTotal().toString().replace('.', ',')));
        tabela.setItems(exibidos);
        carregar();
    }

    private void carregar() { todos = repository.listarTodos(); filtrar(); }

    @FXML private void filtrar() {
        try {
            LocalDate inicio = dataInicio.getValue(), fim = dataFim.getValue();
            if (inicio != null && fim != null && fim.isBefore(inicio)) throw new IllegalArgumentException("A data final não pode ser anterior à inicial.");
            String cliente = txtCliente.getText() == null ? "" : txtCliente.getText().trim().toLowerCase(Locale.ROOT);
            String status = comboStatus.getValue();
            List<Pedido> filtrados = todos.stream().filter(p -> inicio == null || !p.getDataHora().toLocalDate().isBefore(inicio))
                    .filter(p -> fim == null || !p.getDataHora().toLocalDate().isAfter(fim))
                    .filter(p -> cliente.isEmpty() || p.getCliente().getNome().toLowerCase(Locale.ROOT).contains(cliente))
                    .filter(p -> status == null || status.equals("Todos") || p.getStatus().name().equals(status)).toList();
            exibidos.setAll(filtrados);
            BigDecimal total = filtrados.stream().filter(p -> p.getStatus() != StatusPedido.CANCELADO)
                    .map(Pedido::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            lblResumo.setText(filtrados.size() + " pedido(s) | Total dos pedidos não cancelados: R$ " + total.toString().replace('.', ','));
        } catch (Exception e) { alerta(Alert.AlertType.WARNING, e.getMessage()); }
    }

    @FXML private void limparFiltros() { dataInicio.setValue(null); dataFim.setValue(null); txtCliente.clear(); comboStatus.setValue("Todos"); filtrar(); }
    @FXML private void exportarCsv() {
        if (exibidos.isEmpty()) { alerta(Alert.AlertType.INFORMATION, "Não há registros para exportar."); return; }
        FileChooser chooser = new FileChooser(); chooser.setTitle("Exportar relatório CSV"); chooser.setInitialFileName("relatorio_pedidos.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo CSV", "*.csv"));
        File arquivo = chooser.showSaveDialog(tabela.getScene().getWindow());
        if (arquivo == null) return;
        try {
            StringBuilder csv = new StringBuilder("\uFEFFPedido;Cliente;Data;Status;Total\n");
            for (Pedido p : exibidos) {
                csv.append(p.getId()).append(';').append(campo(p.getCliente().getNome())).append(';')
                        .append(p.getDataHora().format(fmt)).append(';').append(p.getStatus().name()).append(';')
                        .append(p.getValorTotal().toString().replace('.', ',')).append('\n');
            }
            Files.writeString(arquivo.toPath(), csv.toString(), StandardCharsets.UTF_8);
            alerta(Alert.AlertType.INFORMATION, "Relatório exportado com sucesso.");
        } catch (Exception e) { alerta(Alert.AlertType.ERROR, "Falha ao exportar: " + e.getMessage()); }
    }
    private String campo(String valor) { return "\"" + valor.replace("\"", "\"\"") + "\""; }
    @FXML private void voltarAoMenu() { navegar("/view/principal.fxml"); }
    private void navegar(String caminho) { try { Parent p = FXMLLoader.load(getClass().getResource(caminho)); tabela.getScene().setRoot(p); } catch (Exception e) { alerta(Alert.AlertType.ERROR, e.getMessage()); } }
    private void alerta(Alert.AlertType tipo, String msg) { Alert a = new Alert(tipo); a.setTitle("Relatórios"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait(); }
}
