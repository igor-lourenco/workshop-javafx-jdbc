package gui;

import java.io.IOException;
import java.net.URL;
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
import modelo.entidades.Departamento;
import modelo.servicos.DepartamentoServico;

public class DepartamentoListaController implements Initializable, DataChangeListener {

	private DepartamentoServico servico;

	@FXML
	private TableView<Departamento> tableViewDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> tabelaColunaId;
	@FXML
	private TableColumn<Departamento, String> tabelaColunaNome;
	@FXML
	private TableColumn<Departamento, Departamento> tabelaColunaEditar;
	@FXML
	private TableColumn<Departamento, Departamento> tabelaColunaRemover;
	@FXML
	private Button btNovo;

	private ObservableList<Departamento> obsLista;

	@FXML
	public void onBtNovoAction(ActionEvent event) {
		Stage parentStage = Utils.palcoAtual(event);
		Departamento obj = new Departamento();
		criarFormaDialogo(obj, "/gui/DepartamentoForma.fxml", parentStage);
	}

	public void setDepartamentoServico(DepartamentoServico servico) {
		this.servico = servico;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tabelaColunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tabelaColunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamento.prefHeightProperty().bind(stage.heightProperty());

	}

	public void atualizarTableView() {
		if (servico == null)
			throw new IllegalStateException("Servico esta nulo");

		List<Departamento> lista = servico.findAll();
		obsLista = FXCollections.observableList(lista);
		tableViewDepartamento.setItems(obsLista);
		initEditButtons();
		initRemoveButtons();
	}

	private void criarFormaDialogo(Departamento obj, String nomeAbsoluto, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane pane = loader.load();

			DepartamentoFormaController controller = loader.getController();
			controller.setDepartamento(obj);
			controller.setDepartamentoServico(new DepartamentoServico());
			controller.inscreverDataChangeListener(this);
			controller.atualizarFormaDados();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Digite os dados do Departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alertas.showAlert("IO Exception", "Erro ao carregar p�gina", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onMudancaDados() {
		atualizarTableView();

	}

	/* m�todo para editar a tabela */
	private void initEditButtons() {
		tabelaColunaEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tabelaColunaEditar.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> criarFormaDialogo(obj, "/gui/DepartamentoForma.fxml", Utils.palcoAtual(event)));
			}
		});
	}

	/* m�todo para remover departamento */
	private void initRemoveButtons() {
		tabelaColunaRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tabelaColunaRemover.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
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

	private void removerEntidade(Departamento obj) {
		Optional<ButtonType> resultado = Alertas.showConfirmacao("Confirma��o", "Tem certeza que voc� deseja deletar");

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
