/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;
import config.Manejador;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
            
            DateFormat format_date = new SimpleDateFormat("yyyy/MM/dd");
            Date fecha = new Date();
            String date_now = format_date.format(fecha);
        
            JSONObject cambio = new JSONObject("{nombre: \"Mi segunda libreta\", fecha_modificacion: \""+ date_now +"\"}");
            
            boolean exito = admin.update(cambio, new JSONObject("{id_notebook: 2}"));
            
            if(exito) {
                System.out.println("Registro modificado de manera satisfactoria");
            }
        } catch(FileNotFoundException fe) {
            fe.printStackTrace();
        
        } catch(IOException ioe) {
            ioe.printStackTrace();
        
        } catch(JSONException je) {
            je.printStackTrace();
        }
        
    }
    
}
