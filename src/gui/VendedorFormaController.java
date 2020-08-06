package gui;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alertas;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import modelo.entidades.Departamento;
import modelo.entidades.Vendedor;
import modelo.exceptions.ValidarException;
import modelo.servicos.DepartamentoServico;
import modelo.servicos.VendedorServico;

public class VendedorFormaController implements Initializable {

	private Vendedor entidade;
	private VendedorServico servico;
	private DepartamentoServico departamentoServico;
	private List<DataChangeListener> dataChangeListener = new ArrayList<>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpNascimento;
	@FXML
	private TextField txtSalario;
	@FXML
	private ComboBox<Departamento> comboBoxDepartamento;
	@FXML
	private Label labelErroNome;
	@FXML
	private Label labelErroEmail;
	@FXML
	private Label labelErroNascimento;
	@FXML
	private Label labelErroSalario;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btCancelar;

	private ObservableList<Departamento> obsLista;

	public void setVendedor(Vendedor entidade) {
		this.entidade = entidade;
	}

	public void setServicos(VendedorServico servico, DepartamentoServico departamentoServico) {
		this.servico = servico;
		this.departamentoServico = departamentoServico;
	}

	public void inscreverDataChangeListener(DataChangeListener listener) {
		dataChangeListener.add(listener);
	}

	@FXML
	public void onBtSalvarAcao(ActionEvent event) {
		if (entidade == null)
			throw new IllegalStateException("Entidade nulo");
		if (servico == null)
			throw new IllegalStateException("Servico nulo");
		try {
			entidade = getFormaDados();
			servico.salvarOuAtualizar(entidade);
			notificaDataChangeListeners();
			Utils.palcoAtual(event).close();
		} catch (DbException e) {
			Alertas.showAlert("Erro ao salvar", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidarException e) {
			setMensagemErro(e.getErros());
		}

	}

	private void notificaDataChangeListeners() {
		for (DataChangeListener l : dataChangeListener) {
			l.onMudancaDados();
		}
	}

	/*
	 * pega os dados preenchidos no formulario, e carrega um objeto com esses dados
	 */
	private Vendedor getFormaDados() {
		Vendedor obj = new Vendedor();
		ValidarException exception = new ValidarException("Erro ao validar");
		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {

			exception.addErro("nome", "Campo não pode ser vazio");
		}
		obj.setNome(txtNome.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {

			exception.addErro("email", "Campo não pode ser vazio");
		}
		obj.setEmail(txtEmail.getText());

		if (dpNascimento.getValue() == null) {
			exception.addErro("dataAniversario", "Campo não pode ser vazio");
		} else {
			Instant instant = Instant.from(dpNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataAniversario(Date.from(instant));
		}
		if (txtSalario.getText() == null || txtSalario.getText().trim().equals("")) {

			exception.addErro("salarioBase", "Campo não pode ser vazio");
		}
		obj.setSalarioBase(Utils.tryParseToDouble(txtSalario.getText()));
		obj.setDepartamento(comboBoxDepartamento.getValue());

		if (exception.getErros().size() > 0)
			throw exception;
		return obj;
	}

	@FXML
	public void onBtCancelarAcao(ActionEvent event) {
		Utils.palcoAtual(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtSalario);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpNascimento, "dd/MM/yyyy");

		initializeComboBoxDepartment();

	}

	public void atualizarFormaDados() {
		if (entidade == null)
			throw new IllegalStateException("Entidade nula");

		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
		txtEmail.setText(entidade.getEmail());
		Locale.setDefault(Locale.US);
		txtSalario.setText(String.format("%.2f", entidade.getSalarioBase()));

		if (entidade.getDepartamento() == null)
			comboBoxDepartamento.getSelectionModel().selectFirst();
		else
			comboBoxDepartamento.setValue(entidade.getDepartamento());
	}

	public void carregarObjetosAssociados() {
		if (departamentoServico == null)
			throw new IllegalStateException("Servico de departamento está nulo");
		List<Departamento> lista = departamentoServico.findAll();
		obsLista = FXCollections.observableArrayList(lista);
		comboBoxDepartamento.setItems(obsLista);
	}

	private void setMensagemErro(Map<String, String> erros) {
		Set<String> campos = erros.keySet();

		labelErroNome.setText(campos.contains("nome") ? erros.get("nome") : "");
		labelErroEmail.setText(campos.contains("email") ? erros.get("email") : "");
		labelErroNascimento.setText(campos.contains("dataAniversario") ? erros.get("dataAniversario") : "");
		labelErroSalario.setText(campos.contains("salarioBase") ? erros.get("salarioBase") : "");
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}

}
