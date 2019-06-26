package safeBackup.app.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import safeBackup.app.Main;
import safeBackup.app.model.ConfigureSetup;

public class VistaInicialController implements Initializable{
 	
	@FXML private Button btn_configuracion;
    @FXML private Button btn_subir_archivos;
    @FXML private Button btn_descargar_archivos;
    @FXML private BorderPane ventana_inicial;
    
    private Main main;
       
    //---------- FUNCIONES ----------
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	
    	//Miramos si se ha configurado antes el escenario para habilitar/deshabilitar los botones
    	//de subir y descargar archivos
    	boolean configurado = ConfigureSetup.isConfigured();
    	
    	if(!configurado) {
    		btn_subir_archivos.setDisable(true);
    		btn_descargar_archivos.setDisable(true);  		
    	}   	
    }
    
   
    @FXML
    private void handleConfiguracionClicked(ActionEvent event) throws IOException {
    	main.mostrarVistaConfiguracion();
    }
    
    @FXML
    private void handleSubirArchivosClicked(ActionEvent event) {
    	main.mostrarVistaSubirArchivos();
    }
    
    @FXML
    private void handleDescargarArchivosClicked(ActionEvent event) {
    	main.mostrarVistaDescargarArchivos();
    }

	public void setMain (Main main) {
		this.main = main;
	}
}


