package proyectosokoban.recursos.SelectorNiveles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.Map;

public class MapaSelector {

    private int[][] mapa;
    private Map<Integer, Texture> texturasNiveles;
    private Map<Integer, Texture> texturasCaminos;
    private int TILE;
    private int filas, columnas;

    public MapaSelector(int TILE) {
        this.TILE = TILE;
        inicializarMapa();
        cargarTexturas();
    }

    private void inicializarMapa() {
        // Mapa al estilo Super Mario Bros 3
        // 0 = vacío, 1-7 = niveles, 8 = camino horizontal, 9 = camino vertical
        mapa = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {9, 1, 9, 9, 2, 0, 0, 0, 0, 0, 6, 9, 9, 7, 0},
            {0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0},
            {0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0},
            {0, 0, 0, 0, 3, 9, 9, 4, 9, 9, 5, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        filas = mapa.length;
        columnas = mapa[0].length;
    }

    private void cargarTexturas() {
        texturasNiveles = new HashMap<>();
        texturasCaminos = new HashMap<>();

        // Cargar texturas para los niveles
        for (int i = 1; i <= 7; i++) {
            texturasNiveles.put(i, new Texture("nivel.png"));
        }

        // Cargar texturas para los caminos
        texturasCaminos.put(8, new Texture("camino.png")); // Horizontal
        texturasCaminos.put(9, new Texture("camino.png")); // Vertical
    }

    public boolean esPosicionValida(int x, int y) {
        if (x < 0 || x >= columnas || y < 0 || y >= filas) {
            return false;
        }

        // Solo se puede mover a posiciones que no sean vacías (0)
        return mapa[y][x] != 0;
    }

    public int getNivelEnPosicion(int x, int y) {
        if (x < 0 || x >= columnas || y < 0 || y >= filas) {
            return -1;
        }

        int valor = mapa[y][x];

        // Solo devuelve el nivel si es un número entre 1 y 7
        if (valor >= 1 && valor <= 7) {
            return valor;
        }

        return -1;
    }

    public void render(SpriteBatch batch) {
        for (int y = 0; y < filas; y++) {
            for (int x = 0; x < columnas; x++) {
                int valor = mapa[y][x];

                if (valor != 0) {
                    Texture textura = null;

                    if (valor >= 1 && valor <= 7) {
                        textura = texturasNiveles.get(valor);
                    } else if (valor == 8 || valor == 9) {
                        textura = texturasCaminos.get(valor);
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
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }
}
