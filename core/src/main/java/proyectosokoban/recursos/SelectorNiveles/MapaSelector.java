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
        actualizarParedesABloqueos(); // actualizar paredes segun estado de desbloqueo
    }

    private void cargarEstadoDesbloqueo() {
        LogicaUsuarios lu = new LogicaUsuarios();

        // usar el nuevo metodo para determinar el ultimo nivel desbloqueado
        ultimoNivelDesbloqueado = lu.ultimoNivelDesbloqueado(username);

        // asegurar que al menos el nivel 1 este desbloqueado
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
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                if (mapa[y][x] == 10) {
                    int nivelCorrespondiente = obtenerNivelCorrespondiente(x, y);
                    if (nivelCorrespondiente != -1 && isNivelDesbloqueado(nivelCorrespondiente)) {
                        mapa[y][x] = 9; // convertir a camino
                    }
                }
            }
        }
    }

    public void actualizarEstadoDesbloqueo() {
        int ultimoNivelAnterior = ultimoNivelDesbloqueado;
        cargarEstadoDesbloqueo();

        if (ultimoNivelDesbloqueado > ultimoNivelAnterior) {
            actualizarParedesABloqueos();
        }
    }

    private int obtenerNivelCorrespondiente(int x, int y) {
        if (y == 6 && x >= 1 && x < columnas - 1) {
            return mapa[7][x]; // el nivel correspondiente esta justo abajo
        }
        return -1;
    }

    private void cargarTexturas() {
        texturasNiveles = new HashMap<>();
        texturasCaminos = new HashMap<>();
        texturasParedes = new HashMap<>();
        texturaBloqueado = new Texture("Juego/niveles/bloqueado.png"); // textura para paredes de bloqueo

        // cargar texturas para los niveles
        for (int i = 1; i <= 7; i++) {
            texturasNiveles.put(i, new Texture("Juego/niveles/nivel" + i + ".png"));
        }

        // cargar texturas para los caminos
        texturasCaminos.put(9, new Texture("Juego/niveles/camino.png"));
        texturasParedes.put(0, new Texture("Juego/niveles/paredselector.png"));
    }

    public boolean esPosicionValida(int x, int y) {
        if (x < 0 || x >= columnas || y < 0 || y >= filas) {
            return false;
        }

        int valor = mapa[y][x];

        // bno se puede mover a posiciones vacías (0)
        if (valor == 0) {
            return false;
        }

        // si es un nivel (1-7), verificar si esta desbloqueado
        if (valor >= 1 && valor <= 7) {
            return isNivelDesbloqueado(valor);
        }

        // caminos (9) y paredes convertidas a caminos son validos
        return true;
    }

    public int getNivelEnPosicion(int x, int y) {
        if (x < 0 || x >= columnas || y < 0 || y >= filas) {
            return -1;
        }

        int valor = mapa[y][x];

        // solo devuelve el nivel si es un numero entre 1 y 7 y está desbloqueado (ya me quiero dormir pepepe)
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
                        // dibujar nivel
                        textura = texturasNiveles.get(valor);
                    } else if (valor == 9) {
                        // dibujar camino
                        textura = texturasCaminos.get(valor);
                    } else if (valor == 0) {
                        // dibujar pared de bloqueo
                        textura = texturasParedes.get(valor);
                    } else if (valor == 10) {
                        // dibujar pared de bloqueo
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
