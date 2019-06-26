package safeBackup.app;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import safeBackup.app.model.FileEncrypter;
import safeBackup.app.view.DialogoAgregarUsuarioController;
import safeBackup.app.view.DialogoBorrarUsuarioController;
import safeBackup.app.view.VistaConfiguracionController;
import safeBackup.app.view.VistaDescargarArchivosController;
import safeBackup.app.view.VistaEditarArchivosCompartidosController;
import safeBackup.app.view.VistaInicialController;
import safeBackup.app.view.VistaSubirArchivosController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	
	private Stage primaryStage;
	private BorderPane inicial;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("acr-safe backup");
		this.primaryStage.getIcons().add(new Image("/img/icon-app.png"));
		mostrarInicial();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Stage getPrimaryStage() {
		return this.getPrimaryStage();
	}
		
	public void mostrarInicial() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/VistaInicial.fxml"));
		inicial = (BorderPane) loader.load();
				
		Scene scene = new Scene(inicial);
		primaryStage.setScene(scene);		
		primaryStage.show();
		
		VistaInicialController controller = loader.getController();
		controller.setMain(this);
	}
	
	public void mostrarVistaConfiguracion() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/VistaConfiguracion.fxml"));
		AnchorPane configurationLayout = (AnchorPane) loader.load();
	
		Scene scene = new Scene(configurationLayout);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		VistaConfiguracionController controller = loader.getController();
		controller.setMain(this);
	}
	
	public void mostrarVistaSubirArchivos() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/VistaSubirArchivos.fxml"));
			BorderPane subirarchivosLayout = (BorderPane) loader.load();
		
			Scene scene = new Scene(subirarchivosLayout);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			
			VistaSubirArchivosController controller = loader.getController();
			controller.setMain(this);
		} catch(IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void mostrarVistaDescargarArchivos() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/VistaDescargarArchivos.fxml"));
			BorderPane descargararchivosLayout = (BorderPane) loader.load();
		
			Scene scene = new Scene(descargararchivosLayout);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);	
			primaryStage.show();
			
			VistaDescargarArchivosController controller = loader.getController();
			controller.setMain(this);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void mostrarVistaEditarArchivosCompartidos() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/VistaEditarArchivosCompartidos.fxml"));
			AnchorPane descargararchivosLayout = (AnchorPane) loader.load();
		
			Scene scene = new Scene(descargararchivosLayout);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);	
			primaryStage.show();
			
			VistaEditarArchivosCompartidosController controller = loader.getController();
			controller.setMain(this);
		
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//------------------------------------------------------- FUNCIONES QUE MUESTRAN DIALOGOS ELABORADOS
	
	public void mostrarDialogoAgregarUsuario(String directorio) {
		//Se tiene que llamar al metodo que devuelve los usuarios con los que no se comparte
		ArrayList<String> usuarios = (ArrayList<String>) FileEncrypter.usersWithoutAccess(directorio);
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/DialogoAgregarUsuario.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			Stage dialogo = new Stage();
        	dialogo.setTitle("Añadir usuarios");
        	dialogo.getIcons().add(new Image("/img/icon-app.png"));
        	dialogo.initModality(Modality.WINDOW_MODAL);
        	dialogo.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogo.setScene(scene);
	        	        
	        DialogoAgregarUsuarioController controller = loader.getController();
	        controller.setDirectorio(directorio);
	        controller.setStage(dialogo);
	        controller.initData(usuarios);
	        
	        dialogo.showAndWait();
       
	        this.mostrarVistaEditarDatos(directorio);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void mostrarDialogoBorrarUsuarios(String directorio) {
		ArrayList<String> usuarios = (ArrayList<String>) FileEncrypter.usersWithAccess(directorio);
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/DialogoBorrarUsuario.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			
			Stage dialogo = new Stage();
        	dialogo.setTitle("Eliminar usuarios");
        	dialogo.getIcons().add(new Image("/img/icon-app.png"));
        	dialogo.initModality(Modality.WINDOW_MODAL);
        	dialogo.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogo.setScene(scene);
	        	        
	        DialogoBorrarUsuarioController controller = loader.getController();
	        controller.setDirectorio(directorio);
	        controller.setStage(dialogo);
	        controller.initData(usuarios);
	        
	        dialogo.showAndWait();
	        
	        String directorio_recuperado = controller.getDirectorio();
	        
	        if(directorio_recuperado != null && directorio_recuperado != "") {
	        	this.mostrarVistaEditarDatos(directorio_recuperado);
	        }
	        else {
	        	this.mostrarVistaEditarArchivosCompartidos();
	        }
	        	
	        
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void mostrarVistaEditarDatos(String directorio) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/VistaEditarArchivosCompartidos.fxml"));
			AnchorPane descargararchivosLayout = (AnchorPane) loader.load();
		
			Scene scene = new Scene(descargararchivosLayout);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);	
			primaryStage.show();
			
			VistaEditarArchivosCompartidosController controller = loader.getController();			
			controller.setMain(this);
			controller.initData(directorio);			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
