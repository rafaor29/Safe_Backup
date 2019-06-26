package safeBackup.app.view;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import safeBackup.app.Main;
import safeBackup.app.model.ConfigureSetup;
import safeBackup.app.model.FileEncrypter;

public class VistaEditarArchivosCompartidosController {
	
    @FXML private Button btn_inicio;
    @FXML private Button btn_ir_subir;
    @FXML private Button btn_ir_descargas;
    @FXML private Button btn_examinar;
    @FXML private ListView<String> lista_usuarios;
    @FXML private Button btn_agregar;
    @FXML private Button btn_borrar;
    @FXML private TextField text_directorio;
    
    private String path_directorio;
	private Main main;
	
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	@FXML private void initialize() {
		btn_agregar.setDisable(true);
		btn_borrar.setDisable(true);
		text_directorio.setDisable(true);
		text_directorio.setStyle("-fx-opacity: 1");
		lista_usuarios.getSelectionModel().setSelectionMode(null);
		lista_usuarios.setMouseTransparent( true );
    	lista_usuarios.setFocusTraversable( false );
	}
	
	public void initData(String directorio) {
		path_directorio = directorio;	
    	text_directorio.setText(path_directorio);
    	text_directorio.setDisable(true);
		text_directorio.setStyle("-fx-opacity: 1");
    	listarUsuarios();
    	btn_agregar.setDisable(false);
		btn_borrar.setDisable(false);
    }
	
	@FXML private void handleInicio(ActionEvent event) {
		try {
			main.mostrarInicial();
		} catch (IOException e) {
			mostrarError(event, "No se ha podido acceder a la vista de Inicio.");
		}
	}
	
	@FXML private void handleSubir() {
		main.mostrarVistaSubirArchivos();
	}

	@FXML private void handleDescargar() {
		main.mostrarVistaDescargarArchivos();
	}
	
	@FXML private void handleExaminar(ActionEvent event) {
    	examinarArchivos();
    	if(path_directorio != null && path_directorio != "") {
    		
    		System.out.println("archivo del usuario. " + archivoUsuario(path_directorio) + " archivo del servidor. "+path_directorio.contains(ConfigureSetup.configFileLocation));
    		
    		if(archivoUsuario(path_directorio) && path_directorio.contains(ConfigureSetup.path_archivos_cifrados)) {
    				
    			text_directorio.setText(path_directorio);        	
            	//Activamos los botones cuando se pone el directorio
            	btn_agregar.setDisable(false);
        		btn_borrar.setDisable(false);	
        		
        		listarUsuarios();
    		}
    		else {
    			mostrarError(event, "El archivo seleccionado no es válido. Debe ser un archivo del servidor y del que seas autor.");
    		} 		
    	} 	
    }
	
	@FXML private void handleAgregar(ActionEvent event) {
		main.mostrarDialogoAgregarUsuario(path_directorio);
		listarUsuarios();
	}
	
	
	@FXML private void handleBorrar(ActionEvent event) {
		main.mostrarDialogoBorrarUsuarios(path_directorio);
	}
	
	//----------------------------------- FUNCIONES ADICIONALES -----------------------------------
	private void examinarArchivos() {
		FileChooser fc = new FileChooser();
    	fc.setTitle("Examinar archivos");
    	fc.setInitialDirectory(new File(ConfigureSetup.path_archivos_cifrados));
    	
    	File file = fc.showOpenDialog(null);   
    	
    	if(file != null) {
    		path_directorio = file.getAbsolutePath().replace('\\', '/');    		
    	}
	}
	
	private void listarUsuarios() {	
		lista_usuarios.getItems().clear();
		for(String usuario : FileEncrypter.usersWithAccess(text_directorio.getText())) {
    		if(!usuario.equals(ConfigureSetup.getUserName()))
    			lista_usuarios.getItems().add(usuario);
    	}
	}
	
	private boolean archivoUsuario(String path) { //archivosUsuario(String usuario)	
		String[] tokens = path.split("/");
		String nombre_archivo = tokens[tokens.length-1];
		
		return nombre_archivo.endsWith(ConfigureSetup.getUserName());
	}
	
	
	//----------------------------------- FUNCIONES QUE MUESTRAN LAS VENTANAS DE DIALOGO -----------------------------------
	
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
