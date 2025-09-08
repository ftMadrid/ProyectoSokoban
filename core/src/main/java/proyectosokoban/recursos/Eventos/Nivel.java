package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

public class Nivel {

    private int numero;
    private int[][] mapa;
    private List<Pared> paredes;
    private List<Suelo> suelos;
    private List<Caja> cajas;
    private List<Objetivo> objetivos;
    private boolean completado = false;

    private int spawnJugadorX;
    private int spawnJugadorY;

    private final int TILE = 90;
    private final int FILAS = 8;
    private final int COLUMNAS = 12;

    public Nivel(int numero) {
        this.numero = numero;
        inicializar();
    }

    private void inicializar() {
        paredes = new ArrayList<>();
        suelos = new ArrayList<>();
        cajas = new ArrayList<>();
        objetivos = new ArrayList<>();

        switch (numero) {
            case 1:
                mapa = new int[][]{
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1},
                    {1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
                    {1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
                    {1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                spawnJugadorX = 2;
                spawnJugadorY = 2;

                // Cajas
                cajas.add(new Caja(7, 3, 8, 6));

                // Objetivos
                objetivos.add(new Objetivo(8, 6));
                break;

            case 2:
                mapa = new int[][]{
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
                    {1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
                    {1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                };
                spawnJugadorX = 1;
                spawnJugadorY = 1;

                cajas.add(new Caja(3, 3, 5, 5));
                cajas.add(new Caja(2, 4, 6, 4));

                objetivos.add(new Objetivo(5, 5));
                objetivos.add(new Objetivo(6, 4));
                break;

            // Agregar más niveles con sus cajas, objetivos y spawn
            default:
                System.out.println("Nivel no definido");
                return;
        }

        // Crear paredes y suelos
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                if (mapa[y][x] == 1) {
                    paredes.add(new Pared(x, y));
                } else {
                    suelos.add(new Suelo(x, y));
                }
            }
        }
    }

    // Renderizado ahora recibe el SpriteBatch desde Sokoban
    public void render(SpriteBatch batch) {

        // Renderizar suelos
        for (Suelo suelo : suelos) {
            suelo.render(batch, TILE);
        }

        // Renderizar paredes
        for (Pared pared : paredes) {
            pared.render(batch, TILE);
        }

        // Renderizar objetivos
        for (Objetivo obj : objetivos) {
            obj.render(batch, TILE);
        }

        // Renderizar cajas
        for (Caja caja : cajas) {
            caja.render(batch);
        }
    }

    public boolean verificarVictoria() {
        if (completado) {
            return true;
        }

        for (Caja caja : cajas) {
            if (!caja.estaEnObjetivo()) {
                return false;
            }
        }

        completado = true;
        return true;
    }

    public boolean esPared(int x, int y) {
        if (x < 0 || x >= COLUMNAS || y < 0 || y >= FILAS) {
            return true;
        }
        return mapa[y][x] == 1;
    }

    public boolean hayCajaEn(int x, int y) {
        for (Caja caja : cajas) {
            if (caja.getX() == x && caja.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void moverCaja(int xActual, int yActual, int nuevoX, int nuevoY) {
        for (Caja caja : cajas) {
            if (caja.getX() == xActual && caja.getY() == yActual) {
                caja.mover(nuevoX, nuevoY);
                break;
            }
        }
    }

    public void actualizar(float delta) {
        for (Caja caja : cajas) {
            caja.actualizar(delta);
        }
    }

    public void actualizarAnimacionCajas(float delta) {
        for (Caja caja : cajas) {
            caja.actualizar(delta);
        }
    }

    public void dispose() {
        for (Pared pared : paredes) {
            pared.dispose();
        }
        for (Suelo suelo : suelos) {
            suelo.dispose();
        }
        for (Caja caja : cajas) {
            caja.dispose();
        }
        for (Objetivo obj : objetivos) {
            obj.dispose();
        }
    }

    // Getters
    public int getSpawnJugadorX() {
        return spawnJugadorX;
    }

    public int getSpawnJugadorY() {
        return spawnJugadorY;
    }

    public List<Caja> getCajas() {
        return cajas;
    }

    public List<Objetivo> getObjetivos() {
        return objetivos;
    }

    public List<Pared> getParedes() {
        return paredes;
    }
}
