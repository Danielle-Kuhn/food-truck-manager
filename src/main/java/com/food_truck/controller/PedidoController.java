
package com.food_truck.controller;

import com.food_truck.model.*;
import com.food_truck.service.ClienteService;
import com.food_truck.service.PedidoService;
import com.food_truck.service.ProdutoService;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class PedidoController {

    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private ComboBox<Produto> comboProduto;
    @FXML private ComboBox<StatusPedido> comboStatus;

    @FXML private TextField txtQuantidade;

    @FXML private Label lblTotal;

    @FXML private TableView<ItemPedido> tabelaItens;
    @FXML private TableColumn<ItemPedido, String> colunaItemProduto;
    @FXML private TableColumn<ItemPedido, String> colunaItemQuantidade;
    @FXML private TableColumn<ItemPedido, String> colunaItemPreco;
    @FXML private TableColumn<ItemPedido, String> colunaItemSubtotal;

    @FXML private TableView<Pedido> tabelaPedidos;
    @FXML private TableColumn<Pedido, String> colunaPedidoId;
    @FXML private TableColumn<Pedido, String> colunaPedidoCliente;
    @FXML private TableColumn<Pedido, String> colunaPedidoData;
    @FXML private TableColumn<Pedido, String> colunaPedidoTotal;
    @FXML private TableColumn<Pedido, String> colunaPedidoStatus;

    @FXML private TableView<ItemPedido> tabelaDetalhes;
    @FXML private TableColumn<ItemPedido, String> colunaDetalheProduto;
    @FXML private TableColumn<ItemPedido, String> colunaDetalheQuantidade;
    @FXML private TableColumn<ItemPedido, String> colunaDetalhePreco;
    @FXML private TableColumn<ItemPedido, String> colunaDetalheSubtotal;

    private final ClienteService clienteService = new ClienteService();
    private final ProdutoService produtoService = new ProdutoService();
    private final PedidoService pedidoService = new PedidoService();

    private final ObservableList<ItemPedido> itens =
            FXCollections.observableArrayList();

    private final ObservableList<Pedido> pedidos =
            FXCollections.observableArrayList();

    private final ObservableList<ItemPedido> itensDetalhados =
            FXCollections.observableArrayList();

    private final NumberFormat moeda =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final ObjectProperty<BigDecimal> totalPedido =
            new SimpleObjectProperty<>(BigDecimal.ZERO);

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        configurarTabelas();
        configurarCombos();

        tabelaItens.setItems(itens);
        tabelaPedidos.setItems(pedidos);
        tabelaDetalhes.setItems(itensDetalhados);

        lblTotal.textProperty().bind(
                Bindings.createStringBinding(
                        () -> moeda.format(totalPedido.get()),
                        totalPedido
                )
        );

        // Atualiza o total sempre que a lista de itens muda.
        itens.addListener(
                (javafx.collections.ListChangeListener<ItemPedido>) change ->
                        atualizarTotal()
        );

        tabelaPedidos.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, selecionado) -> {
                    if (selecionado != null) {
                        comboStatus.setValue(selecionado.getStatus());
                        carregarDetalhes(selecionado);
                    } else {
                        itensDetalhados.clear();
                    }
                });

        carregarDados();
        atualizarTotal();
    }

    private void configurarTabelas() {
        colunaItemProduto.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getProduto().getNome()
                )
        );

        colunaItemQuantidade.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.valueOf(cell.getValue().getQuantidade())
                )
        );

        colunaItemPreco.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        moeda.format(cell.getValue().getPrecoUnitario())
                )
        );

        colunaItemSubtotal.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        moeda.format(cell.getValue().getSubtotal())
                )
        );

        colunaPedidoId.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.valueOf(cell.getValue().getId())
                )
        );

        colunaPedidoCliente.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getCliente().getNome()
                )
        );

        colunaPedidoData.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getDataHora().format(formatoData)
                )
        );

        colunaPedidoTotal.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        moeda.format(cell.getValue().getValorTotal())
                )
        );

        colunaPedidoStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getStatus().toString()
                )
        );

        colunaDetalheProduto.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getProduto().getNome()
                )
        );

        colunaDetalheQuantidade.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.valueOf(cell.getValue().getQuantidade())
                )
        );

        colunaDetalhePreco.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        moeda.format(cell.getValue().getPrecoUnitario())
                )
        );

        colunaDetalheSubtotal.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        moeda.format(cell.getValue().getSubtotal())
                )
        );
    }

    private void configurarCombos() {
        comboCliente.setConverter(new StringConverter<>() {
            @Override
            public String toString(Cliente cliente) {
                return cliente == null ? "" : cliente.getNome();
            }

            @Override
            public Cliente fromString(String texto) {
                return null;
            }
        });

        comboProduto.setConverter(new StringConverter<>() {
            @Override
            public String toString(Produto produto) {
                if (produto == null) {
                    return "";
                }

                return produto.getNome() + " - "
                        + moeda.format(produto.getPreco());
            }

            @Override
            public Produto fromString(String texto) {
                return null;
            }
        });

        comboStatus.setItems(
                FXCollections.observableArrayList(StatusPedido.values())
        );
    }

    private void carregarDados() {
        try {
            comboCliente.setItems(
                    FXCollections.observableArrayList(
                            clienteService.listarTodos()
                    )
            );

            comboProduto.setItems(
                    FXCollections.observableArrayList(
                            produtoService.listarDisponiveis()
                    )
            );

            carregarPedidos();

        } catch (Exception e) {
            mostrarMensagem(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível carregar os dados: " + e.getMessage()
            );
        }
    }

    @FXML
    private void adicionarItem() {
        Produto produto = comboProduto.getValue();

        if (produto == null) {
            mostrarMensagem(
                    Alert.AlertType.WARNING,
                    "Atenção",
                    "Selecione um produto."
            );
            return;
        }

        try {
            int quantidade = Integer.parseInt(
                    txtQuantidade.getText().trim()
            );

            if (quantidade <= 0) {
                throw new NumberFormatException();
            }

            // Se o produto já estiver na lista, soma as quantidades.
            ItemPedido existente = itens.stream()
                    .filter(item ->
                            item.getProduto().getId().equals(produto.getId())
                    )
                    .findFirst()
                    .orElse(null);

            if (existente != null) {
                existente.setQuantidade(
                        existente.getQuantidade() + quantidade
                );
                tabelaItens.refresh();
            } else {
                itens.add(new ItemPedido(produto, quantidade));
            }

            txtQuantidade.clear();
            comboProduto.getSelectionModel().clearSelection();

            atualizarTotal();

        } catch (NumberFormatException e) {
            mostrarMensagem(
                    Alert.AlertType.WARNING,
                    "Quantidade inválida",
                    "Informe uma quantidade inteira maior que zero."
            );
        }
    }

    @FXML
    private void removerItem() {
        ItemPedido selecionado =
                tabelaItens.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarMensagem(
                    Alert.AlertType.WARNING,
                    "Atenção",
                    "Selecione um item para remover."
            );
            return;
        }

        itens.remove(selecionado);
        atualizarTotal();
    }

    private void atualizarTotal() {
        BigDecimal total = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalPedido.set(total);
    }

    @FXML
    private void salvarPedido() {
        Cliente cliente = comboCliente.getValue();

        if (cliente == null) {
            mostrarMensagem(
                    Alert.AlertType.WARNING,
                    "Atenção",
                    "Selecione um cliente."
            );
            return;
        }

        if (itens.isEmpty()) {
            mostrarMensagem(
                    Alert.AlertType.WARNING,
                    "Atenção",
                    "Adicione pelo menos um produto."
            );
            return;
        }

        try {
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);

            for (ItemPedido item : itens) {
                ItemPedido novoItem = new ItemPedido(
                        item.getProduto(),
                        item.getQuantidade()
                );

                pedido.adicionarItem(novoItem);
            }

            pedidoService.cadastrar(pedido);

            mostrarMensagem(
                    Alert.AlertType.INFORMATION,
                    "Sucesso",
                    "Pedido cadastrado com sucesso!"
            );

            limparPedido();
            carregarPedidos();

        } catch (Exception e) {
            mostrarMensagem(
                    Alert.AlertType.ERROR,
                    "Erro ao salvar",
                    e.getMessage()
            );
            e.printStackTrace();
        }
    }

    @FXML
    private void atualizarStatus() {
        Pedido selecionado =
                tabelaPedidos.getSelectionModel().getSelectedItem();

        StatusPedido novoStatus = comboStatus.getValue();

        if (selecionado == null) {
            mostrarMensagem(
                    Alert.AlertType.WARNING,
                    "Atenção",
                    "Selecione um pedido na tabela."
            );
            return;
        }

        if (novoStatus == null) {
            mostrarMensagem(
                    Alert.AlertType.WARNING,
                    "Atenção",
                    "Selecione o novo status."
            );
            return;
        }

        try {
            pedidoService.atualizarStatus(
                    selecionado.getId(),
                    novoStatus
            );

            mostrarMensagem(
                    Alert.AlertType.INFORMATION,
                    "Sucesso",
                    "Status atualizado com sucesso."
            );

            carregarPedidos();

        } catch (Exception e) {
            mostrarMensagem(
                    Alert.AlertType.ERROR,
                    "Erro",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void limparPedido() {
        itens.clear();
        comboCliente.getSelectionModel().clearSelection();
        comboProduto.getSelectionModel().clearSelection();
        txtQuantidade.clear();

        atualizarTotal();
    }

    private void carregarPedidos() {
        pedidos.setAll(pedidoService.listarTodos());
    }

    private void carregarDetalhes(Pedido pedido) {
        itensDetalhados.setAll(pedido.getItens());
    }

    @FXML
    private void voltarAoMenu() {
        try {
            javafx.scene.Parent tela =
                    javafx.fxml.FXMLLoader.load(
                            getClass().getResource("/view/principal.fxml")
                    );

            tabelaPedidos.getScene().setRoot(tela);

        } catch (Exception e) {
            mostrarMensagem(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível voltar ao menu."
            );
            e.printStackTrace();
        }
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
