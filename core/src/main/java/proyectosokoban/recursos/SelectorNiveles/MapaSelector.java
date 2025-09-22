package proyectosokoban.recursos.SelectorNiveles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.Map;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class MapaSelector {

    private int[][] mapa;
    private Map<Integer, Texture> texturasNiveles;
    private Map<Integer, Texture> texturasCaminos;
    private Map<Integer, Texture> texturasParedes;
    private Texture texturaBloqueado;
    private int TILE;
    private int filas, columnas;
    private String username;
    private int ultimoNivelDesbloqueado;

    public MapaSelector(int TILE, String username) {
        this.TILE = TILE;
        this.username = username;
        inicializarMapa();
        cargarTexturas();
        cargarEstadoDesbloqueo();
        actualizarParedesABloqueos(); // Actualizar paredes según estado de desbloqueo
    }

    private void cargarEstadoDesbloqueo() {
        LogicaUsuarios lu = new LogicaUsuarios();

        // Usar el nuevo método para determinar el último nivel desbloqueado
        ultimoNivelDesbloqueado = lu.ultimoNivelDesbloqueado(username);

        // Asegurar que al menos el nivel 1 esté desbloqueado
        if (ultimoNivelDesbloqueado < 1) {
            ultimoNivelDesbloqueado = 1;
        }
    }

    private void inicializarMapa() {
        mapa = new int[][]{
            // 1-7 = NIVEL, 9 = CAMINO, 10 = BLOQUEO
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0},
            {0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0},
            {0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0},
            {0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 0},
            {0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0},
            {0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        filas = mapa.length;
        columnas = mapa[0].length;
    }

    private void actualizarParedesABloqueos() {
        // Convertir paredes de bloqueo (10) a caminos (9) si el nivel correspondiente está desbloqueado
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                if (mapa[y][x] == 10) {
                    int nivelCorrespondiente = obtenerNivelCorrespondiente(x, y);
                    if (nivelCorrespondiente != -1 && isNivelDesbloqueado(nivelCorrespondiente)) {
                        mapa[y][x] = 9; // Convertir a camino
                    }
                }
            }
        }
    }

    public void actualizarEstadoDesbloqueo() {
        int ultimoNivelAnterior = ultimoNivelDesbloqueado;
        cargarEstadoDesbloqueo();

        // Si se desbloqueó un nuevo nivel, actualizar las paredes
        if (ultimoNivelDesbloqueado > ultimoNivelAnterior) {
            actualizarParedesABloqueos();
        }
    }

    private int obtenerNivelCorrespondiente(int x, int y) {
        // Las paredes de bloqueo (valor 10) están en la fila 6 (índice 6)
        // Los niveles (valores 1-7) están en la fila 7 (índice 7), justo debajo
        if (y == 6 && x >= 1 && x < columnas - 1) {
            return mapa[7][x]; // El nivel correspondiente está justo debajo
        }
        return -1;
    }

    private void cargarTexturas() {
        texturasNiveles = new HashMap<>();
        texturasCaminos = new HashMap<>();
        texturasParedes = new HashMap<>();
        texturaBloqueado = new Texture("Juego/niveles/bloqueado.png"); // Textura para paredes de bloqueo

        // Cargar texturas para los niveles
        for (int i = 1; i <= 7; i++) {
            texturasNiveles.put(i, new Texture("Juego/niveles/nivel" + i + ".png"));
        }

        // Cargar texturas para los caminos
        texturasCaminos.put(9, new Texture("Juego/niveles/camino.png")); // Camino (valor 9)
        texturasParedes.put(0, new Texture("Juego/niveles/paredselector.png")); // Camino (valor 0)
    }

    public boolean esPosicionValida(int x, int y) {
        if (x < 0 || x >= columnas || y < 0 || y >= filas) {
            return false;
        }

        int valor = mapa[y][x];

        // No se puede mover a posiciones vacías (0)
        if (valor == 0) {
            return false;
        }

        // Si es un nivel (1-7), verificar si está desbloqueado
        if (valor >= 1 && valor <= 7) {
            return isNivelDesbloqueado(valor);
        }

        // Caminos (9) y paredes convertidas a caminos son válidos
        return true;
    }

    public int getNivelEnPosicion(int x, int y) {
        if (x < 0 || x >= columnas || y < 0 || y >= filas) {
            return -1;
        }

        int valor = mapa[y][x];

        // Solo devuelve el nivel si es un número entre 1 y 7 y está desbloqueado
        if (valor >= 1 && valor <= 7 && isNivelDesbloqueado(valor)) {
            return valor;
        }

        return -1;
    }

    public boolean isNivelDesbloqueado(int nivel) {
        LogicaUsuarios lu = new LogicaUsuarios();
        return lu.isNivelDesbloqueado(username, nivel);
    }

    public void render(SpriteBatch batch) {
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                int valor = mapa[y][x];

                if (valor != 11) {
                    Texture textura = null;

                    if (valor >= 1 && valor <= 7) {
                        // Dibujar nivel
                        textura = texturasNiveles.get(valor);
                    } else if (valor == 9) {
                        // Dibujar camino
                        textura = texturasCaminos.get(valor);
                    } else if (valor == 0) {
                        // Dibujar pared de bloqueo
                        textura = texturasParedes.get(valor);
                    } else if (valor == 10) {
                        // Dibujar pared de bloqueo
                        textura = texturaBloqueado;
                    }
                    if (textura != null) {
                        batch.draw(textura, x * TILE, y * TILE, TILE, TILE);
                    }
                }
            }
        }
    }

    public void dispose() {
        for (Texture tex : texturasNiveles.values()) {
            if (tex != null) {
                tex.dispose();
            }
        }

        for (Texture tex : texturasCaminos.values()) {
            if (tex != null) {
                tex.dispose();
            }
        }

        for (Texture tex : texturasParedes.values()) {
            if (tex != null) {
                tex.dispose();
            }
        }

        if (texturaBloqueado != null) {
            texturaBloqueado.dispose();
        }
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }
}
