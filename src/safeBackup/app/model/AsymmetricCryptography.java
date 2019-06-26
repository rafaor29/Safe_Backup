package safeBackup.app.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class AsymmetricCryptography {

	
	
	private KeyPairGenerator keyGen;//Generador de claves
	private static KeyPair pair;
	private static PrivateKey privateKey;
	private static PublicKey publicKey;
	
	//Constructor de la clase que inicializa el generador
	public AsymmetricCryptography(int keylength){
		try {
			this.keyGen = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
		}
		this.keyGen.initialize(keylength);
	}
	
	//Metodo que crea las claves y las guarda en los atributos de la clase
	public void createKeys() {
		this.pair = this.keyGen.generateKeyPair();
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
	}

	//Getters
	public static PrivateKey getPrivateKey() {
		return privateKey;
	}
	public static PublicKey getPublicKey() {
		return publicKey;
	}
	public static KeyPair getPair() {
		return pair;
	}

	//Almacena el par de claves de un usuario, encriptando la clave privada previamente con la contraseña del usuario
	public static void save2(KeyPair kp, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, IOException {// , String pathPublicKey, String pathPrivateKey
		
		String nombreUsuario = ConfigureSetup.getUserName();
		String pathPublicKey = ConfigureSetup.getPathDrive()+ ConfigureSetup.publicKeyLocation+nombreUsuario+"_PublicKey.pub";
		String pathPrivateKey = ConfigureSetup.getBackupLocation()+nombreUsuario+"_PrivateKey.key";
		

		
		final File publicKeyFile = new File(pathPublicKey);//TODO: Esto se lee del fichero de configuraciï¿½n
	    publicKeyFile.getParentFile().mkdirs(); // make directories if they do not exist
	    final File privateKeyFile = new File(pathPrivateKey);//TODO: Esto se lee del fichero de configuraciï¿½n
	    privateKeyFile.getParentFile().mkdirs(); // make directories if they do not exist
	    
		Key pub = kp.getPublic();
		Key pvt = kp.getPrivate();
			
		try {
			// Se generan las claves sin cifrar
			FileOutputStream out; 
			out = new FileOutputStream(pathPrivateKey);
			out.write( pvt.getEncoded());
			out.close();


			out = new FileOutputStream(pathPublicKey);
			out.write(pub.getEncoded());
			out.close();
			
			
			
			//Se cifra la clave privada con el passphrase
			Passphrase.generateSalt(password);
			Passphrase.PassphraseEncrypt(password);
			
			if(privateKeyFile.exists()) {
				System.out.println(privateKeyFile.getAbsolutePath());
				privateKeyFile.delete();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		//Con esto se crean las claves y se almacenan dichas claves donde se indica en save2
			
			AsymmetricCryptography gk;
			gk = new AsymmetricCryptography(1024);
			gk.createKeys();
			try {
				gk.save2(gk.getPair(), "pass");
				System.out.println("Claves generadas y guardadas");
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			
	}
}