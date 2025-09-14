
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author saidn
 */
public class LogicaUsuarios {

    private final File direct;

    public LogicaUsuarios() {
        direct = new File("Usuarios");
        if (!direct.exists()) {
            direct.mkdirs();
        }
    }

    public boolean CrearUser(String nombre, int password) {

        
        try{
        File CarpetaUser = new File(direct,nombre);
        File CarpetaScores = new File(CarpetaUser,"scores");
            
        
        if(CarpetaUser.exists()){
        JOptionPane.showMessageDialog(null, "Un Usuario Con Este Username Ya Existe");
        }
        
        if(!CarpetaScores.mkdirs()){
        JOptionPane.showMessageDialog(null, "No Se Pudo Crear La Carpeta De Scores");
        }  
        
        File n1 = new File(CarpetaScores,"Nivel1.dat");
        try(RandomAccessFile raf = new RandomAccessFile(n1,"rw")){
        if(raf.length()==0){
        raf.writeDouble(0);
        }
        }
        
        File perfil = new File (CarpetaUser,"Perfil.dat");
        
       try(RandomAccessFile raf = new RandomAccessFile(perfil,"rw")){
       if(raf.length()==0){
       raf.writeUTF(nombre);
       raf.writeInt(password);
       }
       }
       return true;
        }catch(IOException e){
        JOptionPane.showMessageDialog(null, "Hubo Un Error En La Creacion De Los Archivos");
        return false;
        }
    }
    
    
}
