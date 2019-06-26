package safeBackup.app.view;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import safeBackup.app.Main;
import safeBackup.app.model.ConfigureSetup;
import safeBackup.app.model.FileDecrypter;

public class VistaDescargarArchivosController {
	
	@FXML private Button btn_descargar;
    @FXML private Button btn_examinar;
    @FXML private TextField text_descarga;
    
    private String path_descarga;
    private Main main;
    
    @FXML private void initialize() {
		
    	text_descarga.setDisable(true);
    	text_descarga.setStyle("-fx-opacity: 1");
		
	}
    
    @FXML private void handleExaminar(ActionEvent event) {
    	FileChooser fc = new FileChooser();
    	fc.setTitle("Examinar archivos");
    	fc.setInitialDirectory(new File(ConfigureSetup.path_archivos_cifrados));
    	
    	File file = fc.showOpenDialog(null);     
    	
    	if(file != null) {
    		path_descarga = file.getAbsolutePath().replace('\\', '/');
    		text_descarga.setText(path_descarga);	
    	}
    }
    
    @FXML private void handleDescargar(ActionEvent event) {
    	if(text_descarga != null && text_descarga.getText().length() != 0) { 
    		if(existeDirectorio(text_descarga.getText()) && text_descarga.getText().contains(ConfigureSetup.path_archivos_cifrados)) {
    			
        		String path_archivo = text_descarga.getText();
        		
        		String p = "";
        		System.out.println("Contraseña: "+p);
        		System.out.println(p != "");
        		boolean errorPass = false;
        		
        		/*do {
        			p = dialogoContrasenya(event);
        			errorPass = false;
        			if(p==null || p=="") {
            			mensajeError(event, "Se ha producido un error al descifrar el archivo. Pruebe de nuevo.");
            			//p = dialogoContrasenya(event);
            		}else {
            			boolean descifrado = false;
            			try {
            				descifrado = descifrar(event, path_archivo, p);
            				if(!descifrado) {
            					mensajeError(event, "Se ha producido un error al descifrar el archivo. Pruebe de nuevo.");
            				}
            				else {
            					mensajeExito(event, "El archivo se ha descifrado correctamente. Acceda al directorio para comprobarlo.");
                    			text_descarga.setText("");
            				}
            			}
            			catch(Exception e) {
            				mensajeError(event, "La contraseña es incorrecta.");
            				errorPass = true;
            				
            			}
            		}
        		}while(errorPass==true);*/
        		
        		do {
        			p = dialogoContrasenya(event);
        			errorPass = false;
        			if(p!=null &&  p!="") {
            			//mensajeError(event, "Se ha producido un error al descifrar el archivo. Pruebe de nuevo.");
            			//p = dialogoContrasenya(event);
        				
        				boolean descifrado = false;
            			try {
            				descifrado = descifrar(event, path_archivo, p);
            				if(!descifrado) {
            					mensajeError(event, "Se ha producido un error al descifrar el archivo. Pruebe de nuevo.");
            				}
            				else {
            					mensajeExito(event, "El archivo se ha descifrado correctamente. Acceda al directorio para comprobarlo.");
                    			text_descarga.setText("");
            				}
            			}
            			catch(Exception e) {
            				mensajeError(event, "La contraseña es incorrecta.");
            				errorPass = true;
            				
            			}	
        			}
        		}while(errorPass==true);       
    		}
    		else {
    			mensajeError(event, "El archivo que intentas abrir no se encuentra en el sistema o no pertenece al servidor");
    			text_descarga.setText("");
    		}
    		
    	}else {
    		mensajeErrorPath(event);
    	}
    }
    
    @FXML 
    private void handleIrConfiguracion() {
    	try {
			main.mostrarVistaConfiguracion();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML 
    private void handleIrSubirArchivos() {
    	main.mostrarVistaSubirArchivos();
    }
    
    @FXML 
    private void handleIrInicio() {
    	try {
			main.mostrarInicial();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void setMain(Main main) {
    	this.main = main;
    }
    
    private boolean existeDirectorio(String directorio) {
    	File fichero = new File(directorio);
    	if(fichero.exists()) {
    		return true;
    	}
    	return false;
    }
    
    //----------------------------------- FUNCIONES QUE MUESTRAN LAS VENTANAS DE DIALOGO -----------------------------------
    
    private void mensajeErrorPath(ActionEvent event) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	
    	Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Ventana de error");
		alert.setHeaderText("Se ha producido un error al intentar abrir el directorio");
		alert.setContentText("El directorio no puede ser vacio. Por favor, pruebe con un directorio válido.");
		alert.initOwner(stage);
		alert.showAndWait();       
    }
    
    private void mensajeExito(ActionEvent event, String mensaje) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Ventana de información");
		alert.setHeaderText("Todo ha ido correctamente");
		alert.setContentText(mensaje);
		alert.initOwner(stage);
		alert.showAndWait();       
    }
    
    private void mensajeError(ActionEvent event, String mensaje) {
    	
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	
    	Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Ventana de error");
		alert.setHeaderText("Se ha producido un error");
		alert.setContentText(mensaje);
		alert.initOwner(stage);
		alert.showAndWait();       
    }
    
    private String dialogoContrasenya(ActionEvent event) {
    	
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); //Obtenemos la ventana principal donde se origina el evento
    	
    	//Crear la ventana de dialogo customizada
    	Dialog <String> dialog = new Dialog<>();
    	dialog.setTitle("Escriba su contraseña");
    	dialog.setHeaderText("Para descifrar el archivo es necesario conocer su contraseña");
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
    
    private  boolean descifrar(ActionEvent event, String path_archivo, String psswd) throws Exception {
		//String psswd = dialogoContrasenya(event);
		
		String[] tokens = path_archivo.split("/");
		String nombre_archivo = tokens[tokens.length-1];	
		
		return FileDecrypter.decrypt(nombre_archivo, psswd);  	
    }
}