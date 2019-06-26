package safeBackup.app.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import safeBackup.app.Main;
import safeBackup.app.model.ConfigureSetup;

public class VistaConfiguracionController {
	
	@FXML private TextField nombre_usuario;
    @FXML private TextField directorio_drive;
    @FXML private TextField directorio_descargas;
    @FXML private PasswordField contrasenya;
    @FXML private PasswordField contrasenya_rep;
    @FXML private Button btn_config;
    @FXML private Button btn_inicio;
      
    private Main main; //Referencia a la aplicacion principal
    ArrayList<String> datos_configuracion;
    
    //----------------------------------- FUNCIONES -----------------------------------
 
    public VistaConfiguracionController() {}
    
    /**
     * Este metodo se llama de forma automatica cuando se ha cargado
     * el archivo fxml.
     * Inicializa la clase del controlador
     */
    @FXML private void initialize() {
    	mostrarInformacion();
    }
    
    /**
     * Metodo que sera invocado desde la clase Main. Con esto podemos acceder al objeto Main
     * @param main
     */
    public void setMain(Main main) {
    	this.main = main;
    }
    
   
    /**
     * Metodo que se llama cuando el usuario pulsa el boton de continuar
     */
    @FXML private void handleConfigurar(ActionEvent event) {
    	String mensajeError = validarCampos();
    	if(mensajeError.length() == 0) {    		
    		if(coincidenContrasenyas(contrasenya.getText(), contrasenya_rep.getText())) {
    			//System.out.println(nombre_usuario.getText());
    			/*if(nombreUsuarioRepetido(nombre_usuario.getText())) {
    				//confirmacionNombresRepetidos(event);
    				ConfigureSetup.configureInitialSetup(nombre_usuario.getText(),directorio_drive.getText(), directorio_descargas.getText());
    		    	ConfigureSetup.createDirectories();
    			}
    			else {*/
    				datos_configuracion = new ArrayList<String>();
            		datos_configuracion.add(nombre_usuario.getText());        		
            		datos_configuracion.add(directorio_drive.getText().replace('\\', '/'));
            		datos_configuracion.add(directorio_descargas.getText().replace('\\', '/'));
            		datos_configuracion.add(contrasenya.getText());  
            		
            		//main.mostrarDialogoAlertaConfiguracion();            		
            		confirmacionCambios(event); //muestra el diagolo de confirmacion de los cambios        		
    			//}
   		}
    		else {
    			errorContrasenya(event); //muestra un dialogo de error si las contrasenyas no coinciden
    		}		
    	}
    	else {  	
    		warningCampos(event, mensajeError); //llamada al mï¿½todo que muestra el dialogo de warning sobre los campos validados
    	}
    }
    
    @FXML private void handleInicio() {
    	try {
			main.mostrarInicial();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML
    private void mostrarInformacion() {
    	File file = new File(ConfigureSetup.configFileLocation);
    	
    	if(file.exists()) {
    		String nombre = ConfigureSetup.getUserName();
    		String path = ConfigureSetup.getPathDrive();
    		String path_descargas = ConfigureSetup.getBackupLocation();
    		
    		if( nombre != "" && nombre != null) {
    			nombre_usuario.setText(nombre);
    		}
    		if(path != "" && path != null) {
    			directorio_drive.setText(path);
    		}
    		if(path_descargas != "" && path_descargas != null) {
    			directorio_descargas.setText(path_descargas);
    		}
    	}
    }
    
    //----------------------------------- FUNCIONES PARA VALIDAR CAMPOS -----------------------------------
    
    /**
     * Comprueba si los textfield de la vista de configuracion son validos
     * @return true si todos los textfield son validos, false en cualquier otro caso
     */
    private String validarCampos() {
    	String mensajeError = "";	
 	
    	if(esVacio(nombre_usuario)) {
    		mensajeError += "- El nombre de usuario está vacío\n"; 		
    	}
    	if(!esAlfanumerico(nombre_usuario)) {
    		mensajeError += "- El nombre de usuario no es válido. Solo puede contener letras y números\n";  
    	}
    	
    	if(esVacio(directorio_drive) || soloNumeros(directorio_drive)) {
    		mensajeError += "- El directorio de Google Drive no es válido o está vacío \n";	
    	}
    	else if(!existeDirectorio(directorio_drive.getText())) {
    		mensajeError += "- El directorio de Google Drive no existe \n";
    	}
    	
    	if(esVacio(directorio_descargas) || soloNumeros(directorio_descargas)) {
    		mensajeError += "- El directorio donde se almacenarán los archivos descargados no es válida o está vacío \n";	
    	}
    	else if(!existeDirectorio(directorio_descargas.getText())) {
    		mensajeError += "- El directorio donde intentas almacenar las descargas no existe \n";
    	}
    		
    	if(esVacio(contrasenya)) {
    		mensajeError += "- El campo de la contraseña está vacío o no es una contraseñaa válida \n";
    	}
    	else if(!validarContrasenya(contrasenya.getText())) {  		
    		mensajeError += "- La contraseña debe tener mínimo 8 caracteres \n";
    	}
    	
    	if(esVacio(contrasenya_rep)) {
    		mensajeError += "- Debe repetir la contraseña para comprobar si es correcta \n";
    	}
    
    	return mensajeError;
    }
    
    /**
     * Comprueba si el campo pasado por parametro es null o solo es un espacio
     * @param textfield
     * @return true si es null o un espacio, false si tiene texto
     */
    private boolean esVacio(TextField textfield) {
    	if(textfield.getText() == null || textfield.getText().trim().length()==0 || textfield.getText().charAt(0) == ' ' || textfield.getText().matches("([ ]{8,})")) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Comprueba si la cadena introducida en el text field pasado por parï¿½metro contiene solo numeros
     * @return true si la cadena es numerica, falso en cualquier otro caso
     */
    private boolean soloNumeros(TextField textfield) {   	
    	if(textfield.getText().matches("-?([0-9]*)?")) {
    		return true;
    	}
    	return false;
    }
    
    private boolean esAlfanumerico(TextField textfield) {
    	if(textfield.getText().matches("^[a-zA-Z0-9]*$")) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Comprueba si existe el directorio pasado como parametro en el sistema
     * @param directorio
     * @return true si existe el directorio, false en cualquier otro caso
     */
    private boolean existeDirectorio(String directorio) {
    	File fichero = new File(directorio);
    	if(fichero.exists()) {
    		return true;
    	}
    	return false;
    }
    
     
    /**
     * Mï¿½todo que compara las contraseï¿½as introducidas en el formulario 
     * @param psswd
     * @param psswd_rep
     * @return true si ambas coinciden, false en cualquier otro caso
     */
    private boolean coincidenContrasenyas(String psswd, String psswd_rep) {
    	if(psswd.contentEquals(psswd_rep)) {
    		return true;
    	}
    	return false;
    }
    //Valida si el numero de caracteres es mayor/igual que ocho
    private boolean validarContrasenya(String c) {
    	if(c.length() < 8) {
    		return false;
    	}
    	return true;
    }
    
    private boolean usuarioRepetido(String usuario) {
    	//Comprobamos si existe la carpeta Claves_Publicas, eso es que la app ya se ha configurado
    	File file = new File(ConfigureSetup.getPathDrive()+ConfigureSetup.publicKeyLocation);
    	if(file.exists()) {
    		List<String> users = ConfigureSetup.getUsers();
    		for(String user : users) {
        		if(usuario.equals(user)) {
        			return true;
        		}
        	}
    	}
    	
    	return false;
    }
    
    
    //----------------------------------- FUNCIONES QUE MUESTRAN LAS VENTANAS DE DIALOGO -----------------------------------
    
    private void warningCampos(ActionEvent event, String mensajeError) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText("Se ha producido un fallo en la configuración");
		//alert.setContentText("Hay campos vacï¿½os o valores no vï¿½lidos en el formulario. Compruebe que todo estï¿½ correcto.");
		alert.setContentText(mensajeError+ "\nCompruebe que todos los campos se han rellenado correctamente");
		alert.initOwner(stage);
		alert.showAndWait();
	}
    
    private void errorContrasenya(ActionEvent event) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	
    	Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Ventana de error");
		alert.setHeaderText("La contraseña no coincide");
		alert.setContentText("Por favor, compruebe que las contraseñas coincidan");
		alert.initOwner(stage);
		alert.showAndWait();
    }
    
    private void confirmacionCambios(ActionEvent event) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Ventana de confirmación");
		alert.setHeaderText("Todos los campos son correctos");
		alert.setContentText("Pulsa aceptar para guardar los cambios.");

		alert.initOwner(stage);
		
		//Creamos los botones personalizados para que haga lo que nosotros queramos
		ButtonType btn_alert_aceptar = new ButtonType("Aceptar", ButtonData.OK_DONE);
		ButtonType btn_alert_cancelar = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(btn_alert_aceptar, btn_alert_cancelar);
		      		
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == btn_alert_aceptar) {
			
			String usuario = datos_configuracion.get(0);
			String path_drive = datos_configuracion.get(1);
			String path_descargas = datos_configuracion.get(2);
			String password = datos_configuracion.get(3);
			
			
			//guardamos los cambios en la aplicaciï¿½n - Se llama a los mï¿½todos del main de ConfigureInitSetUp			
			ConfigureSetup.configureInitialSetup(usuario,path_drive, path_descargas);
	    	ConfigureSetup.createDirectories();
	    	
	    	if(!usuarioRepetido(usuario)) {
	    		ConfigureSetup.configureAsymmetricCripto(password);//Ahora mismo si se hace el configure con un usuario ya existente se crean las carpetas si se tienen que crear, pero no se crean las claves
			}
	    	else {
	    		mensajeInformativo(event, "Solo se han modificado los directorios configurados previamente. La contraseña sigue siendo la que utilizabas antes.");
	    	}
	    	
	    	main.mostrarVistaSubirArchivos();
		}
		else {
			//No deben guardarse los cambios en la aplicaciï¿½n
		}
    }
    
    private void mensajeInformativo(ActionEvent event, String mensaje) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Ventana de confirmacion");
		alert.setHeaderText("Informació actualizada.");
		alert.setContentText(mensaje);
		alert.initOwner(stage);
		
		alert.showAndWait();
    	
    }
    
   /* private void confirmacionNombresRepetidos(ActionEvent event) {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Ventana de confirmación");
		alert.setHeaderText("Nombre de usuario duplicado");
		alert.setContentText("El nombre de usuario ya está en uso, utilice otro nombre de usuario.");
		alert.initOwner(stage);
		
		//Creamos los botones personalizados para que haga lo que nosotros queramos
		ButtonType btn_alert_aceptar = new ButtonType("Aceptar", ButtonData.OK_DONE);
		ButtonType btn_alert_cancelar = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(btn_alert_aceptar, btn_alert_cancelar);
		
		nombre_usuario.setText("");
		      		
		alert.showAndWait();
    }*/
    
    @FXML private void handleDirectorioDrive(MouseEvent event) {
    	DirectoryChooser dc = new DirectoryChooser();
    	dc.setTitle("Examinar archivos");  	
    	File dir = dc.showDialog(null);     
    	
    	if(dir != null) {
    		String path = dir.getAbsolutePath().replace('\\', '/');
    		directorio_drive.setText(path);
    	}   	
    }
    
    @FXML private void handleDirectorioDescargas(MouseEvent event) {
    	DirectoryChooser dc = new DirectoryChooser();
    	dc.setTitle("Examinar archivos");  	
    	File dir = dc.showDialog(null);     
    	
    	if(dir != null) {
    		String path = dir.getAbsolutePath().replace('\\', '/');
    		directorio_descargas.setText(path);
    	}   	
    }
    

}
