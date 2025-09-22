// Ruta: core/src/main/java/proyectosokoban/recursos/Utilidades/GestorIdiomas.java
package proyectosokoban.recursos.Utilidades;

import java.util.HashMap;
import java.util.Map;

public class GestorIdiomas {

    private static GestorIdiomas instancia;
    private String codigoIdioma = "es";
    private Map<String, Map<String, String>> diccionarios;
    private UserManager userManager;

    private GestorIdiomas() {
        diccionarios = new HashMap<>();
        userManager = new UserManager();
        inicializarIdiomas();
    }

    public static GestorIdiomas obtenerInstancia() {
        if (instancia == null) {
            instancia = new GestorIdiomas();
        }
        return instancia;
    }

    private void inicializarIdiomas() {
        // ======= ESPAÑOL =======
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
        es.put("login.no_tienes_cuenta", "No tienes cuenta?");
        es.put("login.registrate", "Registrate");
        es.put("login.salir", "SALIR");
        es.put("login.error", "Usuario o contrasena incorrectos.");
        es.put("register.titulo", "Registro de Usuario");
        es.put("register.nombre", "NOMBRE");
        es.put("register.registrarse", "REGISTRARSE");
        es.put("register.ya_tienes_cuenta", "Ya tienes cuenta?");
        es.put("register.login", "Login");
        es.put("register.error_password", "Contrasena invalida. Debe tener 8+ caracteres, letra, numero y simbolo.");
        es.put("register.error_username", "El nombre de usuario ya existe o es invalido.");
        es.put("levelselect.titulo", "Nivel ");
        es.put("levelselect.iniciar_juego", "Iniciar Juego");
        es.put("levelselect.volver_menu", "Volver al Menu");
        es.put("game.nivel", "Nivel: ");
        es.put("game.movimientos", "Movimientos: ");
        es.put("game.empujes", "Empujes: ");
        es.put("game.score", "Score: ");
        es.put("game.tiempo", "Tiempo: ");
        es.put("game.menu_boton", "MENU");
        es.put("game.dialogo_victoria_titulo", "\nNIVEL COMPLETADO!");
        es.put("game.dialogo_victoria_mensaje", "Score: {0}\nMovimientos: {1}\nEmpujes: {2}\nTiempo: {3}");
        es.put("game.dialogo_victoria_reintentar", "Reintentar");
        es.put("game.dialogo_victoria_menu", "Salir");
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
        es.put("profile.title", "Perfil de Usuario");
        es.put("profile.username", "Usuario: ");
        es.put("profile.fullname", "Nombre: ");
        es.put("profile.highscores", "Mejores Puntuaciones");
        es.put("profile.change_avatar", "Cambiar Avatar");
        es.put("profile.close", "Cerrar");
        es.put("avatar.title", "Seleccionar Avatar");
        es.put("avatar.select", "Selecciona un Avatar");
        es.put("avatar.save", "Guardar");
        es.put("avatar.back", "Volver");
        es.put("pause.titulo", "PAUSA");
        es.put("pause.continuar", "Continuar");
        es.put("pause.niveles", "Seleccionar Nivel");
        es.put("pause.menu", "Menu Principal");
        es.put("pause.opciones", "Opciones");
        es.put("pause.reiniciar", "Reiniciar");
        es.put("pause.salir", "Salir");
        es.put("pause.title", "PAUSA");
        es.put("pause.resume", "Continuar");
        es.put("pause.restart", "Reiniciar");
        es.put("pause.level_select", "Seleccionar Nivel");
        es.put("pause.main_menu", "Menu Principal");
        es.put("pause.options", "Opciones");
        es.put("pause.quit", "Salir");
        es.put("history.title", "Historial de partidas");
        es.put("history.fecha", "Fecha");
        es.put("history.nivel", "Nivel");
        es.put("history.score", "Score");
        es.put("history.intentos", "Intentos");
        es.put("history.duracion", "Duración");
        es.put("history.resultado", "Resultado");
        es.put("history.empty", "No hay registros.");
        es.put("historial.view", "Ver Historial");
        es.put("profile.no_scores", "No hay puntuaciones guardadas.");
        es.put("ranking.title", "Ranking");
        es.put("ranking.view", "Vista");
        es.put("ranking.type", "Tipo");
        es.put("ranking.global", "Global");
        es.put("ranking.friends", "Amigos");
        es.put("ranking.total", "Total");
        es.put("ranking.by_level", "Por Nivel");
        es.put("ranking.header.rank", "#");
        es.put("ranking.header.user", "Usuario");
        es.put("ranking.header.score", "Puntuacion");
        es.put("ranking.empty", "No hay datos disponibles");
        es.put("history.yes", "SI");
        es.put("history.no", "NO");
        es.put("profile.achievements", "Logros");
        es.put("achievements.title", "Mis Logros");
        es.put("achievements.back", "Volver al Perfil");
        es.put("achievement.complete_3_levels.name", "Principiante");
        es.put("achievement.complete_3_levels.desc", "Completa los primeros 3 niveles.");
        es.put("achievement.complete_6_levels.name", "Intermedio");
        es.put("achievement.complete_6_levels.desc", "Completa los primeros 6 niveles.");
        es.put("achievement.complete_all_levels.name", "Maestro del Almacen");
        es.put("achievement.complete_all_levels.desc", "Completa todos los 7 niveles.");
        es.put("achievement.high_score.name", "Acumulador de Puntos");
        es.put("achievement.high_score.desc", "Consigue mas de 60,000 puntos en total.");
        es.put("achievement.speed_demon.name", "Demonio de la Velocidad");
        es.put("achievement.speed_demon.desc", "Completa el nivel 7 en menos de 5 minutos.");
        es.put("level_selector.title", "SELECTOR DE NIVELES");
        es.put("level_selector.enter", "Apretar ENTER para entrar al nivel");
        diccionarios.put("es", es);

        // ======= INGLÉS =======
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
        en.put("login.salir", "EXIT");
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
        en.put("game.nivel", "Level: ");
        en.put("game.movimientos", "Moves: ");
        en.put("game.empujes", "Pushes: ");
        en.put("game.score", "Score: ");
        en.put("game.tiempo", "Time: ");
        en.put("game.menu_boton", "MENU");
        en.put("game.dialogo_victoria_titulo", "\nLEVEL COMPLETED!");
        en.put("game.dialogo_victoria_mensaje", "Score: {0}\nMoves: {1}\nPushes: {2}\nTime: {3}");
        en.put("game.dialogo_victoria_reintentar", "Retry");
        en.put("game.dialogo_victoria_menu", "Back to Levels");
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
        en.put("profile.title", "User Profile");
        en.put("profile.username", "Username: ");
        en.put("profile.fullname", "Full Name: ");
        en.put("profile.highscores", "High Scores");
        en.put("profile.change_avatar", "Change Avatar");
        en.put("profile.close", "Close");
        en.put("avatar.title", "Select Avatar");
        en.put("avatar.select", "Select an Avatar");
        en.put("avatar.save", "Save");
        en.put("avatar.back", "Back");
        en.put("pause.titulo", "PAUSE");
        en.put("pause.continuar", "Resume");
        en.put("pause.niveles", "Level Select");
        en.put("pause.menu", "Main Menu");
        en.put("pause.opciones", "Options");
        en.put("pause.reiniciar", "Restart");
        en.put("pause.salir", "Quit");
        en.put("pause.title", "PAUSE");
        en.put("pause.resume", "Resume");
        en.put("pause.restart", "Restart");
        en.put("pause.level_select", "Level Select");
        en.put("pause.main_menu", "Main Menu");
        en.put("pause.options", "Options");
        en.put("pause.quit", "Quit");
        en.put("history.title", "Match history");
        en.put("history.fecha", "Date");
        en.put("history.nivel", "Level");
        en.put("history.score", "Score");
        en.put("history.intentos", "Attempts");
        en.put("history.duracion", "Duration");
        en.put("history.resultado", "Result");
        en.put("history.empty", "No records.");
        en.put("historial.view", "View History");
        en.put("profile.no_scores", "No scores saved.");
        en.put("ranking.title", "Leaderboard");
        en.put("ranking.view", "View");
        en.put("ranking.type", "Type");
        en.put("ranking.global", "Global");
        en.put("ranking.friends", "Friends");
        en.put("ranking.total", "Total");
        en.put("ranking.by_level", "By Level");
        en.put("ranking.header.rank", "#");
        en.put("ranking.header.user", "User");
        en.put("ranking.header.score", "Score");
        en.put("ranking.empty", "No data available");
        en.put("history.yes", "YES");
        en.put("history.no", "NO");
        en.put("profile.achievements", "Achievements");
        en.put("achievements.title", "My Achievements");
        en.put("achievements.back", "Back to Profile");
        en.put("achievement.complete_3_levels.name", "Beginner");
        en.put("achievement.complete_3_levels.desc", "Complete the first 3 levels.");
        en.put("achievement.complete_6_levels.name", "Intermediate");
        en.put("achievement.complete_6_levels.desc", "Complete the first 6 levels.");
        en.put("achievement.complete_all_levels.name", "Warehouse Master");
        en.put("achievement.complete_all_levels.desc", "Complete all 7 levels.");
        en.put("achievement.high_score.name", "Point Hoarder");
        en.put("achievement.high_score.desc", "Get more than 60,000 total points.");
        en.put("achievement.speed_demon.name", "Speed Demon");
        en.put("achievement.speed_demon.desc", "Complete level 7 in less than 5 minutes.");
        en.put("level_selector.title", "LEVEL SELECTOR");
        en.put("level_selector.enter", "Press ENTER to enter the level");
        diccionarios.put("en", en);

        // ======= ITALIANO =======
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
        ita.put("login.salir", "USCIRE");
        ita.put("login.error", "Nome utente o password errati.");
        ita.put("register.titulo", "Registrazione utente");
        ita.put("register.nombre", "NOME COMPLETO");
        ita.put("register.registrarse", "REGISTRATI");
        ita.put("register.ya_tienes_cuenta", "Hai gia un account?");
        ita.put("register.login", "Accedi");
        ita.put("register.error_password", "Password non valida. Deve avere 8+ caratteri, lettera, numero e simbolo.");
        ita.put("register.error_username", "Nome utente gia esistente o non valido.");
        ita.put("levelselect.titulo", "Livello ");
        ita.put("levelselect.iniciar_juego", "Inizia gioco");
        ita.put("levelselect.volver_menu", "Torna al Menu");
        ita.put("game.nivel", "Livello: ");
        ita.put("game.movimientos", "Mosse: ");
        ita.put("game.empujes", "Spinte: ");
        ita.put("game.score", "Punteggio: ");
        ita.put("game.tiempo", "Tempo: ");
        ita.put("game.menu_boton", "MENU");
        ita.put("game.dialogo_victoria_titulo", "\nLIVELLO COMPLETATO!");
        ita.put("game.dialogo_victoria_mensaje", "Punteggio: {0}\nMovimenti: {1}\nSpinge: {2}\nTempo: {3}");
        ita.put("game.dialogo_victoria_reintentar", "Riprova");
        ita.put("game.dialogo_victoria_menu", "Esci");
        ita.put("amigos.titulo", "Gestisci Amici");
        ita.put("amigos.agregar", "Aggiungi Amico");
        ita.put("amigos.volver_menu", "Torna al Menu");
        ita.put("amigos.amigos_list", "Amici:");
        ita.put("amigos.no_amigos", "Non hai amici aggiunti.");
        ita.put("amigos.error_vacio", "Il nome utente non puo' essere vuoto.");
        ita.put("amigos.error_existente", "Hai gia questo utente come amico.");
        ita.put("amigos.exito", "Amico aggiunto con successo.");
        ita.put("amigos.error", "Impossibile aggiungere. Controlla il nome.");
        ita.put("preferences.titulo", "OPZIONI");
        ita.put("preferences.volumen", "Volume");
        ita.put("preferences.display", "DISPLAY");
        ita.put("preferences.controles", "Controlli");
        ita.put("preferences.arriba", "Su");
        ita.put("preferences.abajo", "Giu");
        ita.put("preferences.izquierda", "Sinistra");
        ita.put("preferences.derecha", "Destra");
        ita.put("preferences.cambiar", "Cambia");
        ita.put("preferences.esperando", "Premi un tasto...");
        ita.put("preferences.tecla_en_uso", "Il tasto e' gia in uso. Scegli un altro.");
        ita.put("preferences.control_actualizado", "Controllo aggiornato.");
        ita.put("preferences.guardar", "Salva e torna");
        ita.put("preferences.espera_tecla", "In attesa del tasto per: ");
        ita.put("preferences.idioma", "LINGUA");
        ita.put("amigos.username_message", " Nome utente");
        ita.put("back.button", "TORNA AL MENU");
        ita.put("profile.title", "Profilo Utente");
        ita.put("profile.username", "Utente: ");
        ita.put("profile.fullname", "Nome: ");
        ita.put("profile.highscores", "Punteggi Migliori");
        ita.put("profile.change_avatar", "Cambia Avatar");
        ita.put("profile.close", "Chiudi");
        ita.put("avatar.title", "Seleziona Avatar");
        ita.put("avatar.select", "Seleziona un Avatar");
        ita.put("avatar.save", "Salva");
        ita.put("avatar.back", "Indietro");
        ita.put("pause.titulo", "PAUSA");
        ita.put("pause.continuar", "Continua");
        ita.put("pause.niveles", "Seleziona Livello");
        ita.put("pause.menu", "Menu Principale");
        ita.put("pause.opciones", "Opzioni");
        ita.put("pause.reiniciar", "Ricomincia");
        ita.put("pause.salir", "Esci");
        ita.put("pause.title", "PAUSA");
        ita.put("pause.resume", "Continua");
        ita.put("pause.restart", "Ricomincia");
        ita.put("pause.level_select", "Seleziona Livello");
        ita.put("pause.main_menu", "Menu Principale");
        ita.put("pause.options", "Opzioni");
        ita.put("pause.quit", "Esci");
        ita.put("history.title", "Storico partite");
        ita.put("history.fecha", "Data");
        ita.put("history.nivel", "Livello");
        ita.put("history.score", "Punteggio");
        ita.put("history.intentos", "Tentativi");
        ita.put("history.duracion", "Durata");
        ita.put("history.resultado", "Risultato");
        ita.put("history.empty", "Nessun record.");
        ita.put("historial.view", "Vedi Cronologia");
        ita.put("profile.no_scores", "Nessun punteggio salvato.");
        ita.put("ranking.title", "Classifica");
        ita.put("ranking.view", "Vista");
        ita.put("ranking.type", "Tipo");
        ita.put("ranking.global", "Globale");
        ita.put("ranking.friends", "Amici");
        ita.put("ranking.total", "Totale");
        ita.put("ranking.by_level", "Per Livello");
        ita.put("ranking.header.rank", "#");
        ita.put("ranking.header.user", "Utente");
        ita.put("ranking.header.score", "Punteggio");
        ita.put("ranking.empty", "Nessun dato disponibile");
        ita.put("history.yes", "SI");
        ita.put("history.no", "NO");
        ita.put("profile.achievements", "Obiettivi");
        ita.put("achievements.title", "I Miei Obiettivi");
        ita.put("achievements.back", "Torna al Profilo");
        ita.put("achievement.complete_3_levels.name", "Principiante");
        ita.put("achievement.complete_3_levels.desc", "Completa i primi 3 livelli.");
        ita.put("achievement.complete_6_levels.name", "Intermedio");
        ita.put("achievement.complete_6_levels.desc", "Completa i primi 6 livelli.");
        ita.put("achievement.complete_all_levels.name", "Maestro del Magazzino");
        ita.put("achievement.complete_all_levels.desc", "Completa tutti i 7 livelli.");
        ita.put("achievement.high_score.name", "Accumulatore di Punti");
        ita.put("achievement.high_score.desc", "Ottieni piu di 60,000 punti in totale.");
        ita.put("achievement.speed_demon.name", "Demone della Velocita");
        ita.put("achievement.speed_demon.desc", "Completa il livello 7 in meno di 5 minuti.");
        ita.put("level_selector.title", "SELETTORE DI LIVELLO");
        ita.put("level_selector.enter", "Premi INVIO per entrare nel livello");
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

    public void resetToDefault() {
        this.codigoIdioma = "es";
    }

    public void cambiarIdioma(String nuevoIdioma) {
        if (diccionarios.containsKey(nuevoIdioma)) {
            codigoIdioma = nuevoIdioma;
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

    public String obtenerNombreIdioma(String codigo) {
        switch (codigo) {
            case "es":
                return "Espanol";
            case "en":
                return "English";
            case "ita":
                return "Italiano";
            default:
                return codigo;
        }
    }

    public void cargarPreferenciasUsuario(String username) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        LogicaUsuarios lu = new LogicaUsuarios();
        byte idioma = lu.getIdiomaGuardado(username);
        setIdioma(idioma);
    }

    public void setIdioma(byte idioma) {
        switch (idioma) {
            case 0:
                codigoIdioma = "es";
                break;
            case 1:
                codigoIdioma = "en";
                break;
            case 2:
                codigoIdioma = "ita";
                break;
            default:
                codigoIdioma = "es";
                break;
        }
    }
}
