package proyectosokoban.recursos.Utilidades;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class LogicaUsuarios {

    private final File raiz;

    public LogicaUsuarios() {
        raiz = new File("Usuarios");
        if (!raiz.exists()) {
            raiz.mkdirs();
        }
    }

    private File dirUsuario(String username) {
        return new File(raiz, username);
    }

    private File filePerfil(String username) {
        return new File(dirUsuario(username), "perfil.dat");
    }

    private File dirScores(String username) {
        return new File(dirUsuario(username), "scores");
    }

    private File fileNivel(String username, int nivel) {
        return new File(dirScores(username), "nivel" + nivel + ".dat");
    }

    private File fileHistorial(String username) {
        return new File(dirUsuario(username), "historial.dat");
    }

    private File filePrefs(String username) {
        return new File(dirUsuario(username), "preferencias.dat");
    }

    private File fileAmigos(String username) {
        return new File(dirUsuario(username), "amigos.dat");
    }

    private File fileAvatar(String username) {
        return new File(dirUsuario(username), "avatar.bin");
    }

    //helpers 
    private void writeString(RandomAccessFile raf, String s) throws IOException {
        if (s == null) {
            s = "";
        }
        byte[] data = s.getBytes("UTF-8");
        raf.writeInt(data.length);
        raf.write(data);
    }

    private String readString(RandomAccessFile raf) throws IOException {
        if (raf.getFilePointer() + 4 > raf.length()) {
            return "";
        }
        int len = raf.readInt();
        if (len < 0) {
            return "";
        }
        byte[] data = new byte[len];
        raf.readFully(data); // <- FALTABA
        return new String(data, "UTF-8");
    }

    private void writeBool(RandomAccessFile raf, boolean v) throws IOException {
        raf.writeByte(v ? 1 : 0);
    }

    private boolean readBool(RandomAccessFile raf) throws IOException {
        return raf.readByte() != 0;
    }

    private List<String> listarUsuariosRaiz() {
        List<String> out = new ArrayList<>();
        File[] arr = raiz.listFiles();
        if (arr == null) {
            return out;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].isDirectory()) {
                out.add(arr[i].getName());
            }
        }
        return out;
    }

    private int highscoreUsuarioNivel(String user, int nivel) {
        if (nivel < 1 || nivel > 7) {
            return 0;
        }
        File f = fileNivel(user, nivel);
        if (!f.exists()) {
            return 0;
        }
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            int hs = raf.readInt(); // primer entero del archivo
            // raf.readByte(); // byte "completado", no necesario para ranking
            return hs;
        } catch (IOException e) {
            return 0;
        }
    }

    private List<String> leerAmigosBasico(String username) {
        List<String> out = new ArrayList<>();
        File af = fileAmigos(username);
        if (!af.exists()) {
            return out;
        }
        try (RandomAccessFile raf = new RandomAccessFile(af, "r")) {
            int n = raf.readInt();
            for (int i = 0; i < n; i++) {
                out.add(readString(raf));
            }
        } catch (IOException e) {
        }
        return out;
    }

    private static class ScoreEntry {

        String user;
        int valor;
    }

    private void ordenarDescPorValor(List<ScoreEntry> a) {
        for (int i = 0; i < a.size(); i++) {
            int max = i;
            for (int j = i + 1; j < a.size(); j++) {
                ScoreEntry ej = a.get(j);
                ScoreEntry em = a.get(max);
                if (ej.valor > em.valor
                        || (ej.valor == em.valor && ej.user.compareToIgnoreCase(em.user) < 0)) {
                    max = j;
                }
            }
            if (max != i) {
                ScoreEntry tmp = a.get(i);
                a.set(i, a.get(max));
                a.set(max, tmp);
            }
        }
    }

    private List<String> formatearLeaderboard(List<ScoreEntry> lista, int max) {
        List<String> out = new ArrayList<>();
        int limite = (max <= 0 || max > lista.size()) ? lista.size() : max;
        for (int i = 0; i < limite; i++) {
            ScoreEntry e = lista.get(i);
            out.add((i + 1) + ". " + e.user + " - " + e.valor);
        }
        return out;
    }

    // Codificacion
    private String encodePass(String plain) {
        if (plain == null) {
            return "";
        }
        char[] a = plain.toCharArray();
        int i = 0, j = a.length - 1;
        while (i < j) {
            char t = a[i];
            a[i] = a[j];
            a[j] = t;
            i++;
            j--;
        }
        return new String(a);
    }

    private void crearNivelSiNoExiste(String username, int nivel, int scoreInicial, boolean completado) throws IOException {
        File f = fileNivel(username, nivel);
        if (!f.exists()) {
            try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
                raf.setLength(0);
                raf.writeInt(scoreInicial);
                raf.writeByte(completado ? 1 : 0);
            }
        }
    }

    // Perfil 
    public boolean CrearUsuario(String username, String nombreCompleto, String password) {
        try {
            File du = dirUsuario(username);
            if (du.exists()) {
                return false;
            }
            if (!du.mkdir()) {
                return false;
            }

            File ds = dirScores(username);
            ds.mkdir();

            crearNivelSiNoExiste(username, 1, 0, false);

            long ahora = System.currentTimeMillis();

            try (RandomAccessFile raf = new RandomAccessFile(filePerfil(username), "rw")) {
                raf.setLength(0);
                // username, passwordCodificada, nombreCompleto,
                // fechaRegistro, ultimaSesion,
                // tiempoTotalJugadoMs, rankingGeneral,
                // nivelesCompletados, puntuacionAcumulada,
                // avatarPresente
                writeString(raf, username);
                writeString(raf, encodePass(password));
                writeString(raf, nombreCompleto);
                raf.writeLong(ahora);
                raf.writeLong(ahora);
                raf.writeLong(0L);
                raf.writeInt(0);
                raf.writeInt(0);
                raf.writeInt(0);
                writeBool(raf, false);
            }

            // historial, prefs, amigos
            try (RandomAccessFile h = new RandomAccessFile(fileHistorial(username), "rw")) {
                /* vacio */ }
            try (RandomAccessFile p = new RandomAccessFile(filePrefs(username), "rw")) {
                p.setLength(0);
                p.writeInt(100); // volumen
                p.writeByte(0);  // idioma
                p.writeByte(0);  // control
                p.writeByte(0);  // mute
            }
            try (RandomAccessFile a = new RandomAccessFile(fileAmigos(username), "rw")) {
                a.setLength(0);
                a.writeInt(0); // contador amigos
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean login(String username, String password) {
        File pf = filePerfil(username);
        if (!pf.exists()) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(pf, "rw")) {
            raf.seek(0);
            String u = readString(raf);
            String passCod = readString(raf);
            String nombre = readString(raf);
            long fechaReg = raf.readLong();
            long posUltima = raf.getFilePointer();
            long ultima = raf.readLong();

            boolean ok = passCod.equals(encodePass(password));
            if (!ok) {
                return false;
            }

            raf.seek(posUltima);
            raf.writeLong(System.currentTimeMillis());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sumarTiempoJugado(String username, long tiempo) {
        File pf = filePerfil(username);
        if (!pf.exists()) {
            return false;
        }
        try (RandomAccessFile raf = new RandomAccessFile(pf, "rw")) {
            raf.seek(0);
            readString(raf); // user
            readString(raf); // pass
            readString(raf); // nombre
            raf.readLong();  // fecha reg
            raf.readLong();  // ultima sesion
            long posTiempo = raf.getFilePointer();
            long actual = raf.readLong();
            raf.seek(posTiempo);
            raf.writeLong(actual + Math.max(0, tiempo));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //niveles
    public int leerScore(String username, int nivel) {
        if (nivel < 1 || nivel > 7) {
            return 0;
        }
        File f = fileNivel(username, nivel);
        if (!f.exists()) {
            return 0;
        }
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            return raf.readInt();
        } catch (IOException e) {
            return 0;
        }
    }

    public boolean guardarScore(String username, int nivel, int score) {
        if (nivel < 1 || nivel > 7) {
            return false;
        }
        File f = fileNivel(username, nivel);
        if (!f.exists()) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
            int hs = raf.readInt();
            byte comp = raf.readByte();
            if (score > hs) {
                raf.seek(0);
                raf.writeInt(score);
                raf.writeByte(score > 0 ? 1 : comp);
                if (score > 0 && nivel < 7) {
                    crearNivelSiNoExiste(username, nivel + 1, 0, false);
                }
                actualizarResumenPerfil(username);
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean marcarNivelPasado(String username, int nivel) {
        if (nivel < 1 || nivel > 7) {
            return false;
        }
        File f = fileNivel(username, nivel);
        if (!f.exists()) {
            return false;
        }

        try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
            int hs = raf.readInt();
            byte comp = raf.readByte();
            if (comp == 0) {
                raf.seek(0);
                raf.writeInt(hs > 0 ? hs : 1);
                raf.writeByte(1);
                if (nivel < 7) {
                    crearNivelSiNoExiste(username, nivel + 1, 0, false);
                }
                actualizarResumenPerfil(username);
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private void actualizarResumenPerfil(String username) {
        try {
            int completados = 0;
            int suma = 0;
            for (int i = 1; i <= 7; i++) {
                File f = fileNivel(username, i);
                if (!f.exists()) {
                    break;
                }
                try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
                    int hs = raf.readInt();
                    byte comp = raf.readByte();
                    if (comp != 0) {
                        completados++;
                    }
                    suma += hs;
                }
            }

            File pf = filePerfil(username);
            if (!pf.exists()) {
                return;
            }

            try (RandomAccessFile raf = new RandomAccessFile(pf, "rw")) {
                raf.seek(0);
                readString(raf); // user
                readString(raf); // pass
                readString(raf); // nombre
                raf.readLong();  // fecha reg
                raf.readLong();  // ultima sesion
                raf.readLong();  // tiempo total
                raf.readInt();   // ranking general
                // ahora estamos posicionados en nivelesCompletados
                raf.writeInt(completados);
                raf.writeInt(suma);
                // avatarPresente queda como estaba
            }
        } catch (IOException ignored) {
        }
    }

    public int ultimoNivelDesbloqueado(String username) {
        for (int i = 7; i >= 1; i--) { // <- condicion corregida
            if (fileNivel(username, i).exists()) {
                return i;
            }
        }
        return 0;
    }

    // historial
    public boolean registrarPartida(String username, int nivel, int score, int intentos, long duracionMs, boolean exito) {
        File hf = fileHistorial(username);
        if (!hf.exists()) {
            return false;
        }
        try (RandomAccessFile raf = new RandomAccessFile(hf, "rw")) {
            raf.seek(raf.length());
            raf.writeLong(System.currentTimeMillis());
            raf.writeInt(nivel);
            raf.writeInt(score);
            raf.writeInt(intentos);
            raf.writeLong(duracionMs);
            raf.writeByte(exito ? 1 : 0);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> leerHistorial(String username, int maxRegistros) {
        List<String> out = new ArrayList<>();
        File hf = fileHistorial(username);
        if (!hf.exists()) {
            return out;
        }

        final int REC = 8 + 4 + 4 + 4 + 8 + 1;
        try (RandomAccessFile raf = new RandomAccessFile(hf, "r")) {
            long tam = raf.length();
            long n = tam / REC;
            long start = Math.max(0, n - maxRegistros) * REC;
            raf.seek(start);
            for (long pos = start; pos < tam; pos += REC) {
                long ts = raf.readLong();
                int nv = raf.readInt();
                int sc = raf.readInt();
                int it = raf.readInt();
                long dur = raf.readLong();
                boolean ex = raf.readByte() != 0;
                out.add("ts=" + ts + " nivel=" + nv + " score=" + sc + " intentos=" + it + " durMs=" + dur + " exito=" + ex);
            }
        } catch (IOException ignored) {
        }
        return out;
    }

    //preferencias
    public boolean setPreferencias(String username, int volumen, byte idioma, byte control, boolean mute) {
        try (RandomAccessFile raf = new RandomAccessFile(filePrefs(username), "rw")) {
            raf.setLength(0);
            if (volumen < 0) {
                volumen = 0;
            }
            if (volumen > 100) {
                volumen = 100;
            }
            raf.writeInt(volumen);
            raf.writeByte(idioma);
            raf.writeByte(control);
            raf.writeByte(mute ? 1 : 0);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public int[] getPreferencias(String username) {
        int[] def = new int[]{100, 0, 0, 0};
        File pf = filePrefs(username);
        if (!pf.exists()) {
            return def;
        }
        try (RandomAccessFile raf = new RandomAccessFile(pf, "r")) {
            int vol = raf.readInt();
            int idi = raf.readByte();
            int ctrl = raf.readByte();
            int mute = raf.readByte();
            return new int[]{vol, idi, ctrl, mute};
        } catch (IOException e) {
            return def;
        }
    }

    //amigos
    public boolean agregarAmigo(String username, String amigo) {
        try (RandomAccessFile raf = new RandomAccessFile(fileAmigos(username), "rw")) {
            raf.seek(0);
            int n = raf.readInt();
            raf.seek(raf.length());
            writeString(raf, amigo);
            raf.seek(0);
            raf.writeInt(n + 1);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<String> listarAmigos(String username) {
        List<String> out = new ArrayList<>();
        File af = fileAmigos(username);
        if (!af.exists()) {
            return out;
        }

        try (RandomAccessFile raf = new RandomAccessFile(af, "r")) {
            int n = raf.readInt();
            for (int i = 0; i < n; i++) {
                out.add(readString(raf));
            }
        } catch (IOException ignored) {
        }
        return out;
    }

    public List<String> listarAmgios(String username) {
        return listarAmigos(username);
    }

    //avatar
    public boolean setAvatar(String username, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(fileAvatar(username))) {
            fos.write(data);
        } catch (IOException e) {
            return false;
        }

        File pf = filePerfil(username);
        if (!pf.exists()) {
            return false;
        }
        try (RandomAccessFile raf = new RandomAccessFile(pf, "rw")) {
            raf.seek(0);
            readString(raf); // user
            readString(raf); // pass
            readString(raf); // nombre
            raf.readLong();  // fecha reg
            raf.readLong();  // ultima
            raf.readLong();  // tiempo
            raf.readInt();   // ranking
            raf.readInt();   // niveles
            raf.readInt();   // suma
            writeBool(raf, true); // avatarPresente
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ranking por nivel
   
private int totalHighscoreUsuario(String user) {
    int suma = 0;
    for (int i = 1; i <= 7; i++) {
        File f = fileNivel(user, i);
        if (!f.exists()) break;
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            int hs = raf.readInt();
            if (raf.getFilePointer() < raf.length()) raf.readByte();
            suma += hs;
        } catch (IOException e) {
        }
    }
    return suma;
}

    private List<String> leaderboardNivelGlobal(int nivel, int topN) {

        List<String> usuarios = listarUsuariosRaiz();
        List<ScoreEntry> lista = new ArrayList<>();

        for (int i = 0; i < usuarios.size(); i++) {
            String u = usuarios.get(i);
            int hs = highscoreUsuarioNivel(u, nivel);
            ScoreEntry e = new ScoreEntry();
            e.user = u;
            e.valor = hs;
            lista.add(e);
        }

        ordenarDescPorValor(lista);
        return formatearLeaderboard(lista, topN);
    }
    
    public boolean setRankingGeneral(String username, int ranking) {
    File pf = filePerfil(username);
    if (!pf.exists()) return false;
    try (RandomAccessFile raf = new RandomAccessFile(pf, "rw")) {
        raf.seek(0);
        readString(raf);  // username
        readString(raf);  // password codificada
        readString(raf);  // nombre completo
        raf.readLong();   // fechaRegistro
        raf.readLong();   // ultimaSesion
        raf.readLong();   // tiempoTotalJugadoMs
        raf.writeInt(ranking); // aqui va rankingGeneral
        return true;
    } catch (IOException e) {
        return false;
    }
}


    public List<String> leaderboardNivelAmigos(String username, int nivel, boolean incluirPropio, int topN) {
        List<String> participantes = leerAmigosBasico(username);
        if (incluirPropio) {
            participantes.add(0, username);
        }

        List<ScoreEntry> lista = new ArrayList<>();
        for (int i = 0; i < participantes.size(); i++) {
            String u = participantes.get(i);
            if (!dirUsuario(u).exists()) {
                continue;
            }

            int hs = highscoreUsuarioNivel(u, nivel);
            ScoreEntry e = new ScoreEntry();
            e.user = u;
            e.valor = hs;
            lista.add(e);
        }

        ordenarDescPorValor(lista);
        return formatearLeaderboard(lista, topN);
    }

    public int miPosicionEnLeaderBoardNivelAmigos(String username, int nivel) {
        List<String> tabla = leaderboardNivelAmigos(username, nivel, true, 0);

        for (int i = 0; i < tabla.size(); i++) {
            if (tabla.get(i).contains(". " + username + " -")) {
                return i + 1;
            }
        }
        return -1;
    }

    //ranking general 
    public List<String> leaderboardGlobalTotal(int topN) {
        List<String> usuarios = listarUsuariosRaiz();
        List<ScoreEntry> lista = new ArrayList<>();

        for (int i = 0; i < usuarios.size(); i++) {
            String u = usuarios.get(i);
            if (!filePerfil(u).exists()) {
                continue;
            }

            ScoreEntry e = new ScoreEntry();
            e.user = u;
            e.valor = totalHighscoreUsuario(u);
            lista.add(e);
        }

        ordenarDescPorValor(lista);
        return formatearLeaderboard(lista, topN);
    }

    public int miPosicionEnLeaderboardGlobalTotal(String username) {
        List<String> tabla = leaderboardGlobalTotal(0);
        for (int i = 0; i < tabla.size(); i++) {
            if (tabla.get(i).contains(". " + username + " -")) {
                return i + 1;
            }
        }
        return -1;
    }

    public boolean actualizarMiRankingGeneralGlobalTotal(String username) {
        int pos = miPosicionEnLeaderboardGlobalTotal(username);
        if (pos <= 0) {
            return false;
        }
        return setRankingGeneral(username, pos);
    }

}
