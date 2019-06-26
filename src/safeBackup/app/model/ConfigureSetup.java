package safeBackup.app.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.FileReader;

public class ConfigureSetup {

	
	//El fichero siempre se guarda en el mismo sitio
	public final static String configFolderLocation = System.getProperty("user.home")+File.separator+".safeBackup";
	public final static String configFileLocation = System.getProperty("user.home")+File.separator+".safeBackup"+File.separator+"ServerES-config.txt";
	public final static String serverLocation = "/ServidorES/";
	public final static String publicKeyLocation = "/ServidorES/Claves_Publicas/";
	public final static String privateKeyLocation = "/ServidorES/Claves_Privadas/";
	public final static String fileKeyLocation = "/ServidorES/Claves_Archivos/";
	public final static String fileLocation = "/ServidorES/Archivos_Cifrados/";
	public final static String path_archivos_cifrados = getPathDrive()+fileLocation;
	
	//Metodo que dado un nombre comprueba si hay una publicKey para un usuario con ese nombre
	public static boolean validateUserName(String userName) {
		//Para cada uno de los ficheros de ...Drive.../ServidorES/Claves_Publicas
		File[] files = new File(ConfigureSetup.getPathDrive()+publicKeyLocation).listFiles();
		for(File file : files) {
			//Quitar hasta la "_" y quedarnos con la primera parte
			String[] tokens = file.getName().split("_");
			//Si es diferente la primera parte que userName seguimos. Si no return false
			if(tokens[0].equals(userName)) {
				return false;
			}		
		}
		return true;
	}
	
	//Metodo que devuelve un array con los usuarios que han configurado el escenario en la aplicaci�n
	public static List<String> getUsers() {
		//Para cada uno de los ficheros de ...Drive.../ServidorES/Claves_Publicas
		File[] files = new File(ConfigureSetup.getPathDrive()+publicKeyLocation).listFiles();
		List<String> users = new ArrayList<String>();
		int i = 0;
		for(File file : files) {
			//Quitar hasta la "_" y quedarnos con la primera parte
			String[] tokens = file.getName().split("_");
			users.add(tokens[0]);
			i++;
		}
		return users;
	}

	//Metodos para obtener los valores del fichero de configuracion
	public static String getUserName() {
		String username="";
		File configFile = new File(configFileLocation);
    	try {
    		BufferedReader b = new BufferedReader(new FileReader(configFile));
        	for(int i=0;i<2;i++) {
        		if(i==1) {
        			username=b.readLine();
        		}
        		else{
        			b.readLine();
        		}
        	}
        	b.close();	
    	}catch(IOException e) {
    		System.out.println(e.getMessage());
    	}  	
    	return username;
	}
	public static String getPathDrive() {
		String pathPrivate="";
		File configFile = new File(configFileLocation);
    	try {
    		BufferedReader b = new BufferedReader(new FileReader(configFile));
        	for(int i=0;i<4;i++) {
        		if(i==3) {
        			pathPrivate=b.readLine();
        		}
        		else{
        			b.readLine();
        		}
        	}
        	b.close();	
    	}catch(IOException e) {
    		System.out.println(e.getMessage());
    	}  	
    	return pathPrivate;
	}
	
	public static String getBackupLocation() {
		String backupLocation = "";
		File configFile = new File(configFileLocation);
    	try {
    		BufferedReader b = new BufferedReader(new FileReader(configFile));
        	for(int i=0;i<6;i++) {
        		if(i==5) {
        			backupLocation=b.readLine();
        		}
        		else{
        			b.readLine();
        		}
        	}
        	b.close();	
    	}catch(IOException e) {
    		System.out.println(e.getMessage());
    	}  	
		return backupLocation;
	}
	
	public static String getPathPrivateKey() {
		String pathPrivateKey="";
		File configFile = new File(configFileLocation);
    	try {
    		BufferedReader b = new BufferedReader(new FileReader(configFile));
        	for(int i=0;i<6;i++) {
        		if(i==5) {
        			pathPrivateKey=b.readLine();
        		}
        		else{
        			b.readLine();
        		}
        	}
        	b.close();	
    	}catch(IOException e) {
    		System.out.println(e.getMessage());
    	}  	
    	return pathPrivateKey;
	}

	
	//Metodo que introduciendo el directorio de Drive te genera las carpetas en caso de que no existan
    public static boolean createDirectories() {
    	String pathDriveS = ConfigureSetup.getPathDrive();
    	String pathPrivateKeyS = ConfigureSetup.getPathPrivateKey();
    	Path pathDrive = Paths.get(pathDriveS);
        
        if(Files.notExists(pathDrive)) {
        	System.out.println("La ruta del directorio de Drive es incorrecta");
        	return false;
        }
        else {
        	System.out.println("Creando o actualizando la estructura de directorios");
        	File server = new File(pathDriveS+serverLocation);  
        	File fileFolder = new File(pathDriveS+fileLocation);
        	File keyFileFolder = new File(pathDriveS+fileKeyLocation);
        	File keyFileFolderUser = new File(pathDriveS+fileKeyLocation+getUserName());
        	File publicKeyFolder = new File(pathDriveS+publicKeyLocation);
        	File privateKeyFolder = new File(pathDriveS+privateKeyLocation);
        	File fileFolderBackups = new File(getBackupLocation());
        	try {
        		if(!server.exists())server.mkdir();
            	if(!fileFolder.exists())fileFolder.mkdir();
            	if(!keyFileFolder.exists()) {
            		keyFileFolder.mkdir();
            		Files.setAttribute(keyFileFolder.toPath(), "dos:hidden", true);
            	}
            	if(!keyFileFolderUser.exists())keyFileFolderUser.mkdir();
            	if(!publicKeyFolder.exists()) {
            		publicKeyFolder.mkdir();
            		Files.setAttribute(publicKeyFolder.toPath(), "dos:hidden", true);
            	}
            	if(!privateKeyFolder.exists()) {
            		privateKeyFolder.mkdir();
            		Files.setAttribute(privateKeyFolder.toPath(), "dos:hidden", true);
            	}
            	if(!fileFolderBackups.exists())fileFolderBackups.mkdir();
        	}
        	catch(IOException e) {
        		return false;
        	}
        	
        	return true;

        }   
    }
    
    //Se comprueba si existe todo lo que se crea al configurar y devuelve si se ha configurado el escenario o no
    public static boolean isConfigured() {
    	String pathDriveS = ConfigureSetup.getPathDrive();
    	
    	File configFile = new File(configFileLocation);
    	File server = new File(pathDriveS+serverLocation);  
    	File fileFolder = new File(pathDriveS+fileLocation);
    	File keyFileFolder = new File(pathDriveS+fileKeyLocation);
    	File keyFileFolderUser = new File(pathDriveS+fileKeyLocation+getUserName());
    	File publicKeyFolder = new File(pathDriveS+publicKeyLocation);
    	File privateKeyFolder = new File(pathDriveS+privateKeyLocation);
    	File fileFolderBackups = new File(getBackupLocation());
    	//Se comprueba que existan todas las carpetas
    	if(configFile.exists() && server.exists() && fileFolder.exists() && keyFileFolder.exists() && keyFileFolderUser.exists() && publicKeyFolder.exists() && privateKeyFolder.exists() && fileFolderBackups.exists()) {
    		File publicKey = new File(publicKeyFolder+"/"+getUserName()+"_PublicKey.pub");
    		File privateKey = new File(privateKeyFolder+"/"+getUserName()+"_PrivateKeyEncripted.key");
    		//Se comprueba que ademas existan las claves publica y privada
    		if(publicKey.exists() && privateKey.exists()) return true;
    		else return false;
    	}
    	else return false;    	
    }
    
    //Metodo que pregunta nombre de usuario, donde este Drive, donde guardar privateKey y almacena esa info en un fichero almacenado en Files del proyecto
    public static boolean configureInitialSetup(String userName, String drivePath, String downloadPath) {
    	File configFolder = new File(configFolderLocation);
    	try {
    		if(!configFolder.exists()) {
        		configFolder.mkdir();
        		Files.setAttribute(configFolder.toPath(), "dos:hidden", true);
        	}
    	}catch(IOException e) {
    		e.printStackTrace();
    	}
    	
    	File configFile = new File(configFileLocation);
    	if(!downloadPath.endsWith("/")) {
    		downloadPath=downloadPath+"/";
    	}

    	
    	//Escribimos en el fichero
    	try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(configFile.getAbsoluteFile()));
			writer.write("//Nombre de usuario"+"\n");
			writer.write(userName+"\n");
			writer.write("//Ubicacion del directorio de Drive"+"\n");
			writer.write(drivePath+"\n");
			writer.write("//Ubicacion d�nde se va a descargar los backups"+"\n");
			writer.write(downloadPath+"\n");
			writer.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
    	return true;
    	
    }
    
    //M�todo que genera las claves publica y privada y las escribe seg�n lo indicado en el fichero de configuraci�n
    public static boolean configureAsymmetricCripto(String password) {
    	//Primero miramos si existe ya un usuario registrado con el nombre ha introducir
    	if(validateUserName(getUserName())==true) {
    		AsymmetricCryptography gk;   		
    		gk = new AsymmetricCryptography(1024);
			gk.createKeys();
			try {	
				gk.save2(gk.getPair(), password);
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
    	}
    	else {
    		System.out.println("Se esta intentando crear un usuario que ya existe.");
    		return false;
    	}
    	
    	
    }
    
    public static void main(String[] args) {
    	//configureInitialSetup("Carlos","D:/Drive");
    	//createDirectories();
    	//configureAsymmetricCripto();//Ahora mismo si se hace el configure con un usuario ya existente se crean las carpetas si se tienen que crear, pero no se crean las claves
    	//getUsers();
    	//configureInitialSetup("Rafa","D:/Drive","D:/Descargas/ES");
    	//createDirectories();
    	//String password = "asdf1234";
    	//configureAsymmetricCripto(password);//Ahora mismo si se hace el configure con un usuario ya existente se crean las carpetas si se tienen que crear, pero no se crean las claves
    	//System.out.println("Is configured? "+isConfigured());
    
    	//System.out.println(configFileLocation);
    }
    
}