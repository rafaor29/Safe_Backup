package safeBackup.app.view;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import safeBackup.app.model.ConfigureSetup;
import safeBackup.app.model.FileEncrypter;

public class DialogoBorrarUsuarioController {

    @FXML private ListView<String> lista_borrar_usuarios;
    @FXML private Button btn_aceptar;
    
    private Stage stage;
    private String directorio;
    private ObservableList<String> usuarios_seleccionados;

    public void setDirectorio(String directorio) {
    	this.directorio = directorio;
    	
    }
    
    public String getDirectorio() {
    	return directorio;
    }
    
    public void setStage (Stage stage) {
    	this.stage = stage;
    }
    
    //--------------------------------------------------- METODOS QUE SE COMUNICAN CON LOS ELEMENTOS DE LA INTERFAZ
    
    @FXML private void initialize() {}
    
    public void initData(ArrayList<String> usuarios) {
    	listarUsuarios(usuarios);
    	if(!lista_borrar_usuarios.getItems().isEmpty()) {
    		System.out.println("La lista de usuarios NO está vacía.");
    	}
    	else {
    		System.out.println("La lista de usuarios está vacía.");
    	}
    }
    
    @FXML private void handleAceptar(ActionEvent event) {
    	//se llamara al metodo que borre los usuarios y se cerrara la ventana
    	usuarios_seleccionados = lista_borrar_usuarios.getSelectionModel().getSelectedItems();
    	FileEncrypter.removePermission(directorio, usuarios_seleccionados);
    	
    	if(usuarios_seleccionados.contains(ConfigureSetup.getUserName())) {
    		directorio = null;
    	}
    	
    	stage.close();
    }
    
    //--------------------------------------------------------------------------
    
    private void listarUsuarios(ArrayList<String> usuarios) {
    	
    	for(String usuario : usuarios) {
    		lista_borrar_usuarios.getItems().add(usuario);    			
    	}
    	
    	lista_borrar_usuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}

