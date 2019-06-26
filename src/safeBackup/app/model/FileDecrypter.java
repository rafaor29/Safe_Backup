package safeBackup.app.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileDecrypter {
	private static final String DIGEST_ALGORITHM = "SHA-256";
	private static final String CHARSET_NAME = "UTF-8";
	private static final int iterations = 20;
	// Vector inicializador para nuestro cifrador, siempre igual en todas las ejecuciones
	final static byte[] defaultSalt = Base64.getDecoder().decode("dRFzbpRD7ec=");
	private static final int count = 20;
	static IvParameterSpec ivParameterSpec;
	
	// Lee el archivo pasado por parámetro
	private static String readFile(String path, Charset encoding) throws IOException {
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return new String(encoded, encoding);
		  
}
	
	//desencripta la clave privada del usuario a partir de la contraseña
	public static PrivateKey decryptPrivateKey(String pass) {
		String nombreUsuario = ConfigureSetup.getUserName();
		String decriptedKey = ConfigureSetup.getBackupLocation()+nombreUsuario+"_PrivateKey.key";
		byte[] pk =null;
		try {
			pk = Passphrase.PassphraseDecrypt(pass);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try (FileOutputStream fos = new FileOutputStream(decriptedKey)) {		 
			fos.write(pk);
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File dec = new File (decriptedKey);
        byte[] decKey = new byte[(int)dec.length()];
	    try {
			new FileInputStream(dec).read(decKey);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decKey);
	    KeyFactory kf = null;
		try {
			kf = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    PrivateKey prv=null;
		try {
			prv = kf.generatePrivate(privateKeySpec);
		} catch (InvalidKeySpecException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return prv;
	}
	
	//obtiene el vector de inicialización a partir de la contraseña introducida
	public static void getIV(String password) {
	
		// initialization vector
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
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
	}
	//1 - Introducir el nombre sin ruta del archivo a descargar por parametro (Se sacarï¿½ de la interfaz)
	public static boolean decrypt(String inFile, String pass) throws Exception {
		
		String nombreUsuario = ConfigureSetup.getUserName();
		String pathDrive = ConfigureSetup.getPathDrive();	
		//Esta seria la ruta completa del archivo a descargar
		String pathFileS = (pathDrive+ConfigureSetup.fileLocation+inFile);
		String outFile = ConfigureSetup.getBackupLocation()+FileEncrypter.getFileName(inFile)+".zip";
		String decriptedKey = ConfigureSetup.getBackupLocation()+nombreUsuario+"_PrivateKey.key";

		//2 - Miramos que el archivo existe
		Path pathFile = Paths.get(pathFileS);
		if(Files.notExists(pathFile)) {
        	System.out.println("El archivo indicado no existe");  
        	return false;
        }
        else {
        	//3 - Coger la clave "nombreFichero_nombreUsuario" y descifrarlo con "nombreUsuario_PrivateKey"
        	String keyFileEncriptedPath = pathDrive+ConfigureSetup.fileKeyLocation+ConfigureSetup.getUserName()+"/"+inFile+".txt";
    		File keyFileEncripted = new File(keyFileEncriptedPath);
    		
    	
            //---------------------------------
    		
    		byte[] pk =null;
			PKCS8EncodedKeySpec privateKeySpec = null;
			KeyFactory kf = null;
		    PrivateKey prv=null;
		    SecretKey secretKey = null;

			//Conseguimos la clave privada del autor
			pk = Passphrase.PassphraseDecrypt(pass);

			try {
				
				//Descencriptamos la keyFileEncripted del autor
    			privateKeySpec = new PKCS8EncodedKeySpec(pk);
    			kf = KeyFactory.getInstance("RSA");
    			prv = kf.generatePrivate(privateKeySpec);
    			
    			//Coge la keyFile cifrada, la descifra y la carga
    			secretKey = keyFile.loadKey(keyFileEncripted, prv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();	
			}
            
        	try {
        		//Caragmos la clave descifrando la que estï¿½ en ServidorES/Claves_Archivos
        		getIV(pass);
				//Desciframos el fichero de ServidorES/Archivos_Cifrados y lo guardamos en una carpeta en Files
        		Cipher ci;
    			ci = Cipher.getInstance(FileEncrypter.cI);
				ci.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);   			
				//Creamos el fichero donde vamos a guardar la restauraciï¿½n de la copia de seguridad
    			File f = new File(outFile);
    			f.createNewFile();    			
    			//Desciframos la copia de seguridad y lo guardamos en ./Files/Backups
    			FileEncrypter.processFile(ci, pathFileS, outFile);       		
    			File borrandoPK = new File(decriptedKey);
    			if(borrandoPK.exists()==true) {
    				System.out.println(borrandoPK.getAbsolutePath());
    				borrandoPK.delete();
    			}        		
        		System.out.println("Copia de seguridad descifrada y descargada en el equipo");
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
        	return true;
        }
		
	}
	
	public static void main(String[] args) {
		//Prueba para desencriptar el fichero del Drive "Pruebas"
		try {
			String archivo_cifrado = "Pruebas_2019_05_09_Rafa";
			String pass = "asdf1234";
			decrypt(archivo_cifrado,pass);
		}catch(Exception e) {
			System.out.println(e.getStackTrace());
		}
		
 }	
	
}
