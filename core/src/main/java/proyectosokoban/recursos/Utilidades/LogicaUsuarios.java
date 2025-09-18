package proyectosokoban.recursos.Utilidades;

import com.badlogic.gdx.Input;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Gestiona toda la logica de creacion, lectura y escritura de los datos de
 * los usuarios.
 * Utiliza RandomAccessFile para un acceso eficiente a archivos binarios (.dat).
 * Esta version incluye todos los metodos necesarios para la funcionalidad completa del juego.
 */
public class LogicaUsuarios {

    private final File raiz;
    public static String usuarioLogged = null;

    public LogicaUsuarios() {
        raiz = new File("Usuarios");
        if (!raiz.exists()) {
            raiz.mkdirs();
        }
    }

    // --- Definicion de Rutas a los Archivos ---

    private File dirUsuario(String username) { return new File(raiz, username); }
    private File dirScores(String username) { return new File(dirUsuario(username), "scores"); }
    private File filePerfil(String username) { return new File(dirUsuario(username), "perfil.dat"); }
    private File fileAmigos(String username) { return new File(dirUsuario(username), "amigos.dat"); }
    private File filePrefs(String username) { return new File(dirUsuario(username), "preferencias.dat"); }
    private File fileNivel(String username, int nivel) { return new File(dirScores(username), "nivel" + nivel + ".dat"); }
    private File fileHistorial(String username) { return new File(dirUsuario(username), "historial.dat"); }

    // --- Metodos Auxiliares para Escribir y Leer Datos Binarios ---

    private void writeString(RandomAccessFile raf, String s) throws IOException {
        raf.writeUTF(s != null ? s : "");
    }

    private String readString(RandomAccessFile raf) throws IOException {
        return raf.readUTF();
    }
    
    // --- Gestion de Perfil de Usuario ---

    public boolean CrearUsuario(String username, String nombreCompleto, String password) {
        if (username == null || username.trim().isEmpty() || dirUsuario(username).exists()) {
            return false;
        }

        try {
            dirUsuario(username).mkdir();
            dirScores(username).mkdir();

            long ahora = System.currentTimeMillis();

            // Escribir perfil.dat
            try (RandomAccessFile raf = new RandomAccessFile(filePerfil(username), "rw")) {
                writeString(raf, username);
                writeString(raf, password);
                writeString(raf, nombreCompleto);
                raf.writeLong(ahora);           // fechaRegistro
                raf.writeLong(ahora);           // ultimaSesion
                raf.writeLong(0L);              // tiempoTotalJugadoMs
                raf.writeInt(0);                // rankingGeneral
                raf.writeInt(0);                // nivelesCompletados
                raf.writeInt(0);                // puntuacionAcumulada
                raf.writeBoolean(false);        // avatarPresente
            }

            // Crear preferencias.dat con valores por defecto
            try (RandomAccessFile raf = new RandomAccessFile(filePrefs(username), "rw")) {
                raf.writeInt(100);  // volumen
                raf.writeByte(0);   // idioma
                raf.writeByte(0);   // control
                raf.writeBoolean(false); // mute
            }

            // Crear amigos.dat vacio
            try (RandomAccessFile raf = new RandomAccessFile(fileAmigos(username), "rw")) {
                raf.writeInt(0); // 0 amigos al inicio
            }
            
            // Crear historial.dat vacio
            fileHistorial(username).createNewFile();

            // Crear el primer nivel para que este disponible
            crearNivelSiNoExiste(username, 1);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            dirUsuario(username).delete(); // Limpiar en caso de error
            return false;
        }
    }

    public boolean login(String username, String password) {
        File perfilFile = filePerfil(username);
        if (!perfilFile.exists()) return false;

        try (RandomAccessFile raf = new RandomAccessFile(perfilFile, "rw")) {
            raf.seek(0);
            readString(raf); // Saltar username
            String savedPassword = readString(raf);

            if (savedPassword.equals(password)) {
                readString(raf); // Saltar nombreCompleto
                raf.readLong();  // Saltar fechaRegistro
                raf.writeLong(System.currentTimeMillis()); // Actualizar ultimaSesion
                usuarioLogged = username;
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Gestion de Amigos ---
    
    public List<String> listarAmigos(String username) {
        List<String> amigos = new ArrayList<>();
        File amigosFile = fileAmigos(username);
        if (!amigosFile.exists()) return amigos;

        try (RandomAccessFile raf = new RandomAccessFile(amigosFile, "r")) {
            int count = raf.readInt();
            for (int i = 0; i < count; i++) {
                amigos.add(readString(raf));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return amigos;
    }

    public boolean agregarAmigo(String username, String amigo) {
        if (username.equals(amigo) || listarAmigos(username).contains(amigo) || !dirUsuario(amigo).exists()) {
            return false;
        }
        
        try (RandomAccessFile raf = new RandomAccessFile(fileAmigos(username), "rw")) {
            int count = raf.readInt();
            raf.seek(raf.length());
            writeString(raf, amigo);
            raf.seek(0);
            raf.writeInt(count + 1);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // --- Gestion de Niveles y Puntuaciones ---

    private void crearNivelSiNoExiste(String username, int nivel) throws IOException {
        File nivelFile = fileNivel(username, nivel);
        if (!nivelFile.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "rw")) {
                raf.writeInt(0); // highscore
                raf.writeBoolean(false); // completado
            }
        }
    }
    
    public boolean guardarScore(String username, int nivel, int score) {
        File nivelFile = fileNivel(username, nivel);
        try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "rw")) {
            int highscore = raf.readInt();
            if (score > highscore) {
                raf.seek(0);
                raf.writeInt(score);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean marcarNivelPasado(String username, int nivel) {
        File nivelFile = fileNivel(username, nivel);
        try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "rw")) {
            raf.readInt(); // Saltar highscore
            raf.writeBoolean(true);
            
            if (nivel < 7) {
                crearNivelSiNoExiste(username, nivel + 1);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int ultimoNivelDesbloqueado(String username) {
        for (int i = 7; i >= 1; i--) {
            if (fileNivel(username, i).exists()) {
                return i;
            }
        }
        return 1;
    }

    // --- Gestion de Preferencias ---
    public int[] getPreferencias(String username) {
        // El array ahora es mas grande para incluir las 4 teclas
        // Formato: {vol, idi, ctrl, mute, keyUp, keyDown, keyLeft, keyRight}
        int[] defaults = {100, 0, 0, 0, Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT};
        File prefsFile = filePrefs(username);
        if (!prefsFile.exists()) return defaults;

        try (RandomAccessFile raf = new RandomAccessFile(prefsFile, "r")) {
            int vol = raf.readInt();
            int idi = raf.readByte();
            int ctrl = raf.readByte();
            int mute = raf.readBoolean() ? 1 : 0;
            // Leer las 4 teclas guardadas
            int keyUp = raf.readInt();
            int keyDown = raf.readInt();
            int keyLeft = raf.readInt();
            int keyRight = raf.readInt();
            return new int[]{vol, idi, ctrl, mute, keyUp, keyDown, keyLeft, keyRight};
        } catch (IOException e) {
            return defaults;
        }
    }

    public boolean setPreferencias(String username, int volumen, byte idioma, byte control, boolean mute, int keyUp, int keyDown, int keyLeft, int keyRight) {
        try (RandomAccessFile raf = new RandomAccessFile(filePrefs(username), "rw")) {
            raf.setLength(0); // Limpiar el archivo para reescribir
            raf.writeInt(volumen);
            raf.writeByte(idioma);
            raf.writeByte(control);
            raf.writeBoolean(mute);
            // Escribir las 4 nuevas teclas
            raf.writeInt(keyUp);
            raf.writeInt(keyDown);
            raf.writeInt(keyLeft);
            raf.writeInt(keyRight);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // --- Gestion de Historial ---
    
    public boolean registrarPartida(String username, int nivel, int score, int intentos, long duracionMs, boolean exito) {
        try (RandomAccessFile raf = new RandomAccessFile(fileHistorial(username), "rw")) {
            raf.seek(raf.length());
            raf.writeLong(System.currentTimeMillis());
            raf.writeInt(nivel);
            raf.writeInt(score);
            raf.writeInt(intentos);
            raf.writeLong(duracionMs);
            raf.writeBoolean(exito);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // --- Metodos para Leaderboards y Rankings ---

    // Clase interna para facilitar la ordenacion de puntajes
    private static class ScoreEntry {
        String user;
        int score;

        ScoreEntry(String user, int score) {
            this.user = user;
            this.score = score;
        }
    }
    
    // Comparador para ordenar ScoreEntry de mayor a menor
    private Comparator<ScoreEntry> scoreComparator = new Comparator<ScoreEntry>() {
        @Override
        public int compare(ScoreEntry s1, ScoreEntry s2) {
            return Integer.compare(s2.score, s1.score);
        }
    };

    private int getScoreDeNivel(String username, int nivel) {
        File nivelFile = fileNivel(username, nivel);
        if (!nivelFile.exists()) return 0;
        try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "r")) {
            return raf.readInt();
        } catch (IOException e) {
            return 0;
        }
    }

    private List<String> formatLeaderboard(List<ScoreEntry> scores, int topN) {
        List<String> leaderboard = new ArrayList<>();
        int limit = Math.min(topN > 0 ? topN : scores.size(), scores.size());
        for (int i = 0; i < limit; i++) {
            ScoreEntry entry = scores.get(i);
            leaderboard.add((i + 1) + ". " + entry.user + " - " + entry.score);
        }
        return leaderboard;
    }

    public List<String> leaderboardNivelGlobal(int nivel, int topN) {
        List<ScoreEntry> scores = new ArrayList<>();
        File[] users = raiz.listFiles();
        
        if (users != null) {
            for (File userDir : users) {
                if (userDir.isDirectory()) {
                    String username = userDir.getName();
                    int score = getScoreDeNivel(username, nivel);
                    if (score > 0) {
                        scores.add(new ScoreEntry(username, score));
                    }
                }
            }
        }
        Collections.sort(scores, scoreComparator);
        return formatLeaderboard(scores, topN);
    }

    public List<String> leaderboardNivelAmigos(String username, int nivel, boolean incluirPropio, int topN) {
        List<ScoreEntry> scores = new ArrayList<>();
        List<String> amigos = listarAmigos(username);
        if (incluirPropio && !amigos.contains(username)) {
            amigos.add(username);
        }

        for (String amigo : amigos) {
            int score = getScoreDeNivel(amigo, nivel);
            if (score > 0) {
                scores.add(new ScoreEntry(amigo, score));
            }
        }
        Collections.sort(scores, scoreComparator);
        return formatLeaderboard(scores, topN);
    }
    
    public int miPosicionEnLeaderBoardNivelAmigos(String username, int nivel) {
        List<String> leaderboard = leaderboardNivelAmigos(username, nivel, true, 0); // 0 para obtener todos
        for (int i = 0; i < leaderboard.size(); i++) {
            // Busca una linea que empiece con "X. username - "
            if (leaderboard.get(i).matches("^\\d+\\. " + username + " - .*")) {
                return i + 1;
            }
        }
        return -1; // No encontrado
    }

    private int getTotalScore(String username) {
        int total = 0;
        for (int i = 1; i <= 7; i++) {
            total += getScoreDeNivel(username, i);
        }
        return total;
    }

    public List<String> leaderboardGlobalTotal(int topN) {
        List<ScoreEntry> scores = new ArrayList<>();
        File[] users = raiz.listFiles();
        
        if (users != null) {
            for (File userDir : users) {
                if (userDir.isDirectory()) {
                    String username = userDir.getName();
                    int totalScore = getTotalScore(username);
                    if (totalScore > 0) {
                        scores.add(new ScoreEntry(username, totalScore));
                    }
                }
            }
        }
        Collections.sort(scores, scoreComparator);
        return formatLeaderboard(scores, topN);
    }

    public int miPosicionEnLeaderboardGlobalTotal(String username) {
        List<String> leaderboard = leaderboardGlobalTotal(0);
        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).matches("^\\d+\\. " + username + " - .*")) {
                return i + 1;
            }
        }
        return -1;
    }
    
    public boolean actualizarMiRankingGeneralGlobalTotal(String username) {
        int ranking = miPosicionEnLeaderboardGlobalTotal(username);
        if (ranking == -1) return false;

        try (RandomAccessFile raf = new RandomAccessFile(filePerfil(username), "rw")) {
            raf.seek(0);
            readString(raf); // user
            readString(raf); // pass
            readString(raf); // nombre
            raf.readLong();  // fecha reg
            raf.readLong();  // ultima sesion
            raf.readLong();  // tiempo total
            // El puntero esta en la posicion del ranking
            raf.writeInt(ranking);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}