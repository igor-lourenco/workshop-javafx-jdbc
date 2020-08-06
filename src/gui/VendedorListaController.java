package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alertas;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.entidades.Vendedor;
import modelo.servicos.DepartamentoServico;
import modelo.servicos.VendedorServico;

public class VendedorListaController implements Initializable, DataChangeListener {

	private VendedorServico servico;

	@FXML
	private TableView<Vendedor> tableViewVendedor;
	@FXML
	private TableColumn<Vendedor, Integer> tabelaColunaId;
	@FXML
	private TableColumn<Vendedor, String> tabelaColunaNome;
	@FXML
	private TableColumn<Vendedor, String> tabelaColunaEmail;
	@FXML
	private TableColumn<Vendedor, Date> tabelaColunaAniversario;
	@FXML
	private TableColumn<Vendedor, Double> tabelaColunaSalario;
	@FXML
	private TableColumn<Vendedor, Vendedor> tabelaColunaEditar;
	@FXML
	private TableColumn<Vendedor, Vendedor> tabelaColunaRemover;
	@FXML
	private Button btNovo;

	private ObservableList<Vendedor> obsLista;

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parentStage = Utils.palcoAtual(event);
		Vendedor obj = new Vendedor();
		criarFormaDialogo(obj, "/gui/VendedorForma.fxml", parentStage);
	}

	public void setVendedorServico(VendedorServico servico) {
		this.servico = servico;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tabelaColunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tabelaColunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tabelaColunaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tabelaColunaAniversario.setCellValueFactory(new PropertyValueFactory<>("dataAniversario"));
		Utils.formatarTableColumnDate(tabelaColunaAniversario, ("dd/MM/yyyy"));
		tabelaColunaSalario.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
		Utils.formatarTableColumnDouble(tabelaColunaSalario, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedor.prefHeightProperty().bind(stage.heightProperty());

	}

	public void atualizarTableView() {
		if (servico == null)
			throw new IllegalStateException("Servico esta nulo");

		List<Vendedor> lista = servico.findAll();
		obsLista = FXCollections.observableList(lista);
		tableViewVendedor.setItems(obsLista);
		initEditButtons();
		initRemoveButtons();
	}

	private void criarFormaDialogo(Vendedor obj, String nomeAbsoluto, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane pane = loader.load();

			VendedorFormaController controller = loader.getController();
			controller.setVendedor(obj);
			controller.setServicos(new VendedorServico(), new DepartamentoServico());
			controller.carregarObjetosAssociados();
			controller.inscreverDataChangeListener(this);
			controller.atualizarFormaDados();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Digite os dados do Vendedor");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alertas.showAlert("IO Exception", "Erro ao carregar página", e.getMessage(), AlertType.ERROR);
		}
		
	}

	@Override
	public void onMudancaDados() {
		atualizarTableView();

	}

	/* método para editar a tabela */
	private void initEditButtons() {
		tabelaColunaEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tabelaColunaEditar.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> criarFormaDialogo(obj, "/gui/VendedorForma.fxml", Utils.palcoAtual(event)));
			}
		});
	}

	/* método para remover departamento */
	private void initRemoveButtons() {
		tabelaColunaRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tabelaColunaRemover.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removerEntidade(obj));
			}
		});
	}

	private void removerEntidade(Vendedor obj) {
		Optional<ButtonType> resultado = Alertas.showConfirmacao("Confirmação", "Tem certeza que você deseja deletar");

		if (resultado.get() == ButtonType.OK) {
			if (servico == null)
				throw new IllegalStateException("Servico nulo");

			try {
				servico.remover(obj);
				atualizarTableView();
			} catch (DbIntegrityException e) {
				Alertas.showAlert("Erro ao remover", null, e.getMessage(), AlertType.ERROR);

			}

		}
	}

}
