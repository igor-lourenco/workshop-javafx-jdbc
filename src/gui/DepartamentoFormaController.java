package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.entidades.Departamento;

public class DepartamentoFormaController implements Initializable {

	private Departamento entidade;
	
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
	
	public void setDepartamento(Departamento entidade) {
		this.entidade = entidade;
	}

	@FXML
	public void onBtSalvarAcao() {
		System.out.println("onBtSalvarAcao");
	}

	@FXML
	public void onBtCancelarAcao() {
		System.out.println("onBtCancelarAcao");
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
		if(entidade == null) 
			throw new IllegalStateException("Entidade nula");
		
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
	}

}
