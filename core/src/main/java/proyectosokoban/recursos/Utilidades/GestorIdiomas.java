package proyectosokoban.recursos.Utilidades;

import java.util.HashMap;
import java.util.Map;

public class GestorIdiomas {

    private static GestorIdiomas instancia;
    private String codigoIdioma = "es";
    private Map<String, Map<String, String>> diccionarios;
    private LogicaUsuarios userLogic;

    private GestorIdiomas() {
        diccionarios = new HashMap<>();
        userLogic = new LogicaUsuarios();
        inicializarIdiomas();
    }

    public static GestorIdiomas obtenerInstancia() {
        if (instancia == null) {
            instancia = new GestorIdiomas();
        }
        return instancia;
    }

    private void inicializarIdiomas() {
        Map<String, String> es = new HashMap<>();
        es.put("app.name", "SOKOMINE");
        es.put("menu.jugar", "Jugar");
        es.put("menu.amigos", "Amigos");
        es.put("menu.preferencias", "Preferencias");
        es.put("menu.cerrar_sesion", "Cerrar Sesion");
        es.put("login.titulo", "SOKOMINE");
        es.put("login.usuario", "USUARIO");
        es.put("login.contrasena", "CONTRASENA");
        es.put("login.mostrar_contrasena", " MOSTRAR CONTRASENA");
        es.put("login.iniciar_sesion", "INICIAR SESION");
        es.put("login.no_tienes_cuenta", "¿No tienes cuenta?");
        es.put("login.registrate", "Registrate");
        es.put("login.error", "Usuario o contraseña incorrectos.");
        es.put("register.titulo", "Registro de Usuario");
        es.put("register.nombre", "NOMBRE");
        es.put("register.registrarse", "REGISTRARSE");
        es.put("register.ya_tienes_cuenta", "¿Ya tienes cuenta?");
        es.put("register.login", "Login");
        es.put("register.error_password", "Contraseña invalida. Debe tener 8+ caracteres, letra, numero y simbolo.");
        es.put("register.error_username", "El nombre de usuario ya existe o es invalido.");
        es.put("levelselect.titulo", "Nivel ");
        es.put("levelselect.iniciar_juego", "Iniciar Juego");
        es.put("levelselect.volver_menu", "Volver al Menu");
        es.put("game.movimientos", "Movimientos: ");
        es.put("game.empujes", "Empujes: ");
        es.put("game.score", "Score: ");
        es.put("game.tiempo", "Tiempo: ");
        es.put("game.menu_boton", "MENU");
        es.put("game.dialogo_victoria_titulo", "HAS GANADO!");
        es.put("game.dialogo_victoria_mensaje", "¡FELICIDADES!\nScore: {0}\n\n¿Quieres jugar de nuevo?");
        es.put("game.dialogo_victoria_reintentar", "Reintentar");
        es.put("game.dialogo_victoria_menu", "Menu");
        es.put("amigos.titulo", "Gestionar Amigos");
        es.put("amigos.agregar", "Agregar Amigo");
        es.put("amigos.volver_menu", "Volver al Menu");
        es.put("amigos.amigos_list", "Amigos:");
        es.put("amigos.no_amigos", "No tienes amigos agregados.");
        es.put("amigos.error_vacio", "El nombre de usuario no puede estar vacio.");
        es.put("amigos.error_existente", "Ya tienes a este usuario como amigo.");
        es.put("amigos.exito", "Amigo agregado con exito.");
        es.put("amigos.error", "No se pudo agregar. Revisa el nombre.");
        es.put("preferences.titulo", "OPCIONES");
        es.put("preferences.volumen", "Volumen");
        es.put("preferences.display", "DISPLAY");
        es.put("preferences.controles", "Controles");
        es.put("preferences.arriba", "Arriba");
        es.put("preferences.abajo", "Abajo");
        es.put("preferences.izquierda", "Izquierda");
        es.put("preferences.derecha", "Derecha");
        es.put("preferences.cambiar", "Cambiar");
        es.put("preferences.esperando", "Presiona una tecla...");
        es.put("preferences.tecla_en_uso", "Esa tecla ya esta en uso. Elige otra.");
        es.put("preferences.control_actualizado", "Control actualizado.");
        es.put("preferences.guardar", "Guardar y Volver");
        es.put("preferences.espera_tecla", "Esperando tecla para: ");
        es.put("preferences.idioma", "IDIOMA");
        es.put("amigos.username_message", " Nombre de usuario");
        es.put("back.button", "VOLVER AL MENU");
        diccionarios.put("es", es);

        Map<String, String> en = new HashMap<>();
        en.put("app.name", "SOKOMINE");
        en.put("menu.jugar", "Play");
        en.put("menu.amigos", "Friends");
        en.put("menu.preferencias", "Preferences");
        en.put("menu.cerrar_sesion", "Logout");
        en.put("login.titulo", "SOKOMINE");
        en.put("login.usuario", "USER");
        en.put("login.contrasena", "PASSWORD");
        en.put("login.mostrar_contrasena", " SHOW PASSWORD");
        en.put("login.iniciar_sesion", "LOGIN");
        en.put("login.no_tienes_cuenta", "Don't have an account?");
        en.put("login.registrate", "Sign up");
        en.put("login.error", "Incorrect username or password.");
        en.put("register.titulo", "User Registration");
        en.put("register.nombre", "FULLNAME");
        en.put("register.registrarse", "REGISTER");
        en.put("register.ya_tienes_cuenta", "Already have an account?");
        en.put("register.login", "Login");
        en.put("register.error_password", "Invalid password. Must have 8+ characters, letter, number, and symbol.");
        en.put("register.error_username", "Username already exists or is invalid.");
        en.put("levelselect.titulo", "Level ");
        en.put("levelselect.iniciar_juego", "Start Game");
        en.put("levelselect.volver_menu", "Back to Menu");
        en.put("game.movimientos", "Moves: ");
        en.put("game.empujes", "Pushes: ");
        en.put("game.score", "Score: ");
        en.put("game.tiempo", "Time: ");
        en.put("game.menu_boton", "MENU");
        en.put("game.dialogo_victoria_titulo", "YOU WON!");
        en.put("game.dialogo_victoria_mensaje", "CONGRATULATIONS!\nScore: {0}\n\nDo you want to play again?");
        en.put("game.dialogo_victoria_reintentar", "Retry");
        en.put("game.dialogo_victoria_menu", "Menu");
        en.put("amigos.titulo", "Manage Friends");
        en.put("amigos.agregar", "Add Friend");
        en.put("amigos.volver_menu", "Back to Menu");
        en.put("amigos.amigos_list", "Friends:");
        en.put("amigos.no_amigos", "You have no friends added.");
        en.put("amigos.error_vacio", "Username cannot be empty.");
        en.put("amigos.error_existente", "You already have this user as a friend.");
        en.put("amigos.exito", "Friend added successfully.");
        en.put("amigos.error", "Could not add. Check the name.");
        en.put("preferences.titulo", "OPTIONS");
        en.put("preferences.volumen", "Volume");
        en.put("preferences.display", "DISPLAY");
        en.put("preferences.controles", "Controls");
        en.put("preferences.arriba", "Up");
        en.put("preferences.abajo", "Down");
        en.put("preferences.izquierda", "Left");
        en.put("preferences.derecha", "Right");
        en.put("preferences.cambiar", "Change");
        en.put("preferences.esperando", "Press a key...");
        en.put("preferences.tecla_en_uso", "That key is already in use. Choose another.");
        en.put("preferences.control_actualizado", "Control updated.");
        en.put("preferences.guardar", "Save and Back");
        en.put("preferences.espera_tecla", "Waiting for key for: ");
        en.put("preferences.idioma", "LANGUAGE");
        en.put("amigos.username_message", " Username");
        en.put("back.button", "BACK TO MENU");
        diccionarios.put("en", en);

        Map<String, String> ita = new HashMap<>();
        ita.put("app.name", "SOKOMINE");
        ita.put("menu.jugar", "Gioca");
        ita.put("menu.amigos", "Amici");
        ita.put("menu.preferencias", "Preferenze");
        ita.put("menu.cerrar_sesion", "Esci");
        ita.put("login.titulo", "SOKOMINE");
        ita.put("login.usuario", "UTENTE");
        ita.put("login.contrasena", "PASSWORD");
        ita.put("login.mostrar_contrasena", " MOSTRA PASSWORD");
        ita.put("login.iniciar_sesion", "ACCEDI");
        ita.put("login.no_tienes_cuenta", "Non hai un account?");
        ita.put("login.registrate", "Registrati");
        ita.put("login.error", "Nome utente o password errati.");
        ita.put("register.titulo", "Registrazione utente");
        ita.put("register.nombre", "NOME COMPLETO");
        ita.put("register.registrarse", "REGISTRATI");
        ita.put("register.ya_tienes_cuenta", "Hai già un account?");
        ita.put("register.login", "Accedi");
        ita.put("register.error_password", "Password non valida. Deve avere 8+ caratteri, lettera, numero e simbolo.");
        ita.put("register.error_username", "Nome utente già esistente o non valido.");
        ita.put("levelselect.titulo", "Livello ");
        ita.put("levelselect.iniciar_juego", "Inizia gioco");
        ita.put("levelselect.volver_menu", "Torna al Menu");
        ita.put("game.movimientos", "Mosse: ");
        ita.put("game.empujes", "Spinte: ");
        ita.put("game.score", "Punteggio: ");
        ita.put("game.tiempo", "Tempo: ");
        ita.put("game.menu_boton", "MENU");
        ita.put("game.dialogo_victoria_titulo", "HAI VINTO!");
        ita.put("game.dialogo_victoria_mensaje", "CONGRATULAZIONI!\nPunteggio: {0}\n\nVuoi giocare di nuovo?");
        ita.put("game.dialogo_victoria_reintentar", "Riprova");
        ita.put("game.dialogo_victoria_menu", "Menu");
        ita.put("amigos.titulo", "Gestisci Amici");
        ita.put("amigos.agregar", "Aggiungi Amico");
        ita.put("amigos.volver_menu", "Torna al Menu");
        ita.put("amigos.amigos_list", "Amici:");
        ita.put("amigos.no_amigos", "Non hai amici aggiunti.");
        ita.put("amigos.error_vacio", "Il nome utente non puo' essere vuoto.");
        ita.put("amigos.error_existente", "Hai già questo utente come amico.");
        ita.put("amigos.exito", "Amico aggiunto con successo.");
        ita.put("amigos.error", "Impossibile aggiungere. Controlla il nome.");
        ita.put("preferences.titulo", "OPZIONI");
        ita.put("preferences.volumen", "Volume");
        ita.put("preferences.display", "DISPLAY");
        ita.put("preferences.controles", "Controlli");
        ita.put("preferences.arriba", "Su");
        ita.put("preferences.abajo", "Giù");
        ita.put("preferences.izquierda", "Sinistra");
        ita.put("preferences.derecha", "Destra");
        ita.put("preferences.cambiar", "Cambia");
        ita.put("preferences.esperando", "Premi un tasto...");
        ita.put("preferences.tecla_en_uso", "Il tasto e' gia' in uso. Scegli un altro.");
        ita.put("preferences.control_actualizado", "Controllo aggiornato.");
        ita.put("preferences.guardar", "Salva e torna");
        ita.put("preferences.espera_tecla", "In attesa del tasto per: ");
        ita.put("preferences.idioma", "LINGUA");
        ita.put("amigos.username_message", " Nome utente");
        ita.put("back.button", "TORNA AL MENU");
        diccionarios.put("ita", ita);
    }

    public String setTexto(String clave) {
        Map<String, String> textosIdioma = diccionarios.get(codigoIdioma);
        if (textosIdioma != null && textosIdioma.containsKey(clave)) {
            return textosIdioma.get(clave);
        }
        Map<String, String> esp = diccionarios.get("es");
        if (esp != null && esp.containsKey(clave)) {
            return esp.get(clave);
        }

        return "[" + clave + "]";
    }
    
    public String setTexto(String clave, Object... args) {
        String texto = setTexto(clave);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                texto = texto.replace("{" + i + "}", String.valueOf(args[i]));
            }
        }
        return texto;
    }


    public void cambiarIdioma(String nuevoIdioma) {
        if (diccionarios.containsKey(nuevoIdioma)) {
            codigoIdioma = nuevoIdioma;
            if (LogicaUsuarios.usuarioLogged != null) {
                int[] prefs = userLogic.getPreferencias(LogicaUsuarios.usuarioLogged);
                userLogic.setPreferencias(LogicaUsuarios.usuarioLogged, prefs[0], (byte) getCodigoIdiomaByte(nuevoIdioma), (byte) prefs[2], prefs[3] == 1, prefs[4], prefs[5], prefs[6], prefs[7], prefs[8]);
            }
        }
    }
    
    private int getCodigoIdiomaByte(String codigo) {
        switch (codigo) {
            case "es":
                return 0;
            case "en":
                return 1;
            case "ita":
                return 2;
            default:
                return 0;
        }
    }

    public String obtenerCodigoIdioma() {
        return codigoIdioma;
    }

    public String[] obtenerIdiomasDisponibles() {
        return new String[]{"es", "en", "ita"};
    }
    
    public int getIdiomaIndex() {
        switch (codigoIdioma) {
            case "es": return 0;
            case "en": return 1;
            case "ita": return 2;
            default: return 0;
        }
    }

    public String obtenerNombreIdioma(String codigo) {
        switch (codigo) {
            case "es":
                return "Español";
            case "en":
                return "English";
            case "ita":
                return "Italiano";
            default:
                return codigo;
        }
    }

    public void cargarPreferenciasUsuario(String username) {
        if (username != null) {
            int[] prefs = userLogic.getPreferencias(username);
            int idiomaIndex = prefs[1];
            switch (idiomaIndex) {
                case 0:
                    cambiarIdioma("es");
                    break;
                case 1:
                    cambiarIdioma("en");
                    break;
                case 2:
                    cambiarIdioma("ita");
                    break;
            }
        }
    }
}