package safeBackup.app.view;

import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import safeBackup.app.model.ConfigureSetup;
import safeBackup.app.model.FileEncrypter;

public class DialogoAgregarUsuarioController {

    @FXML private ListView<String> lista_agregar_usuarios;
    @FXML private Button btn_aceptar;
    
    private Stage stage;
    private String directorio;
    private ObservableList<String> usuarios_seleccionados;

    public void setDirectorio(String directorio) {
    	this.directorio = directorio;
    }
    
    public void setStage (Stage stage) {
    	this.stage = stage;
    }
    
    
    @FXML private void initialize() {}
    
    public void initData(ArrayList<String> usuarios) {
    	listarUsuarios(usuarios);
    	if(!lista_agregar_usuarios.getItems().isEmpty()) {
    		System.out.println("La lista de usuarios NO está vacía.");
    	}
    	else {
    		System.out.println("La lista de usuarios está vacía.");
    	}
    }
    
    @FXML private void handleAceptar(ActionEvent event) {
       	String psswd = dialogoPassword(event); //Obtenemos la contraseña para cifrar el archivo para los nuevos usuarios
       	usuarios_seleccionados = lista_agregar_usuarios.getSelectionModel().getSelectedItems();
    	//se llamara al metodo que agregue los usuarios y se cerrara la ventana
       	try {
           	FileEncrypter.addPermission(directorio, usuarios_seleccionados, psswd);
       	}catch(Exception e) {
       		mostrarError(event,"La contraseña introducida no es correcta.");
       	}
       	
    	stage.close();
    }
    
    public String getDirectorio() {
    	return "Ventana de agregar usuarios - directorio escogido: "+directorio;
    }
    
    private void listarUsuarios(ArrayList<String> usuarios) {
    	for(String usuario : usuarios) {
    		if(!usuario.equals(ConfigureSetup.getUserName())){
    			lista_agregar_usuarios.getItems().add(usuario);
    		}
    	}
    	lista_agregar_usuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    
    
    //------------------------------------ FUNCIONES AUXILIARES QUE MUESTRAN LOS DIALOGOS -------------------------------------------
    private String dialogoPassword(ActionEvent event) {
    	
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); //Obtenemos la ventana principal donde se origina el evento
    	
    	//Crear la ventana de dialogo customizada
    	Dialog <String> dialog = new Dialog<>();
    	dialog.setTitle("Escriba su contraseña");
    	dialog.setHeaderText("Para cifrar el archivo es necesario conocer su contraseña");
    	dialog.setGraphic(new ImageView(this.getClass().getResource("/img/question.png").toString()));
    	dialog.initOwner(stage);
    	 	
    	//Botones
    	ButtonType aceptar = new ButtonType("Aceptar", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().addAll(aceptar);
    	
    	//Campos de la ventana de dialogo
    	HBox hbox = new HBox();
    	hbox.setSpacing(15);
    	hbox.setAlignment(Pos.CENTER_LEFT);
    	
    	PasswordField psswd = new PasswordField();
    	psswd.setPromptText("Contraseña");
    	
    	hbox.getChildren().addAll(new Label("Contraseña:"), psswd);
    
    	//Activar/Desactivar el boton de aceptar si se ha escrito la contraseña o no
    	Node btn_aceptar = dialog.getDialogPane().lookupButton(aceptar);
    	btn_aceptar.setDisable(true);
    	
    	psswd.textProperty().addListener((observable, oldValue, newValue) -> {
    		btn_aceptar.setDisable(newValue.trim().isEmpty());
    	});
    	
    	dialog.getDialogPane().setContent(hbox);
    	
    	//Obtener el valor de la contraseña
    	Optional<String> result = dialog.showAndWait(); 
    	String contrasenya = "";
    	if(result.isPresent()) {
    		contrasenya = psswd.getText();
    	}
    	return contrasenya;
    }
    
    private void mostrarError(ActionEvent event, String mensaje) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Ventana de error");
		alert.setHeaderText("Se ha producido un error");
		alert.setContentText(mensaje);
		alert.initOwner(stage);
		alert.showAndWait(); 
	}
}
