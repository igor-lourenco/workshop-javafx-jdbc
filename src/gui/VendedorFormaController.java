package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alertas;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.entidades.Vendedor;
import modelo.exceptions.ValidarException;
import modelo.servicos.VendedorServico;

public class VendedorFormaController implements Initializable {

	private Vendedor entidade;
	private VendedorServico servico;
	private List<DataChangeListener> dataChangeListener = new ArrayList<>();
	

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private Label labelErroNome;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btCancelar;

	public void setVendedor(Vendedor entidade) {
		this.entidade = entidade;
	}

	public void setVendedorServico(VendedorServico servico) {
		this.servico = servico;
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
		}catch(ValidarException e) {
			setMensagemErro(e.getErros());
		}

	}

	private void notificaDataChangeListeners() {
		for(DataChangeListener l : dataChangeListener) {
			l.onMudancaDados();
		}
	}

	private Vendedor getFormaDados() {
		Vendedor obj = new Vendedor();
		ValidarException exception = new ValidarException("Erro ao validar");
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtNome.getText() == null || txtNome.getText().trim().equals(""));
		exception.addErro("nome", "Campo não pode ser vazio");
		obj.setNome(txtNome.getText());
	
		if(exception.getErros().size() > 0)
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
		Constraints.setTextFieldMaxLength(txtNome, 30);

	}

	public void atualizarFormaDados() {
		if (entidade == null)
			throw new IllegalStateException("Entidade nula");

		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
	}
	
	private void setMensagemErro(Map<String, String> erros) {
		Set<String> campos = erros.keySet();
		
		if(campos.contains("nome"))
			labelErroNome.setText(erros.get("nome"));
	}

}
