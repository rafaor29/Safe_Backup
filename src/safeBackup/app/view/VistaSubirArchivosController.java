package safeBackup.app.view;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import safeBackup.app.Main;
import safeBackup.app.model.ConfigureSetup;
import safeBackup.app.model.FileEncrypter;

public class VistaSubirArchivosController {
	
    @FXML private Button btn_subir_archivos;
    @FXML private Button btn_examinar;
    @FXML private TextField text_examinar;
    @FXML private Button btn_compartir;
    @FXML private Button btn_ir_inicio; 
    @FXML private Button btn_ir_configuracion;
    @FXML private Button btn_ir_descargas;
    @FXML private Button btn_ayuda;
    @FXML private ListView<String> lista_usuarios;
    @FXML private Hyperlink link_editar_usuarios;
    
    private String path;   
    private Main main; //esto habrá que borrarlo cuando todo funcione ok  
        
    //----------------------- Funciones  -----------------------
    
    @FXML private void initialize() { 
    	text_examinar.setDisable(true);
    	text_examinar.setStyle("-fx-opacity: 1");
    	listarUsuarios(); 
    	lista_usuarios.getSelectionModel().setSelectionMode(null);
    	lista_usuarios.setMouseTransparent( true );
    	lista_usuarios.setFocusTraversable( false );
    }
    
    //metodo para borrar cuando todo funcione ok
    public void setMain(Main main) {
    	this.main = main;
    }
    
   
    //Metodo que mostrara una ventana de dialogo en la que el usuario pueda seleccionar un archivo
    @FXML private void handleExaminar() {
    	DirectoryChooser dc = new DirectoryChooser();
    	dc.setTitle("Examinar archivos");
    	
    	
    	File dir = dc.showDialog(null);     
    	
    	if(dir != null) {
    		path = dir.getAbsolutePath().replace('\\', '/');
    		text_examinar.setText(path);
    	}
    	
    	lista_usuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	lista_usuarios.setMouseTransparent( false );
    	lista_usuarios.setFocusTraversable( true );
    }
    
      
    @FXML private void handleSubirArchivos(ActionEvent event) {
    	if(text_examinar != null && text_examinar.getText().length() != 0) {    	  		
    		String file = text_examinar.getText(); //carpeta que se desea cifrar
    		if(existeDirectorio(file)) {
    			String[] usuarios;
    	  		
        		//Comprobamos si se ha seleccionado algun usuario de la lista para compartir. En caso afirmativo obtenemos dichos usuarios
        		ObservableList<String> usuarios_seleccionados;
            	usuarios_seleccionados = lista_usuarios.getSelectionModel().getSelectedItems();
            	
            	if(!usuarios_seleccionados.isEmpty()) {
            		usuarios = new String[usuarios_seleccionados.size()+1]; //lista con los usuarios que se desea compartir
            		usuarios[0] = ConfigureSetup.getUserName();
            		int i = 1;           	
                	for(String u : usuarios_seleccionados) {
                		usuarios[i] = u;
                		i++;
                	}
            	}
            	else {
            		usuarios = new String[] { ConfigureSetup.getUserName()}; //lista con los usuarios que se desea compartir
            	}
    		
            	String inFile = file.replace('\\', '/');
            	String p = "";
            	boolean errorPass = false;
            	do {
        			p = dialogoPassword(event);
        			errorPass = false;
        			if(p!=null &&  p!="") {
            			//mensajeError(event, "Se ha producido un error al descifrar el archivo. Pruebe de nuevo.");
            			//p = dialogoContrasenya(event);
        				
        				boolean cifrado = false;
            			try {
            				cifrado = FileEncrypter.Encrypt(inFile, usuarios, p);
            				if(!cifrado) {
            					mensajeError(event, "Se ha producido un error al cifrar el archivo. Pruebe de nuevo.");
            				}
            				else {
            					mensajeExito(event, "El archivo se ha cifrado correctamente. Acceda al directorio para comprobarlo.");
            					text_examinar.setText("");
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
    			
    			mensajeError(event, "El directorio que intentas abrir no se encuentra en el sistema");
    			text_examinar.setText("");
    		}        	    		
    	}
    	else {
    		mensajeErrorPath(event);
    	}
    }
    
    //Funcion que redirige a la vista de configuracion al pulsar el boton de Configuracion
    @FXML 
    private void handleIrConfiguracion() {
    	try {
			main.mostrarVistaConfiguracion();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //Funcion que redirige a la vista de descargas al pulsar el boton de Descargas
    @FXML 
    private void handleIrDescargas() {
    	main.mostrarVistaDescargarArchivos();
    }
    
    //Funcion que redirige a la vista inicial al pulsar el boton de Inicio
    @FXML 
    private void handleIrInicio() {
    	try {
			main.mostrarInicial();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //funcion que abre la pantalla de edicion de los usuarios compartidos
    @FXML private void handleEditar(ActionEvent event) {
    	main.mostrarVistaEditarArchivosCompartidos();
    }
    
    
    // ---------------- FUNCIONES ADICIONALES -----------------------------
    
    //Metodo que lista los usuarios (exlcuyendo al usuario actual) disponibles para compartir un archivo
    private void listarUsuarios() {
    	
    	for(String usuario : ConfigureSetup.getUsers()) {
    		if(!usuario.equals(ConfigureSetup.getUserName()))
    			lista_usuarios.getItems().add(usuario);
    	}
    	
    	//lista_usuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);	
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
}
