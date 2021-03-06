/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private JSONObject dont_exists;
    
    public Manejador() throws JSONException {
       this.ruta = ""; 
       this.src_archivo = "";
       this.dont_exists = new JSONObject("{ id: 0, msg: Registro no encontrado }");
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
        }
        is.close();
    }
    
    public boolean saveFile() throws FileNotFoundException, IOException {
        File copia = new File("Copia.json");
        InputStream is = new FileInputStream( this.archivo );
        OutputStream os = new FileOutputStream( copia );
        
        byte[] data = this.src_archivo.getBytes("UTF-8");
        os.write(data);
        
        is.close();
        os.close();
        return true;
    }
    
    public Object Select() throws JSONException {
        JSONObject js_obj = new JSONObject( this.src_archivo );
        return js_obj.get( this.nom_coleccion );
    }
    
    /**
     * @param str_obj 
     * Recibe como parametro el objeto que se desea evaluar
     * @return int<br />
     * 0 si es un valor simple
     * 1 si es un JSONObject
     * 2 si es un JSONArray
    */
    private int getTypeJSON(String str_obj) {
        Object objeto;
        try {
            objeto = new JSONObject(str_obj);
            return 1;
        
        } catch(JSONException ex) {
            try {
                objeto = new JSONArray(str_obj);
                return 2;
                
            } catch(JSONException ex2) {
                return 0;
            }
        }  
    }
    
    public JSONObject Select(JSONObject parametros) throws JSONException {
        // validar si el string es un object o un array
        JSONObject js_obj = new JSONObject( this.src_archivo );
        JSONArray js_array = (JSONArray) js_obj.get( this.nom_coleccion );
        
        JSONObject search;
        JSONObject retorno = this.dont_exists;
        
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
                        search.put("idx", i);
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
    
    public JSONObject SelectIntoArray(JSONObject objeto, JSONObject busqueda) throws JSONException {
        JSONObject retorno = this.dont_exists;
        
        JSONArray js_array_obj;
        JSONArray js_array_busqueda;
        
        Iterator it_json = objeto.keys();
        Iterator it_busqueda = busqueda.keys();
        
        String key_busqueda = "", key_obj = "", key_arr_obj = "";
        
        int idx_busqueda = 0;
        boolean fin = false;
        
        if(it_busqueda.hasNext()) {
            key_busqueda = it_busqueda.next().toString();
            js_array_busqueda = (JSONArray) busqueda.get(key_busqueda);
            JSONObject obj_into_arr = js_array_busqueda.getJSONObject(0);
            
            Iterator it_key_to_search = obj_into_arr.keys();
            if(it_key_to_search.hasNext()) {
                String key_into_arr = it_key_to_search.next().toString();
                String value_of_key = obj_into_arr.get(key_into_arr).toString();
                
                while( it_json.hasNext() ) {
                    key_obj = it_json.next().toString();

                    if(!key_obj.equals(key_busqueda)) {
                        continue;
                    }

                    js_array_obj = (JSONArray) objeto.get(key_obj);

                    for(int i = 0, l = js_array_obj.length(); i < l; i++) {
                        JSONObject arr_obj = js_array_obj.getJSONObject(i);

                        Iterator it_arr_obj = arr_obj.keys();

                        while(it_arr_obj.hasNext()) {
                            key_arr_obj = it_arr_obj.next().toString();
                            String value_of_key_obj = arr_obj.get(key_arr_obj).toString();
                            
                            if( !key_arr_obj.equals(key_into_arr) || !value_of_key_obj.equals(value_of_key) ) {
                                continue;
                            }
                            
                            retorno = objeto;
                            retorno.remove(key_obj);
                            
                            arr_obj.put("idx", idx_busqueda);
                            retorno.put(key_obj, new JSONArray("["+ arr_obj +"]"));
                            
                            fin = true;
                            break;
                        }
                        
                        idx_busqueda++;
                    }
                    
                    if(fin) break;
                }
            }
        }
        
        return retorno;
    }
    
    public boolean update(JSONObject criteria, JSONObject changes) throws JSONException, IOException {
        JSONObject js_obj = new JSONObject( this.src_archivo );
        JSONArray js_array = (JSONArray) js_obj.get( this.nom_coleccion );
        
        boolean retorno = false;
        
        JSONObject busqueda = this.Select(criteria);
        
        if( !busqueda.equals(this.dont_exists) ) {
            Iterator it_changes = changes.keys();
            while( it_changes.hasNext() ) {
                String key_c = it_changes.next().toString();
                busqueda.put(key_c, changes.get(key_c));
            }
            
            int idx = busqueda.getInt("idx");
            busqueda.remove("idx");
            
            js_obj.put(this.nom_coleccion, js_array.put(idx, busqueda));
            retorno = true;
        }
        
        this.src_archivo = js_obj.toString(4);
        this.saveFile();
        return retorno;
    }
    
    public boolean updateIntoArray(JSONObject criteria, JSONObject changes) throws JSONException, IOException {
        boolean retorno = false;
        
        JSONObject js_obj = new JSONObject( this.src_archivo );
        JSONArray js_array = (JSONArray) js_obj.get( this.nom_coleccion );
        
        if(criteria.has("idx")) { 
            int idx_registro = criteria.getInt("idx");
            JSONObject registro = js_array.getJSONObject(idx_registro);
            JSONArray arr_registro, arr_criteria, arr_changes;
            JSONObject obj_arr_registro, obj_arr_criteria, obj_arr_changes;

            Iterator it_changes = changes.keys();

            while(it_changes.hasNext()) {
                String key = it_changes.next().toString();

                int idx_arr_registro = 0;
                arr_registro = registro.getJSONArray(key);
                arr_criteria = criteria.getJSONArray(key);
                arr_changes = changes.getJSONArray(key);

                obj_arr_criteria = arr_criteria.getJSONObject(0);
                obj_arr_changes = arr_changes.getJSONObject(0);
                
                idx_arr_registro = obj_arr_criteria.getInt("idx");
                obj_arr_registro = arr_registro.getJSONObject( idx_arr_registro );

                Iterator field_changes = obj_arr_changes.keys();
                while(field_changes.hasNext()) {
                    String key_change = field_changes.next().toString();
                    obj_arr_registro.put(key_change, obj_arr_changes.get(key_change));
                }

                arr_registro.put(idx_arr_registro, obj_arr_registro);
                registro.put(key, arr_registro);

                js_obj.put(this.nom_coleccion, js_array.put(idx_registro, registro));
                retorno = true;
            }
        }
        
        this.src_archivo = js_obj.toString(4);
        this.saveFile();
        return retorno;
    }
    
    public boolean delete(JSONObject criteria) throws JSONException, IOException {
        boolean retorno = false;
        
        JSONObject js_obj = new JSONObject( this.src_archivo );
        JSONArray js_array = (JSONArray) js_obj.get( this.nom_coleccion );
        
        JSONObject busqueda = this.Select(criteria);
        
        if(!busqueda.equals(this.dont_exists)) {
            System.out.println(busqueda.toString(4));
            
            int indice = busqueda.getInt("idx");
            System.out.println(indice);
            
            js_array.remove(indice);
            js_obj.put(this.nom_coleccion, js_array);
            
            retorno = true;
            
            this.src_archivo = js_obj.toString(4);
            this.saveFile();
        }
        
        return retorno;
    }
    
    public boolean deleteIntoArray(JSONObject criteria, JSONObject target) throws JSONException, IOException {
        boolean retorno = false;

        JSONObject js_obj = new JSONObject( this.src_archivo );
        JSONArray js_array = (JSONArray) js_obj.get( this.nom_coleccion );
        
        System.out.println(criteria.toString(4));
        
        if(criteria.has("idx")) {
            int idx_registro = criteria.getInt("idx");
            int idx_arr_obj = 0;
            
            JSONObject registro = js_array.getJSONObject(idx_registro);
            
            JSONArray arr_registro, arr_criteria;
            JSONObject obj_arr_criteria;
            
            Iterator it_target = target.keys();
            if(it_target.hasNext()) {
                String key = it_target.next().toString();
                
                arr_registro = registro.getJSONArray(key);
                arr_criteria = criteria.getJSONArray(key);

                obj_arr_criteria = arr_criteria.getJSONObject(0);
                
                idx_arr_obj = obj_arr_criteria.getInt("idx");
                
                arr_registro.remove(idx_arr_obj);
                registro.put(key, arr_registro);
                
                js_array.put(idx_registro, registro);
                js_obj.put(this.nom_coleccion, js_array);
                retorno = true;
                
                this.src_archivo = js_obj.toString(4);
                this.saveFile();
            }
        }
        
        return retorno;
    }
}
