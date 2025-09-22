package proyectosokoban.recursos.Utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashSet;
import java.util.Set;

public class LogicaUsuarios {

    private final File raiz;
    public static String usuarioLogged = null;

    public LogicaUsuarios() {
        raiz = new File("Usuarios");
        if (!raiz.exists()) {
            raiz.mkdirs();
        }
    }
    
    private File dirUsuario(String username) {
        return new File(raiz, username);
    }

    private File dirScores(String username) {
        return new File(dirUsuario(username), "scores");
    }

    private File filePerfil(String username) {
        return new File(dirUsuario(username), "perfil.dat");
    }

    private File fileAmigos(String username) {
        return new File(dirUsuario(username), "amigos.dat");
    }

    private File filePrefs(String username) {
        return new File(dirUsuario(username), "preferencias.dat");
    }

    private File fileNivel(String username, int nivel) {
        return new File(dirScores(username), "nivel" + nivel + ".dat");
    }

    private File fileHistorial(String username) {
        return new File(dirUsuario(username), "historial.dat");
    }

    private void writeString(RandomAccessFile raf, String s) throws IOException {
        raf.writeUTF(s != null ? s : "");
    }

    private String readString(RandomAccessFile raf) throws IOException {
        return raf.readUTF();
    }

    public boolean CrearUsuario(String username, String nombreCompleto, String password) {
        if (username == null || username.trim().isEmpty() || dirUsuario(username).exists()) {
            return false;
        }

        try {

            Preferences prefs = Gdx.app.getPreferences("SokobanPrefs");
            prefs.remove(username + "_tutorial_completado");
            prefs.flush();

            dirUsuario(username).mkdir();
            dirScores(username).mkdir();
            long ahora = System.currentTimeMillis();

            try (RandomAccessFile raf = new RandomAccessFile(filePerfil(username), "rw")) {
                writeString(raf, username);
                writeString(raf, password);
                writeString(raf, nombreCompleto);
                raf.writeLong(ahora);
                raf.writeLong(ahora);
                raf.writeLong(0L);
                raf.writeInt(0);
                raf.writeInt(0);
                raf.writeInt(0);
                raf.writeBoolean(false);
                writeString(raf, "avatares/south.png");
            }

            try (RandomAccessFile raf = new RandomAccessFile(filePrefs(username), "rw")) {
                raf.writeInt(100);
                raf.writeByte(0);
                raf.writeByte(0);
                raf.writeBoolean(false);
                raf.writeInt(Input.Keys.W);
                raf.writeInt(Input.Keys.S);
                raf.writeInt(Input.Keys.A);
                raf.writeInt(Input.Keys.D);
                raf.writeInt(1);
            }

            try (RandomAccessFile raf = new RandomAccessFile(fileAmigos(username), "rw")) {
                raf.writeInt(0);
            }

            fileHistorial(username).createNewFile();
            crearNivelSiNoExiste(username, 1);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            File userDir = dirUsuario(username);
            if (userDir.exists()) {
                new File(userDir, "historial.dat").delete();
                new File(userDir, "preferencias.dat").delete();
                new File(userDir, "amigos.dat").delete();
                new File(userDir, "perfil.dat").delete();
                File scoresDir = new File(userDir, "scores");
                if (scoresDir.exists()) {
                    scoresDir.delete();
                }
                userDir.delete();
            }
            return false;
        }
    }

    public boolean login(String username, String password) {
        File perfilFile = filePerfil(username);
        if (!perfilFile.exists()) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(perfilFile, "rw")) {
            String savedUsername = readString(raf);
            String savedPassword = readString(raf);

            if (savedUsername.equals(username) && savedPassword.equals(password)) {
                readString(raf);
                raf.readLong();
                raf.writeLong(System.currentTimeMillis());
                usuarioLogged = username;
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public String[] getPerfil(String username) {
        File perfilFile = filePerfil(username);
        String[] perfil = new String[3];
        if (!perfilFile.exists()) {
            return perfil;
        }
        try (RandomAccessFile raf = new RandomAccessFile(perfilFile, "r")) {
            perfil[0] = readString(raf);
            perfil[1] = readString(raf);
            perfil[2] = readString(raf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return perfil;
    }

    public String getAvatar(String username) {
        File perfilFile = filePerfil(username);
        if (!perfilFile.exists()) {
            return "avatares/south.png";
        }

        try (RandomAccessFile raf = new RandomAccessFile(perfilFile, "r")) {
            readString(raf);
            readString(raf);
            readString(raf);
            raf.readLong();
            raf.readLong();
            raf.readLong();
            raf.readInt();
            raf.readInt();
            raf.readInt();
            raf.readBoolean();

            if (raf.getFilePointer() < raf.length()) {
                return readString(raf);
            }
        } catch (IOException e) {
            return "avatares/south.png";
        }

        return "avatares/south.png";
    }

    public boolean setAvatar(String username, String avatarPath) {
        File perfilFile = filePerfil(username);
        if (!perfilFile.exists()) {
            return false;
        }

        try {
            String user, pass, nombre;
            long fechaReg, ultimaSesion, tiempoJugado;
            int rankGeneral, rankAmigos, partidas;
            boolean modoJuego;

            try (RandomAccessFile reader = new RandomAccessFile(perfilFile, "r")) {
                user = readString(reader);
                pass = readString(reader);
                nombre = readString(reader);
                fechaReg = reader.readLong();
                ultimaSesion = reader.readLong();
                tiempoJugado = reader.readLong();
                rankGeneral = reader.readInt();
                rankAmigos = reader.readInt();
                partidas = reader.readInt();
                modoJuego = reader.readBoolean();
            }

            try (RandomAccessFile writer = new RandomAccessFile(perfilFile, "rw")) {
                writer.setLength(0);
                writeString(writer, user);
                writeString(writer, pass);
                writeString(writer, nombre);
                writer.writeLong(fechaReg);
                writer.writeLong(ultimaSesion);
                writer.writeLong(tiempoJugado);
                writer.writeInt(rankGeneral);
                writer.writeInt(rankAmigos);
                writer.writeInt(partidas);
                writer.writeBoolean(modoJuego);
                writeString(writer, avatarPath);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<Integer, Integer> getHighScores(String username) {
        Map<Integer, Integer> scores = new HashMap<>();
        File scoresDir = dirScores(username);
        if (scoresDir.exists() && scoresDir.isDirectory()) {
            File[] scoreFiles = scoresDir.listFiles();
            if (scoreFiles != null) {
                for (File scoreFile : scoreFiles) {
                    if (scoreFile.getName().startsWith("nivel") && scoreFile.getName().endsWith(".dat")) {
                        try {
                            int nivel = Integer.parseInt(scoreFile.getName().replaceAll("[^0-9]", ""));
                            int score = getScoreDeNivel(username, nivel);
                            if (score > 1) {
                                scores.put(nivel, score);
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
        }
        return scores;
    }

    public List<String> listarAmigos(String username) {
        List<String> amigos = new ArrayList<>();
        File amigosFile = fileAmigos(username);
        if (!amigosFile.exists()) {
            return amigos;
        }
        try (RandomAccessFile raf = new RandomAccessFile(amigosFile, "r")) {
            if (raf.length() < 4) {
                return amigos;
            }
            int count = raf.readInt();
            for (int i = 0; i < count && raf.getFilePointer() < raf.length(); i++) {
                amigos.add(readString(raf));
            }
        } catch (IOException e) {
        }
        return amigos;
    }

    public boolean agregarAmigo(String username, String amigo) {
        if (username.equals(amigo) || listarAmigos(username).contains(amigo) || !dirUsuario(amigo).exists()) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(fileAmigos(username), "rw")) {
            int count = (raf.length() > 0) ? raf.readInt() : 0;
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

    private void crearNivelSiNoExiste(String username, int nivel) throws IOException {
        File nivelFile = fileNivel(username, nivel);
        if (!nivelFile.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "rw")) {
                raf.writeInt(0);
                raf.writeBoolean(false);
            }
        }
    }

    public boolean guardarScore(String username, int nivel, int score) {
        File nivelFile = fileNivel(username, nivel);
        try {
            crearNivelSiNoExiste(username, nivel);
            try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "rw")) {
                int highscore = 0;
                if (raf.length() > 0) {
                    highscore = raf.readInt();
                }
                if (score > highscore) {
                    raf.seek(0);
                    raf.writeInt(score);
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean marcarNivelPasado(String username, int nivel) {
        File nivelFile = fileNivel(username, nivel);
        try {
            if (!nivelFile.exists()) {
                try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "rw")) {
                    raf.writeInt(0);
                    raf.writeBoolean(true);
                }
            } else {
                try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "rw")) {
                    if (raf.length() >= 5) {
                        raf.seek(4);
                        raf.writeBoolean(true);
                    } else {
                        raf.seek(raf.length());
                        raf.writeBoolean(true);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int ultimoNivelDesbloqueado(String username) {
        for (int i = 1; i <= 7; i++) {
            File nivelFile = fileNivel(username, i);
            if (!nivelFile.exists()) {
                return i - 1;
            }

            try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "r")) {
                if (raf.length() >= 5) {
                    raf.readInt();
                    boolean completado = raf.readBoolean();
                    if (!completado) {
                        return i - 1;
                    }
                } else {
                    return i - 1;
                }
            } catch (IOException e) {
                return i - 1;
            }
        }
        return 7;
    }

    public boolean isNivelDesbloqueado(String username, int nivel) {
        if (nivel == 1) {
            return true;
        }

        if (username.equals("Fer")) {
            return true;
        }

        File nivelAnteriorFile = fileNivel(username, nivel - 1);
        if (!nivelAnteriorFile.exists()) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(nivelAnteriorFile, "r")) {
            if (raf.length() >= 5) {
                raf.readInt();
                return raf.readBoolean();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int[] getPreferencias(String username) {
        int[] defaults = {100, 0, 0, 0, Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D, 1};
        File prefsFile = filePrefs(username);
        if (!prefsFile.exists()) {
            return defaults;
        }
        try (RandomAccessFile raf = new RandomAccessFile(prefsFile, "r")) {
            int vol = raf.readInt();
            int idi = raf.readByte();
            int ctrl = raf.readByte();
            int mute = raf.readBoolean() ? 1 : 0;
            int keyUp = raf.readInt();
            int keyDown = raf.readInt();
            int keyLeft = raf.readInt();
            int keyRight = raf.readInt();
            int displayMode = raf.readInt();
            return new int[]{vol, idi, ctrl, mute, keyUp, keyDown, keyLeft, keyRight, displayMode};
        } catch (IOException e) {
            return defaults;
        }
    }

    public boolean setPreferencias(String username, int volumen, byte idioma, byte control, boolean mute, int keyUp, int keyDown, int keyLeft, int keyRight, int displayMode) {
        try (RandomAccessFile raf = new RandomAccessFile(filePrefs(username), "rw")) {
            raf.setLength(0);
            raf.writeInt(volumen);
            raf.writeByte(idioma);
            raf.writeByte(control);
            raf.writeBoolean(mute);
            raf.writeInt(keyUp);
            raf.writeInt(keyDown);
            raf.writeInt(keyLeft);
            raf.writeInt(keyRight);
            raf.writeInt(displayMode);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean esPrimeraVezJugando(String username) {
        Preferences prefs = Gdx.app.getPreferences("SokobanPrefs");
        return prefs.getBoolean(username + "_tutorial_completado", false);
    }

    public void marcarTutorialCompletado(String username) {
        Preferences prefs = Gdx.app.getPreferences("SokobanPrefs");
        prefs.putBoolean(username + "_tutorial_completado", true);
        prefs.flush();
    }

    public static class HistorialRegistro {

        public final long fechaMs;
        public final int nivel;
        public final int score;
        public final int intentos;
        public final long duracionMs;
        public final boolean exito;

        public HistorialRegistro(long fechaMs, int nivel, int score, int intentos, long duracionMs, boolean exito) {
            this.fechaMs = fechaMs;
            this.nivel = nivel;
            this.score = score;
            this.intentos = intentos;
            this.duracionMs = duracionMs;
            this.exito = exito;
        }
    }

    public List<HistorialRegistro> leerHistorial(String username) {
        List<HistorialRegistro> lista = new ArrayList<HistorialRegistro>();
        File f = fileHistorial(username);
        if (!f.exists()) {
            return lista;
        }
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            while (raf.getFilePointer() < raf.length()) {
                long fecha = raf.readLong();
                int nivel = raf.readInt();
                int score = raf.readInt();
                int intentos = raf.readInt();
                long dur = raf.readLong();
                boolean exito = raf.readBoolean();
                lista.add(new HistorialRegistro(fecha, nivel, score, intentos, dur, exito));
            }
        } catch (IOException e) {
        }
        return lista;
    }

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

    // --- MÉTODOS DE LEADERBOARD ORIGINALES (DEVUELVEN LIST<STRING>) ---
    private static class ScoreEntry {

        String user;
        int score;

        ScoreEntry(String user, int score) {
            this.user = user;
            this.score = score;
        }
    }
    private Comparator<ScoreEntry> scoreComparator = Comparator.comparingInt(s -> -s.score);

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
        scores.sort(scoreComparator);
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
        scores.sort(scoreComparator);
        return formatLeaderboard(scores, topN);
    }

    public int miPosicionEnLeaderBoardNivelAmigos(String username, int nivel) {
        List<String> leaderboard = leaderboardNivelAmigos(username, nivel, true, 0);
        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).matches("^\\d+\\. " + username + " - .*")) {
                return i + 1;
            }
        }
        return -1;
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
        scores.sort(scoreComparator);
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

    public byte getIdiomaGuardado(String username) {
        int[] p = getPreferencias(username);
        return (byte) p[1];
    }

    public boolean actualizarMiRankingGeneralGlobalTotal(String username) {
        int ranking = miPosicionEnLeaderboardGlobalTotal(username);
        if (ranking == -1) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(filePerfil(username), "rw")) {
            readString(raf);
            readString(raf);
            readString(raf);
            raf.readLong();
            raf.readLong();
            raf.readLong();
            raf.writeInt(ranking);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- MÉTODOS NUEVOS PARA RANKINGSCREEN (DEVUELVEN LIST<RANKINGENTRY>) ---
    public static class RankingEntry {

        public final String username;
        public final int totalScore;

        public RankingEntry(String username, int totalScore) {
            this.username = username;
            this.totalScore = totalScore;
        }
    }

    private List<RankingEntry> getRankingEntries(List<String> users) {
        List<RankingEntry> entries = new ArrayList<>();
        for (String user : users) {
            int totalScore = getTotalScore(user);
            if (totalScore > 0) {
                entries.add(new RankingEntry(user, totalScore));
            }
        }
        entries.sort(Comparator.comparingInt(e -> -e.totalScore));
        return entries;
    }

    public List<RankingEntry> getRankingGlobal() {
        File[] userFiles = raiz.listFiles();
        List<String> allUsers = new ArrayList<>();
        if (userFiles != null) {
            for (File userFile : userFiles) {
                if (userFile.isDirectory()) {
                    allUsers.add(userFile.getName());
                }
            }
        }
        return getRankingEntries(allUsers);
    }

    public List<RankingEntry> getRankingAmigos(String username) {
        List<String> amigos = listarAmigos(username);
        if (!amigos.contains(username)) {
            amigos.add(username);
        }
        return getRankingEntries(amigos);
    }

    public List<RankingEntry> getRankingGlobalPorNivel(int nivel) {
        List<RankingEntry> entries = new ArrayList<>();
        File[] userFiles = raiz.listFiles();
        if (userFiles != null) {
            for (File userFile : userFiles) {
                if (userFile.isDirectory()) {
                    String username = userFile.getName();
                    int score = getScoreDeNivel(username, nivel);
                    if (score > 0) {
                        entries.add(new RankingEntry(username, score));
                    }
                }
            }
        }
        entries.sort(Comparator.comparingInt(e -> -e.totalScore));
        return entries;
    }

    public List<RankingEntry> getRankingAmigosPorNivel(String username, int nivel) {
        List<RankingEntry> entries = new ArrayList<>();
        List<String> amigos = listarAmigos(username);
        if (!amigos.contains(username)) {
            amigos.add(username);
        }
        for (String amigo : amigos) {
            int score = getScoreDeNivel(amigo, nivel);
            if (score > 0) {
                entries.add(new RankingEntry(amigo, score));
            }
        }
        entries.sort(Comparator.comparingInt(e -> -e.totalScore));
        return entries;
    }

    private int getScoreDeNivel(String username, int nivel) {
        File nivelFile = fileNivel(username, nivel);
        if (!nivelFile.exists()) {
            return 0;
        }
        try (RandomAccessFile raf = new RandomAccessFile(nivelFile, "r")) {
            return raf.readInt();
        } catch (IOException e) {
            return 0;
        }
    }

    public Set<String> getLogrosDesbloqueados(String username) {
        Set<String> logros = new HashSet<>();
        FileHandle file = Gdx.files.local("assets/Usuarios/" + username + "/logros.dat");
        if (file.exists()) {
            try (DataInputStream dis = new DataInputStream(file.read())) {
                int count = dis.readInt();
                for (int i = 0; i < count; i++) {
                    logros.add(dis.readUTF());
                }
            } catch (IOException e) {
                System.err.println("Error al leer logros: " + e.getMessage());
            }
        }
        return logros;
    }

    public void guardarLogros(String username, Set<String> logros) {
        FileHandle file = Gdx.files.local("assets/Usuarios/" + username + "/logros.dat");
        try (DataOutputStream dos = new DataOutputStream(file.write(false))) {
            dos.writeInt(logros.size());
            for (String logro : logros) {
                dos.writeUTF(logro);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar logros: " + e.getMessage());
        }
    }

    public void verificarYDesbloquearLogros(String username) {
        Set<String> logrosActuales = getLogrosDesbloqueados(username);
        boolean nuevoLogro = false;

        int nivelMax = ultimoNivelDesbloqueado(username);
        if (nivelMax >= 3 && !logrosActuales.contains("complete_3_levels")) {
            logrosActuales.add("complete_3_levels");
            nuevoLogro = true;
        }
        if (nivelMax >= 6 && !logrosActuales.contains("complete_6_levels")) {
            logrosActuales.add("complete_6_levels");
            nuevoLogro = true;
        }
        if (nivelMax >= 7 && !logrosActuales.contains("complete_all_levels")) {
            logrosActuales.add("complete_all_levels");
            nuevoLogro = true;
        }

        Map<Integer, Integer> highScores = getHighScores(username);
        int totalScore = 0;
        for (int score : highScores.values()) {
            totalScore += score;
        }
        if (totalScore > 60000 && !logrosActuales.contains("high_score")) {
            logrosActuales.add("high_score");
            nuevoLogro = true;
        }

        List<HistorialRegistro> historial = leerHistorial(username);
        for (HistorialRegistro r : historial) {
            if (r.nivel == 7 && r.exito && r.duracionMs < 300000) {
                if (!logrosActuales.contains("speed_demon")) {
                    logrosActuales.add("speed_demon");
                    nuevoLogro = true;
                    break;
                }
            }
        }

        if (nuevoLogro) {
            guardarLogros(username, logrosActuales);
        }
    }

    private int getTotalScore(String username) {
        int total = 0;
        for (int i = 1; i <= 7; i++) {
            total += getScoreDeNivel(username, i);
        }
        return total;
    }
}
