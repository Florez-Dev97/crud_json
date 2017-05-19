/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;
import config.Manejador;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Fernando
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Manejador admin = new Manejador();
        
        try {
            admin.setRuta("notebooks.json");
            admin.Conectar();
            admin.setNom_bd("notebooks");
            admin.setNom_coleccion("notebooks");
            JSONObject busqueda = admin.Select( new JSONObject("{id_notebook: 2}") );
            
            System.out.println("Busqueda:");
            System.out.println( busqueda.toString(4) );
        } catch(FileNotFoundException fe) {
            fe.printStackTrace();
        
        } catch(IOException ioe) {
            ioe.printStackTrace();
        
        } catch(JSONException je) {
            je.printStackTrace();
        }
        
    }
    
}
