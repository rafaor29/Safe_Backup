package safeBackup.app.model;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//import com.sun.tools.javac.comp.Todo;

public class Passphrase {
	/** name of the character set to use for converting between characters and bytes */
    private static final String CHARSET_NAME = "UTF-8";

    /** random number generator algorithm */
    private static final String RNG_ALGORITHM = "SHA1PRNG";

    /** message digest algorithm (must be sufficiently long to provide the key and initialization vector) */
    private static final String DIGEST_ALGORITHM = "SHA-256";

    /** key algorithm (must be compatible with CIPHER_ALGORITHM) */
    private static final String KEY_ALGORITHM = "AES";

    /** cipher algorithm (must be compatible with KEY_ALGORITHM) */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    
    private static final int count = 20;
    
    public static  byte[] cleartext = null;// esto es solo para comprobar
    
    public static  byte[] desencriptao = null;//comprobacion
    
    final static byte[] defaultSalt = Base64.getDecoder().decode("dRFzbpRD7ec="); //sal por defecto para todos los usuarios con la que se cifra la salt de cada usuario
    
    //Metodo para leer archivos
    private static String readFile(String path, Charset encoding) throws IOException {
		  byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return new String(encoded, encoding);
    		  
    }
    
    //Metodo que hashea el contenido del path pasado como parï¿½metro con la contraseï¿½a del usuario y un parï¿½metro de sal aleatorio
	public static byte[] encrypt(final byte[] salt, final int iterations, final String password, String username, String path) throws Exception {
		
		//Reading of the file with unencripted PrivateKey
		String pathPrivateKey = ConfigureSetup.getBackupLocation()+username+"_PrivateKey.key";
		
        File plana = new File (pathPrivateKey);
        byte[] cleartext = new byte[(int)plana.length()];
	    FileInputStream fileInputStream = new FileInputStream(plana);
		fileInputStream.read(cleartext);
		fileInputStream.close();
	    
        
        // compute key and initialization vector 
        final MessageDigest shaDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        byte[] pw = password.getBytes(CHARSET_NAME);

        for (int i = 0; i < iterations; i++)
        {
            // add salt 
            final byte[] salted = new byte[pw.length + salt.length];
            System.arraycopy(pw, 0, salted, 0, pw.length);
            System.arraycopy(salt, 0, salted, pw.length, salt.length);
            Arrays.fill(pw, (byte) 0x00);

            // compute SHA-256 digest 
            shaDigest.reset();
            pw = shaDigest.digest(salted);
            Arrays.fill(salted, (byte) 0x00);
        }

        // extract the 16-byte key and initialization vector from the SHA-256 digest
        final byte[] key = new byte[16];
        final byte[] iv = new byte[16];
        System.arraycopy(pw, 0, key, 0, 16);
        System.arraycopy(pw, 16, iv, 0, 16);
        Arrays.fill(pw, (byte) 0x00);

        // perform AES-128 encryption
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

        cipher.init(
                Cipher.ENCRYPT_MODE,
                new SecretKeySpec(key, KEY_ALGORITHM),
                new IvParameterSpec(iv));

        Arrays.fill(key, (byte) 0x00);
        Arrays.fill(iv, (byte) 0x00);

        byte[] encriptado = cipher.doFinal(cleartext);
        
        try (FileOutputStream stream = new FileOutputStream(path)) {
            stream.write(encriptado);
        }
	    return cipher.doFinal(cleartext);
    }

	
	//Metodo para desencriptar los archivos hasheados por el mï¿½todo encrypt
	public static byte[] decrypt(final byte[] salt, final int iterations, final String password, final byte[] ciphertext) throws Exception {
        
		// compute key and initialization vector 
        final MessageDigest shaDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
        byte[] pw = password.getBytes(CHARSET_NAME);

        for (int i = 0; i < iterations; i++)
        {
            // add salt
            final byte[] salted = new byte[pw.length + salt.length];
            System.arraycopy(pw, 0, salted, 0, pw.length);
            System.arraycopy(salt, 0, salted, pw.length, salt.length);
            Arrays.fill(pw, (byte) 0x00);

            // compute SHA-256 digest 
            shaDigest.reset();
            pw = shaDigest.digest(salted);
            Arrays.fill(salted, (byte) 0x00);
        }

        /* extract the 16-byte key and initialization vector from the SHA-256 digest */
        final byte[] key = new byte[16];
        final byte[] iv = new byte[16];
        System.out.println("tr: " + iv);
        System.arraycopy(pw, 0, key, 0, 16);
        System.arraycopy(pw, 16, iv, 0, 16);
        Arrays.fill(pw, (byte) 0x00);
        /* perform AES-128 decryption */
        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

        cipher.init(
                Cipher.DECRYPT_MODE,
                new SecretKeySpec(key, KEY_ALGORITHM),
                new IvParameterSpec(iv));

        Arrays.fill(key, (byte) 0x00);
        Arrays.fill(iv, (byte) 0x00);

        desencriptao = cipher.doFinal(ciphertext);
        return cipher.doFinal(ciphertext);
    }
	
	//Metodo para generar el parï¿½metro salt aleatoriamente
	public static void generateSalt(String pass) throws IOException {
		
		String username = ConfigureSetup.getUserName();
		String pathSalt = ConfigureSetup.getPathDrive() + ConfigureSetup.privateKeyLocation+username+"_salt.txt";
		
		final int count = 20;// hash iteration count
		SecureRandom random = new SecureRandom();
		final byte[] salt = new byte[8];
		random.nextBytes(salt);

		String xd = Base64.getEncoder().encodeToString(salt);
		byte[] encoded = xd.getBytes();
		
		FileOutputStream out; 
		out = new FileOutputStream(pathSalt);
		out.write(encoded); 
		out.close();
		cifrarSalt(pass);
	}

	//Metodo para cifrar la sal generada aleatoriamente con otro parï¿½metro de sal que guarda el sistema.
	public static void cifrarSalt(String password) {
		String username = ConfigureSetup.getUserName();
		String pathSalt = ConfigureSetup.getPathDrive() + ConfigureSetup.privateKeyLocation+username+"_salt.txt";
		byte[] salEncriptada = null;
		try {		
			salEncriptada = encrypt(defaultSalt, count, password, username, pathSalt);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	//Metodo para configurar la encriptaciï¿½n de la clave privada de un usuario a partir de la contraseï¿½a del mismo
	public static boolean PassphraseEncrypt(String password) throws Exception {
		
		boolean done = false;
		String username = ConfigureSetup.getUserName();
		String pathPrivateKeyEncripted = ConfigureSetup.getPathDrive() + ConfigureSetup.privateKeyLocation+username+"_PrivateKeyEncripted.key";
		String pathSalt = ConfigureSetup.getPathDrive() + ConfigureSetup.privateKeyLocation+username+"_salt.txt";
		
		File file = new File(pathSalt);
		byte[] fileContent = Files.readAllBytes(file.toPath());

		byte[] sal = null;
		byte[] encriptado = null;
			
		sal = decrypt(defaultSalt, count, password, fileContent);
		encriptado = encrypt(sal, count, password, username, pathPrivateKeyEncripted);
		done = true;
		
		return done;
		
	}
	
	//Función que se encarga del proceso de desencriptado, tanto de la sal como de la clave privada del usuario.
	public static byte[] PassphraseDecrypt(String password) throws Exception {
		boolean done = false;
		String username = ConfigureSetup.getUserName();
		String pathPrivateKeyEncripted = ConfigureSetup.getPathDrive() + ConfigureSetup.privateKeyLocation+username+"_PrivateKeyEncripted.key";
		String pathSalt = ConfigureSetup.getPathDrive() + ConfigureSetup.privateKeyLocation+username+"_salt.txt";
		File file = new File(pathSalt);
		byte[] salFile = Files.readAllBytes(file.toPath());
		
		File fileKey = new File(pathPrivateKeyEncripted);
		byte[] dataKey = Files.readAllBytes(fileKey.toPath());
			
		byte[] sal = decrypt(defaultSalt, count, password, salFile);
		byte[] dec = decrypt(sal, count, password, dataKey);
		done = true;
		
		return dec;
	}

	public static void main(String[] args) throws Exception {
		generateSalt("asdf1234");
		//cifrarSalt("pass");
		boolean a = PassphraseEncrypt("pass");

		
		final String password = "pass"; // el password tiene que venir como argumento
		
		String username = "Carlos"; //el nombre del usuario tambiï¿½n
		String pathPrivateKeyEncripted = ConfigureSetup.getPathDrive() + "/ServidorES/Claves_Privadas/"+username+"_PrivateKeyEncripted.key";
		String pathSalt = ConfigureSetup.getPathDrive() + "/ServidorES/Claves_Privadas/"+username+"_salt.txt";
		String pkplana = null;
		String decriptedKey = "./Files/Backups/"+username+"_PrivateKeyAC.key";
		try {
			pkplana = readFile(decriptedKey, Charset.defaultCharset());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        byte[] cle = pkplana.getBytes();
		byte[] pk =null;
		try {
			pk = Passphrase.PassphraseDecrypt("pass");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			//return false;
		}
	    // Comprobaciï¿½n de que el archivo es realmente el mismo ( a nivel de bytes)
	    if (Arrays.equals(cle, pk))
	    {
	        System.out.println("Yup, they're the same!");
	    }

		

	 
	}
}
