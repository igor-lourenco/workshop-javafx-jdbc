package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.entidades.Departamento;
import modelo.servicos.DepartamentoServico;

public class DepartamentoListaController implements Initializable {

	private DepartamentoServico servico;
	
	@FXML
	private TableView<Departamento> tableViewDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> tabelaColunaId;
	@FXML
	private TableColumn<Departamento, String> tabelaColunaNome;
	@FXML
	private Button btNovo;

	private ObservableList<Departamento> obsLista;
	
	
	@FXML
	public void onBtNovoAction() {
		System.out.println("On buttom Action new");
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
		if(servico == null) 
			throw new IllegalStateException("Servico esta nulo");
		
		List<Departamento> lista = servico.findAll();
		obsLista = FXCollections.observableList(lista);
		tableViewDepartamento.setItems(obsLista);
	}

}
