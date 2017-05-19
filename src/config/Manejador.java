/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 *
 * @author Fernando
 */
public class Manejador {
    private String ruta;
    private String nom_bd;
    private String nom_coleccion;
    private Object[] campos;
    private File archivo;
    private String src_archivo;
    
    public Manejador() {
       this.ruta = ""; 
       this.src_archivo = "";
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNom_bd() {
        return nom_bd;
    }

    public void setNom_bd(String nom_bd) {
        this.nom_bd = nom_bd;
    }
    
    public String getNom_coleccion() {
        return nom_coleccion;
    }

    public void setNom_coleccion(String nom_coleccion) {
        this.nom_coleccion = nom_coleccion;
    }

    public Object[] getCampos() {
        return campos;
    }

    public void setCampos(Object[] campos) {
        this.campos = campos;
    }
    
    public void Conectar() throws FileNotFoundException, IOException {
        this.archivo = new File( this.ruta );
        InputStream is = new FileInputStream( this.archivo );
        
        int ascii;
        
        while( (ascii = is.read()) != -1 ) {
            this.src_archivo += (char) ascii;
            this.src_archivo = this.src_archivo.trim();
        }
        is.close();
    }
    
    public Object Select() throws JSONException {
        JSONObject js_obj = new JSONObject( this.src_archivo );
        return js_obj.get( this.nom_coleccion );
    }
    
    /**
     * @param parametros
     * <p>Recibe los parametros de busqueda de la siguiente forma
     * <ul>
     *  <li>[</li>
     *  <li>["campo", "valor"], ["campo", "valor"], ...</li>
     *  <li>]</li>
     * </ul>
     * </p>
     * @return 
     * @throws org.json.JSONException
    */
    public JSONObject Select(JSONObject parametros) throws JSONException {
        // validar si el string es un object o un array
        JSONObject js_obj = new JSONObject( this.src_archivo );
        JSONArray js_array = (JSONArray) js_obj.get( this.nom_coleccion );
        
        JSONObject search;
        JSONObject retorno = js_obj;
        
        System.out.println("Original:\n" + js_array.toString(4));
        
        Iterator it_p = parametros.keys();
        String param = (String) it_p.next();
        
        boolean encontrado = false;
        for(int i = 0, j = js_array.length(); i < j; i++) {
            search = js_array.getJSONObject(i);
            Iterator<String> it = search.keys();
            
            while( it.hasNext() ) {
                String key = it.next();
                if( key.equals(param) ) {                    
                    String busqueda1 = search.get(key).toString();
                    String busqueda2 = parametros.get(param).toString();
                    
                    if(busqueda1 == null ? busqueda2 == null : busqueda1.equals(busqueda2)) {
                        encontrado = true;                        
                        retorno = search;
                        break;
                    }
                }
            }
            
            if(encontrado) {
                break;
            }
        }
        return retorno;
    }
}
