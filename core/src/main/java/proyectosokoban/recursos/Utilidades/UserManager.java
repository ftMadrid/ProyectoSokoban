package proyectosokoban.recursos.Utilidades;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Calendar;

public class UserManager {
    
    public static ArrayList<UserManager> usuarios = new ArrayList<>();
    public static UserManager usuarioLogged = null;
    
    // Datos Generales
    private String usuario;
    private String nombre;
    private String password;
    private Calendar fecha_registro;
    private Calendar ultima_sesion;
    private Image avatar;
    
    // Datos de Partidas
    private long tiempo_jugado;
    private int puntuacion;
    
    public UserManager(String usuario, String nombre, String password){
        this.usuario = usuario;
        this.nombre = nombre;
        this.password = password;
        fecha_registro = Calendar.getInstance();
    }
    
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Calendar getFechaRegistro() {
        return fecha_registro;
    }

    public Calendar getUltimaSesion() {
        return ultima_sesion;
    }

    public void setUltimaSesion(Calendar ultima_sesion) {
        this.ultima_sesion = ultima_sesion;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public long getTiempoJugado() {
        return tiempo_jugado;
    }

    public void setTiempoJugado(long tiempo_jugado) {
        this.tiempo_jugado += tiempo_jugado;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
