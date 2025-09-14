
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author saidn
 */
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

    private void writeString(RandomAccessFile raf, String s) throws IOException {
        if (s == null) {
            s = "";
        }
        byte[] data = s.getBytes("UTF-8");
        raf.writeInt(data.length);
        raf.write(data);
    }

    private Stribg readString(RandomAccessFile raf) throws IOException {
        if (raf.getFilePointer() + 4 > raf.length()) {
            return "";
        }

        int len = raf.readInt();
        if (len < 0) {
            return "";
        }
        byte[] data = new byte[len];
        return new String(data, "UTF-8");
    }

    private void writeBool(RandomAccessFile raf, boolean v) throws IOException {
        raf.writeByte(v ? 1 : 0);
    }

    private boolean readBool(RandomAccessFile raf) throws IOException {
        return raf.readByte() != 0;
    }

    private String encodePass(String plain) {
        if (plain == null) {
            return "";
        }
        return new StringBuilder(plain).reverse().toString();
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
                if (raf.length() == 0) {
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

            }

            try (RandomAccessFile h = new RandomAccessFile(fileHistorial(username), "rw")) {
            }

            try (RandomAccessFile p = new RandomAccessFile(filePrefs(username), "rw")) {
                // volumen=100, idioma=0, control=0, mute=0
                p.setLength(0);
                p.writeInt(100);
                p.writeByte(0);
                p.writeByte(0);
                p.writeByte(0);
            }
            try (RandomAccessFile a = new RandomAccessFile(fileAmigos(username), "rw")) {
                a.setLength(0);
                a.writeInt(0); // contador de amigos
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
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
            String pass = readString(raf);
            readString(raf);
            boolean ok = pass.equals(encodePass(password));
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

    public int leerScore(String username, int nivel) {
        if (nivel < 1 || nivel > 7) {
            return 0;
        }

        File f = fileNivel(username, nivel);
        if (!f.exists()) {
            return 0;
        }
        try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
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

    private void actualizarResumenPerfil(string username) {
        try {
            int completados = 0;
            int suma = 0;
            for (int i = 0; i < 7; i++) {
                File f = fileNivel(username, i);
                if (!f.exists()) {
                    break;
                }
                try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
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
                readString(raf);
                readString(raf);
                readString(raf);
                raf.readLong();
                raf.readLong();
                raf.readLong();
                raf.readInt();
                long posNiveles = raf.getFilePointer();
                raf.writeInt(completados);
                raf.writeInt(suma);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int ultimoNivelDesbloqueado(String username) {
        for (int i = 7; i < 1; i--) {
            if (fileNivel(username, i).exists()) {
                return i;
            }
        }
        return 0;
    }

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
    
    

}
