package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alertas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import modelo.servicos.DepartamentoServico;
import modelo.servicos.VendedorServico;

public class MainViewController implements Initializable{
	
	@FXML
	private MenuItem menuItemVendedor;
	@FXML
	private MenuItem menuItemDepartamento;
	@FXML
	private MenuItem menuItemSobre;
	
	@FXML
	public void onMenuItemVendedorAction() {
		carregarView("/gui/VendedorLista.FXML", (VendedorListaController controller) -> {
			controller.setVendedorServico(new VendedorServico());
			controller.atualizarTableView();
		});
	}
	
	
	@FXML
	public void onMenuItemDepartamentoAction() {
		carregarView("/gui/DepartamentoLista.FXML", (DepartamentoListaController controller) -> {
			controller.setDepartamentoServico(new DepartamentoServico());
			controller.atualizarTableView();
		});
	}
	
	@FXML
	public void onMenuItemSobreAction() {
		carregarView("/gui/Sobre.FXML", x -> {});
	}
	

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}

	private synchronized <T> void carregarView(String nomeAbsoluto, Consumer<T> acaoInicializacao) {
		try {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
		VBox newVBox = loader.load();
		
		Scene mainScene = Main.getMainScene();
		VBox mainVBox =(VBox)((ScrollPane) mainScene.getRoot()).getContent();
		
		Node mainMenu = mainVBox.getChildren().get(0);
		mainVBox.getChildren().clear();
		mainVBox.getChildren().add(mainMenu);
		mainVBox.getChildren().addAll(newVBox.getChildren());
		
		T controller = loader.getController();
		acaoInicializacao.accept(controller);
		
		}catch(IOException e) {
			Alertas.showAlert("IO Exception", "Erro ao carregar", e.getMessage(), AlertType.ERROR);
		}
	}
	
	
	
}
