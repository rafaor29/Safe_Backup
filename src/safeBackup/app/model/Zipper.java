package safeBackup.app.model;
import java.nio.file.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {
	public static String checkExtension(String fileName) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i >= 0) { extension = fileName.substring(i+1); }
		return extension;
	}
	
    private static void toZip(String sourceFiles, String outFile) throws Exception{
    	
    	File f = new File(outFile);
    	ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
    	ZipEntry e = new ZipEntry(sourceFiles);
    	out.putNextEntry(e);

    	byte[] data = Files.readAllBytes(new File(sourceFiles).toPath());
    	out.write(data, 0, data.length);
    	out.closeEntry();

    	out.close();
    	System.out.println("Ha terminado de comprimir el archivo");
    }
    
    //Comprime el archivo que se pasa como parámetro generando un archivo .zip
    public static void comprimir(String[] inFile, String[] outFile) {
    	try {
    		
  
    		int contador = 0;
    		for(String file:inFile) {
    			//Mira si el archivo no tiene extensión para comprimirlo como carpeta
    			if(checkExtension(file).equals("")) {
    				zipDir(inFile[contador], outFile[contador]);
    			}
    			//comprime el fichero designado
    			else {
    				toZip(inFile[contador], outFile[contador]);
    			}
    			contador++;
    		}
    		System.out.println("Done");
    		
    	}	
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void zipDir(String dirName, String nameZipFile) throws IOException {
        ZipOutputStream zip = null;
        FileOutputStream fW = null;
        fW = new FileOutputStream(nameZipFile);
        zip = new ZipOutputStream(fW);
        addFolderToZip("", dirName, zip);
        zip.close();
        fW.close();
    }

    private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException {
        File folder = new File(srcFolder);
        if (folder.list().length == 0) {
            addFileToZip(path , srcFolder, zip, true);
        }
        else {
            for (String fileName : folder.list()) {
                if (path.equals("")) {
                    addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
                } 
                else {
                     addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
                }
            }
        }
    }

    private static void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag) throws IOException {
        File folder = new File(srcFile);
        if (flag) {
            zip.putNextEntry(new ZipEntry(path + "/" +folder.getName() + "/"));
        }
        else {
            if (folder.isDirectory()) {
                addFolderToZip(path, srcFile, zip);
            }
            else {
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
                in.close();
            }
        }
    }
    
	public static void main(String[] args) {
    		String[] inFile = {"C:\\test\\prueba3.txt", "C:\\test\\carpeta"};
    		String outFile[] = {"C:\\test\\testeo.zip","C:\\test\\testeo2.zip" };
  
    		comprimir(inFile, outFile);

     }	
}
