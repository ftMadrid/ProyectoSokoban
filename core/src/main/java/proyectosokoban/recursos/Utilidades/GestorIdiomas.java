package proyectosokoban.recursos.Utilidades;

import java.util.HashMap;
import java.util.Map;

public class GestorIdiomas {
    
    private static GestorIdiomas instancia;
    private String codigoIdioma = "es"; // default el spanish pue ya sabe que pedo
    private Map<String, Map<String, String>> diccionarios;
    
    private GestorIdiomas() {
        diccionarios = new HashMap<>();
        inicializarIdiomas();
    }
    
    public static GestorIdiomas obtenerInstancia() {
        if (instancia == null) {
            instancia = new GestorIdiomas();
        }
        return instancia;
    }
    
    private void inicializarIdiomas() {
        // Spanish mi rey 
        Map<String, String> es = new HashMap<>();
        es.put("menu.titulo", "Sokomine");
        
        diccionarios.put("es", es);
        
        // Ingles
        Map<String, String> en = new HashMap<>();
        en.put("menu.titulo", "Sokomine");
        
        diccionarios.put("en", en);
        
        // Italiano
        Map<String, String> ita = new HashMap<>();
        ita.put("menu.titulo", "Sokomine");
        
        diccionarios.put("ita", ita);
    }
    
    public String setTexto(String clave) {
        Map<String, String> textosIdioma = diccionarios.get(codigoIdioma);
        if (textosIdioma != null && textosIdioma.containsKey(clave)) {
            return textosIdioma.get(clave);
        }
        // por si no encuentra el idioma pone el espanol
        Map<String, String> esp = diccionarios.get("es");
        if (esp != null && esp.containsKey(clave)) {
            return esp.get(clave);
        }
        
        return "[" + clave + "]"; // textos faltantes (estetica mi causa gaaaa)
    }
    
    public void cambiarIdioma(String nuevoIdioma) {
        if (diccionarios.containsKey(nuevoIdioma)) {
            codigoIdioma = nuevoIdioma;
            System.out.println("Idioma cambiado a: " + obtenerNombreIdioma(nuevoIdioma));
        }
    }
    
    public String obtenerCodigoIdioma() {
        return codigoIdioma;
    }
    
    public String[] obtenerIdiomasDisponibles() {
        return new String[]{"es", "en", "ita"};
    }
    
    public String obtenerNombreIdioma(String codigo) {
        switch (codigo) {
            case "es": return "Español";
            case "en": return "English";
            case "ita": return "Italiano";
            default: return codigo;
        }
    }
    
    /* FUNCIONES QUE MEDIO LAS DEJE HECHAS PARA QUE LAS ADAPTES A TU CODIGO
    public void cargarPreferenciasUsuario() {
        SistemaUsuarios sistema = SistemaUsuarios.obtenerInstancia();
        if (sistema.haySesionActiva()) {
            String idiomaUsuario = sistema.obtenerUsuarioActual().obtenerPreferencias().obtenerIdioma();
            if (idiomaUsuario != null && diccionarios.containsKey(idiomaUsuario)) {
                codigoIdioma = idiomaUsuario;
            }
        }
    }
    
    public void guardarPreferenciasUsuario() {
        SistemaUsuarios sistema = SistemaUsuarios.obtenerInstancia();
        if (sistema.haySesionActiva()) {
            sistema.obtenerUsuarioActual().obtenerPreferencias().establecerIdioma(codigoIdioma);
            sistema.guardarProgreso();
        }
    } */
}
