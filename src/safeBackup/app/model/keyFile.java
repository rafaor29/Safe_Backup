package safeBackup.app.model;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

//TODO: Todos los m�todos que no valgan fuera
public class keyFile {
	//AsymmetricCryptography
	
	
	// Cifra la keyFile con la Clave Publica y la guarda en el Drive
	 public static void saveKey(File out, File publicKeyFile, String aeskey) throws IOException, GeneralSecurityException {
		    // read public key to be used to encrypt the AES key
		    byte[] encodedKey = new byte[(int)publicKeyFile.length()];
		    new FileInputStream(publicKeyFile).read(encodedKey);
		    
		    // create public key - la coge del archivo y crea una a partir de ella para poder usarla en el codigo
		    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
		    KeyFactory kf = KeyFactory.getInstance("RSA");
		    PublicKey pk = kf.generatePublic(publicKeySpec);
		    
		    // write AES key
		    Cipher pkCipher;
		    pkCipher = Cipher.getInstance("RSA");
		    pkCipher.init(Cipher.ENCRYPT_MODE, pk);
		    CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), pkCipher);
		    os.write(FileEncrypter.loadKey(aeskey).getEncoded());
		    os.close();
	 }
 
	 //Carga la keyFile encriptada y la descifra con la Clave privada
	 public static SecretKey loadKey(File in, PrivateKey pk) throws GeneralSecurityException, IOException {
		   
		    // read AES key
		    Cipher pkCipher;
		    pkCipher = Cipher.getInstance("RSA");
		    pkCipher.init(Cipher.DECRYPT_MODE, pk);
		    byte[] aesKey = new byte[FileEncrypter.sizeAES/8];
	        
		    CipherInputStream is = new CipherInputStream(new FileInputStream(in), pkCipher);
		    is.read(aesKey);
		    SecretKeySpec aeskeySpec = new SecretKeySpec(aesKey, "AES");
		    is.close();
	
		    return aeskeySpec;
	} 
	 


}