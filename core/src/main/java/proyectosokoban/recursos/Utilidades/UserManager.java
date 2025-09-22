package proyectosokoban.recursos.Utilidades;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USERS_FILE_PATH = "assets/Usuarios/";
    private static final String USERS_FILE_NAME = "usuarios.dat";
    private static final int USERNAME_LENGTH = 20;
    private static final int PASSWORD_LENGTH = 20;
    private static final int AVATAR_LENGTH = 100;
    private static final int RECORD_LENGTH = (USERNAME_LENGTH * 2) + (PASSWORD_LENGTH * 2) + (AVATAR_LENGTH * 2) + 4;

    private RandomAccessFile raf;

    public UserManager() {
        try {
            File directory = new File(USERS_FILE_PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            raf = new RandomAccessFile(USERS_FILE_PATH + USERS_FILE_NAME, "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean buscarUsuario(String username) {
        try {
            raf.seek(0);
            while (raf.getFilePointer() < raf.length()) {
                long startOfRecord = raf.getFilePointer();
                String storedUser = readFixedString(USERNAME_LENGTH);
                if (storedUser.equals(username)) {
                    return true;
                }
                raf.seek(startOfRecord + RECORD_LENGTH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registrar(String username, String password) {
        if (buscarUsuario(username)) {
            return false;
        }
        try {
            raf.seek(raf.length());
            writeFixedString(username, USERNAME_LENGTH);
            writeFixedString(password, PASSWORD_LENGTH);
            // al registrar se guarda el avatar por defecto
            writeFixedString("avatares/south.png", AVATAR_LENGTH);
            raf.writeInt(0); // puntuacion inicial
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean validar(String username, String password) {
        try {
            raf.seek(0);
            while (raf.getFilePointer() < raf.length()) {
                long currentPos = raf.getFilePointer();
                String storedUser = readFixedString(USERNAME_LENGTH);
                String storedPass = readFixedString(PASSWORD_LENGTH);
                if (storedUser.equals(username) && storedPass.equals(password)) {
                    return true;
                }
                raf.seek(currentPos + RECORD_LENGTH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void guardarPuntuacion(String username, int score) {
        try {
            raf.seek(0);
            while (raf.getFilePointer() < raf.length()) {
                long startOfRecord = raf.getFilePointer();
                String storedUser = readFixedString(USERNAME_LENGTH);
                if (storedUser.equals(username)) {
                    // posicionarse justo antes del campo de puntuacion
                    raf.seek(startOfRecord + (USERNAME_LENGTH * 2) + (PASSWORD_LENGTH * 2) + (AVATAR_LENGTH * 2));
                    int oldScore = raf.readInt();
                    if (score > oldScore) {
                        // regresar a la misma posición para escribir
                        raf.seek(startOfRecord + (USERNAME_LENGTH * 2) + (PASSWORD_LENGTH * 2) + (AVATAR_LENGTH * 2));
                        raf.writeInt(score);
                    }
                    return;
                }
                raf.seek(startOfRecord + RECORD_LENGTH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getAvatar(String username) {
        try {
            raf.seek(0);
            while (raf.getFilePointer() < raf.length()) {
                long startOfRecord = raf.getFilePointer();
                String storedUser = readFixedString(USERNAME_LENGTH);
                if (storedUser.equals(username)) {
                    raf.seek(startOfRecord + (USERNAME_LENGTH * 2) + (PASSWORD_LENGTH * 2)); // posicionarse después de user y pass
                    return readFixedString(AVATAR_LENGTH);
                }
                raf.seek(startOfRecord + RECORD_LENGTH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "avatares/south.png"; // avatar por defecto si hay error o no se encuentra
    }

    public void actualizarAvatar(String username, String avatarPath) {
        try {
            raf.seek(0);
            while (raf.getFilePointer() < raf.length()) {
                long startOfRecord = raf.getFilePointer();
                String storedUser = readFixedString(USERNAME_LENGTH);
                if (storedUser.equals(username)) {
                    // posicionarse justo donde empieza el campo del avatar
                    raf.seek(startOfRecord + (USERNAME_LENGTH * 2) + (PASSWORD_LENGTH * 2));
                    writeFixedString(avatarPath, AVATAR_LENGTH);
                    return;
                }
                raf.seek(startOfRecord + RECORD_LENGTH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Map<Integer, Integer> getHighScores(String username) {
        Map<Integer, Integer> scores = new HashMap<>();
        File scoresDir = new File("assets/Usuarios/" + username + "/scores");
        if (scoresDir.exists() && scoresDir.isDirectory()) {
             File[] scoreFiles = scoresDir.listFiles();
            if (scoreFiles != null) {
                for (File scoreFile : scoreFiles) {
                    if (scoreFile.getName().startsWith("nivel") && scoreFile.getName().endsWith(".dat")) {
                        try (DataInputStream dis = new DataInputStream(new FileInputStream(scoreFile))) {
                            int nivel = Integer.parseInt(scoreFile.getName().replaceAll("[^0-9]", ""));
                            int score = dis.readInt();
                            scores.put(nivel, score);
                        } catch (IOException | NumberFormatException e) {
                            System.err.println("Error al leer score: " + scoreFile.getName());
                        }
                    }
                }
            }
        }
        return scores;
    }

    private void writeFixedString(String s, int length) throws IOException {
        StringBuilder sb = new StringBuilder(s);
        sb.setLength(length);
        raf.writeUTF(sb.toString());
    }

    private String readFixedString(int length) throws IOException {
        String s = raf.readUTF();
        return s.trim();
    }

    public void close() {
        try {
            if (raf != null) {
                raf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}