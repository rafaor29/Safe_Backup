package safeBackup.app.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileEncrypter {
	final static byte[] defaultSalt = Base64.getDecoder().decode("dRFzbpRD7ec=");
	private static final int count = 20;
	// Definicion del tipo de algoritmo a utilizar (AES, DES, RSA)
	final static String alg = "AES";
	// Definicion del modo de cifrado a utilizar
	final static String cI = "AES/CBC/PKCS5Padding";
	// Numero de bits de la clave de cifrado AES
	final static int sizeAES = 128;
	private static final String DIGEST_ALGORITHM = "SHA-256";
	private static final String CHARSET_NAME = "UTF-8";
	private static final int iterations = 20;
	// Vector inicializador para nuestro cifrador, siempre igual en todas las ejecuciones

	static IvParameterSpec ivParameterSpec;
	
	//Obtiene la fecha actual
	public static String getDate() {
		DateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
		Date date = new Date();
        return sdf.format(date);
		
	}
	
	// Carga la clave privada del path obtenido por parámetro
	public static SecretKey loadKey(String keyFilePath){
		System.out.println("Se esta cargando la clave de cifrado AES...");
		SecretKey keyFile = null;
		try {
    		BufferedReader b = new BufferedReader(new FileReader(keyFilePath));
    		String line = b.readLine();
    		String keyFileEncoded="";
    		while(line!=null) {
    			keyFileEncoded=keyFileEncoded+line;
    			line=b.readLine();
    		}
        	b.close();	
    		byte[] decodedKey = Base64.getDecoder().decode(keyFileEncoded);
    		keyFile = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

    	}catch(IOException e) {
    		System.out.println(e.getMessage());
    	}  
		return keyFile;
}

	// Metodo que genera una clave de cifrado y la guarda en el fichero indicado
	@SuppressWarnings("finally")
	public static Key generateKey(String inFile, String[] usuarios){
		System.out.println("Se esta generando la clave de cifrado AES...");

		Key skey = null;
		
		try {
			//Generamos una clave de cifrado para el archivo
			KeyGenerator kgen = KeyGenerator.getInstance(alg);
			kgen.init(sizeAES);
			skey = kgen.generateKey();
			
			//Para nuestro usuario
			//Donde se va a guardar la clave cifrada
			String keyFilePath = ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation+ConfigureSetup.getUserName()+"/"+getFileName(inFile)+"_"+getDate()+"_"+ConfigureSetup.getUserName()+".txt";
			//Ciframos y escribimos la clave en su fichero correspondiente
			writeKey(skey, keyFilePath, ConfigureSetup.getUserName());
			
			//Para cada usuario con el que queremos compartir la clave vamos a escribir la clave cifrada con su clave publica
			for(int i=0; i<usuarios.length;i++) {
				if(ConfigureSetup.validateUserName(usuarios[i])==false) {
					//Donde se va a guardar la clave cifrada
					keyFilePath = ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation+usuarios[i]+"/"+getFileName(inFile)+"_"+getDate()+"_"+ConfigureSetup.getUserName()+".txt";
					//Ciframos y escribimos la clave en su fichero correspondiente
					writeKey(skey, keyFilePath, usuarios[i]);
				}
				else {
					System.out.println("Estas intentando compartir un fichero con un usuario que no existe");
				}
				
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			return skey;
		}

	}

	// Metodo que escribe la clave de cifrado en el fichero indicado
	public static void writeKey(Key skey, String fileName, String userName){
		File file = new File(ConfigureSetup.getBackupLocation()+getFileName(fileName));//La clave sin cifrar se guarda en local
		try (BufferedWriter out = new BufferedWriter(new FileWriter (file))) {
			if (!file.exists()) {
				file.createNewFile();
			}
			String encodedKey = Base64.getEncoder().encodeToString(skey.getEncoded());
			out.write(encodedKey);
			out.close();
			File outFile = new File(fileName);//Donde se va a escribir la clave cifrada
			File clavePublica = new File(ConfigureSetup.getPathDrive()+ConfigureSetup.publicKeyLocation+userName+"_PublicKey.pub");

			//Guardamos la clave cifrada en el Drive
			keyFile.saveKey(outFile,clavePublica, ConfigureSetup.getBackupLocation()+getFileName(fileName));
			
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		System.out.println("Clave AES generada y almacenada en el servidor");
	}

	// Mtodo que coge inFile, lo lee y escribe en outFile el resultado del Cipher
	public static void processFile(Cipher ci, String inFile, String outFile) {

		try {
			FileInputStream in = new FileInputStream(inFile); // fichero que se va a encriptar
			FileOutputStream out = new FileOutputStream(outFile); // fichero donde se guarda la encriptacion

			byte[] ibuf = new byte[1024]; // lee 1024 bytes y lo "mete" en el fichero
			int len;
			while ((len = in.read(ibuf)) != -1) { // le
				byte[] obuf = ci.update(ibuf, 0, len);
				if (obuf != null)
					out.write(obuf);
			}
			byte[] obuf = ci.doFinal(); // obuf = resultado cifrado
			if (obuf != null)
				out.write(obuf); // escribe obuf en el fichero outFile

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}

	// Mtodo para encriptar texto o ficheros
	public static boolean Encrypt(String inFile, String[] usuarios, String password) throws Exception {
		System.out.println("Se va a proceder al cifrado de la copia de seguridad...");
		
		//Directorio a cifrar y guardar
		String[] inFileArray = {inFile};
		//Donde se va a guardar el fichero cifrado
		String outFile = ConfigureSetup.getPathDrive()+ConfigureSetup.fileLocation+getFileName(inFile)+"_"+getDate()+"_"+ConfigureSetup.getUserName();
		//Donde se va a guardar la version provisional sin cifrar -> Se borra al acabar
		String outFileProvisional = ConfigureSetup.getBackupLocation()+getFileName(inFile)+".zip";
		String[] outFileArray = {outFileProvisional};

		//Vamos a comprimir el directorio a cifrar y lo guardamos en una ubicaciï¿½n provisional para cifrarlo
		Zipper.comprimir(inFileArray, outFileArray);
		
		Cipher ci = null;
		
		//Se va a generar una clave y se va a guardar cifrada con la clave publica de cada usuario
		Key secretKey = generateKey(inFile, usuarios);		
		
		String username = ConfigureSetup.getUserName();
		String pathSalt = ConfigureSetup.getPathDrive() + ConfigureSetup.privateKeyLocation+username+"_salt.txt";
		File file = new File(pathSalt);
		byte[] salFile = null;
		byte[] salt = null;
		MessageDigest shaDigest = null;
		byte[] pw = null;
		
		try {
			salFile = Files.readAllBytes(file.toPath());
			salt = Passphrase.decrypt(defaultSalt, count, password, salFile);
			shaDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
			pw = password.getBytes(CHARSET_NAME);
			
			
		}catch(Exception e) {
			File keyFile = null;
			for(String user : usuarios) {
				keyFile = new File(ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation+user+"/"+getFileName(inFile)+".txt");
				if(keyFile.exists()) keyFile.delete();
			}
			keyFile = new File(ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation+ConfigureSetup.getUserName()+"/"+getFileName(inFile)+"_"+getDate()+"_"+ConfigureSetup.getUserName()+".txt");
			if(keyFile.exists()) keyFile.delete();
			
			File file2 = new File(outFileArray[0]);
			System.out.println(file2.getPath());
			file2.delete();
			throw e;
		}
		

        for (int i = 0; i < iterations; i++)
        {
            // add salt 
            final byte[] salted = new byte[pw.length + salt.length];
            System.arraycopy(pw, 0, salted, 0, pw.length);
            System.arraycopy(salt, 0, salted, pw.length, salt.length);
            Arrays.fill(pw, (byte) 0x00);

            /* compute SHA-256 digest */
            shaDigest.reset();
            pw = shaDigest.digest(salted);
            Arrays.fill(salted, (byte) 0x00);
        }

        /* extract the 16-byte key and initialization vector from the SHA-256 digest */
        final byte[] key = new byte[16];
        final byte[] iv = new byte[16];
        System.arraycopy(pw, 0, key, 0, 16);
        System.arraycopy(pw, 16, iv, 0, 16);
        Arrays.fill(pw, (byte) 0x00);
        ivParameterSpec = new IvParameterSpec(iv);
		//---------
		try {
			ci = Cipher.getInstance(cI);
			ci.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchAlgorithmException e) {
			e.getMessage();
			return false;
		} catch (NoSuchPaddingException e) {
			e.getMessage();
			return false;
		}
		
		//Escribimos el zip cifrado en un archivo en el directorio de Drive
		processFile(ci, outFileArray[0], outFile);
		
		//Borramos la version provisional sin cifrar
		File file2 = new File(outFileArray[0]);
		file2.delete();
		return true;
	}
	
	//Obtiene el nombre del archivo
	public static String getFileName(String path) {
		String[] tokens = path.split("/");
		String x = tokens[tokens.length-1];		
		return x;
		
	}
	
	//Devuelve los usuarios que tienen acceso a fileS. Vacï¿½o si no hay usuarios con acceso. "_Error" si el archivo no es de ServidorES
	public static List<String> usersWithAccess(String fileS) {
	
		List<String> usuarios = new ArrayList<String>();
		String nameFile = FileEncrypter.getFileName(fileS);
		File file = new File(fileS);
		if(fileS.equals(ConfigureSetup.getPathDrive()+ConfigureSetup.fileLocation+nameFile) && file.exists()) {
			File keyFolder = new File(ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation);
			File[] usersFiles = keyFolder.listFiles();
			for(File f:usersFiles) {
				File x = new File(f.toString()+"\\"+nameFile+".txt");
				if(x.exists()) {
					usuarios.add(x.getParentFile().getName().toString());
				}			
			}
		}
		else {
			usuarios.add("_Error");
		}

		return usuarios;
	}
	
	//Devuelve los usuarios que no tienen acceso a fileS. Vacï¿½o si no hay usuarios sin acceso. "_Error" si el archivo no es de ServidorES
	public static List<String> usersWithoutAccess(String fileS) {
	
		List<String> usuarios = new ArrayList<String>();
		String nameFile = FileEncrypter.getFileName(fileS);
		File file = new File(fileS);
		System.out.println(ConfigureSetup.getPathDrive());
		System.out.println(fileS);
		if(fileS.equals(ConfigureSetup.getPathDrive()+ConfigureSetup.fileLocation+nameFile) && file.exists()) {
			File keyFolder = new File(ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation);
			File[] usersFiles = keyFolder.listFiles();
			for(File f:usersFiles) {
				File x = new File(f.toString()+"\\"+nameFile+".txt");
				if(!x.exists()) {
					usuarios.add(x.getParentFile().getName().toString());
				}			
			}
		}
		else {
			usuarios.add("_Error");
		}

		return usuarios;
	}
	
	
	
	//Metodo que le quita los permisos de acceder a un archivo a uno o varios usuarios. Si ningun usuario puede acceder se borra la copia
	public static boolean removePermission(String fileS, List<String> usuarios) {
		File file = new File(fileS);
		String nameFile = FileEncrypter.getFileName(fileS);
		List<String> usuariosAutorizados = usersWithAccess(fileS);
		//Solo se pueden borrar tus archivos
		if(!nameFile.contains("_"+ConfigureSetup.getUserName())) {
			System.out.println("El archivo no es tuyo");
			return false;
		}
		else if(usuariosAutorizados.size() == 1 && usuariosAutorizados.get(0).equals("_Error")) {//Se comprueba que el archivo pertenece al ServidorES
			System.out.println("El archivo no pertenece a ServidorES");
			return false;
		}
		else {
			//Para cada usuario vamos a borrar su clave de archivo
			//Si se va a borrar el autor borramos todos, si no lo que hacï¿½amos antes
			if(usuarios.contains(ConfigureSetup.getUserName())) {//Borramos todos los usuarios y el fichero
				usuarios = ConfigureSetup.getUsers();
				for(String user : usuarios) {
					File keyFile = new File(ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation+user+"/"+nameFile+".txt");
					if(keyFile.exists()) keyFile.delete();
				}
				file.delete();
				return true;
				
			}
			else {//Borramos usuario a usuario
				for(String user : usuarios) {
					File keyFile = new File(ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation+user+"/"+nameFile+".txt");
					if(keyFile.exists()) keyFile.delete();
				}
				return true;
			}
		}
	}
	
	//Metodo que le otorga permisos de acceder a un archivo a uno o varios usuarios
		public static boolean addPermission(String fileS, List<String> usuarios, String pass) throws Exception {
			File file = new File(fileS);
			String nameFile = FileEncrypter.getFileName(fileS);
			List<String> usuariosAutorizados = usersWithAccess(fileS);
			//Solo se pueden editar tus archivos
			if(!nameFile.contains("_"+ConfigureSetup.getUserName())) {
				System.out.println("El archivo no es tuyo");
				return false;
			}
			else if(usuariosAutorizados.size() == 1 && usuariosAutorizados.get(0).equals("_Error")) {//Se comprueba que el archivo pertenece al ServidorES
				System.out.println("El archivo no pertenece a ServidorES");
				return false;
			}
			else {	
				byte[] pk =null;
				PKCS8EncodedKeySpec privateKeySpec = null;
				KeyFactory kf = null;
			    PrivateKey prv=null;
			    SecretKey secretKey = null;

				//Conseguimos la clave privada del autor
				pk = Passphrase.PassphraseDecrypt(pass);
				
				//Descencriptamos la keyFileEncripted del autor
    			privateKeySpec = new PKCS8EncodedKeySpec(pk);
    			kf = KeyFactory.getInstance("RSA");
    			prv = kf.generatePrivate(privateKeySpec);
    			
    			//Coge la keyFile cifrada, la descifra y la carga
    			File keyFileEncripted = new File(ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation+ConfigureSetup.getUserName()+"/"+nameFile+".txt");
    			secretKey = keyFile.loadKey(keyFileEncripted, prv);

				for(String user : usuarios) {
					//Se comprueba si el usuario no tiene todavï¿½a acceso
					if(!usuariosAutorizados.contains(user)) {
						//TODO: Se le otorga acceso
						//Donde se va a guardar la clave cifrada
						String keyFilePath = ConfigureSetup.getPathDrive()+ConfigureSetup.fileKeyLocation+user+"/"+getFileName(fileS)+".txt";
						System.out.println(keyFilePath);
						//Ciframos y escribimos la clave en su fichero correspondiente
						writeKey(secretKey, keyFilePath, user);
					}
				}
				return true;
			}
		}

	// Main de ejemplo, esto cada uno lo puede tocar como quiera
	public static void main(String[] args){
		try {
			String inFile = "D:/Descargas/Pruebas";// Se indica la carpeta que se desea cifrar																		
			String[] usuarios = {""};// Se pasa por parametro un Array con los usuarios con los que se desea compartir
			Encrypt(inFile, usuarios, "asdf1234");
			
			//List<String> usuarios = new ArrayList<String>();
			//usuarios.add("Rafa");
			//removePermission("D:/Drive/ServidorES/Archivos_Cifrado/Pruebas_2019_05_07_Carlos",usuarios);
			
			//List<String> usuarios = new ArrayList<String>();
			//usuarios.add("Angela");
			//addPermission("D:/Drive/ServidorES/Archivos_Cifrado/Pruebas_2019_05_07_Carlos",usuarios,"pass");
			//removePermission("D:/Drive/ServidorES/Archivos_Cifrado/Pruebas_Carlos",usuarios);

			
			//String[] usuarios = {"Rafa"};
			//String file = "D:/Drive/ServidorES/Archivos_Cifrados/Pruebas_Rafa";
			//List<String> usuariosList = usersWithoutAccess(file);
			//System.out.println(usuariosList);
			//System.out.println("removePermission? "+removePermission("D:/Drive/ServidorES/Archivos_Cifrados/Prueba1_Antonio", usuarios));

			//String s = getDate();
		}catch(Exception e) {
			System.out.println(e.getStackTrace());
		}
		


	}
}