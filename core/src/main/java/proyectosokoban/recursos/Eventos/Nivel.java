package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import proyectosokoban.recursos.Eventos.Pared;
import proyectosokoban.recursos.Eventos.Suelo;
import proyectosokoban.recursos.Eventos.Caja;
import proyectosokoban.recursos.Eventos.Objetivo;
import java.util.ArrayList;
import java.util.List;

public class Nivel {

    private int numero;
    private int[][] mapa;
    private List<Pared> paredes;
    private List<Suelo> suelos;
    private List<Caja> cajas;
    private Objetivo objetivo;
    private boolean completado = false;

    private final int TILE = 90;
    private final int FILAS = 8;
    private final int COLUMNAS = 12;

    public Nivel(int numero) {
        this.numero = numero;
        inicializar();
    }

    private void inicializar() {
        // Inicializar el nivel según el número

        switch (numero) {
            case 1: // Nivel 1
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
                break;
            case 2: // Nivel 2
            case 3: // Nivel 3
            case 4: // Nivel 4
            case 5: // Nivel 5
            case 6: // Nivel 6
            case 7: // Nivel 7
            default:
                System.out.println("Nada");
                break;
        }

        // Crear entidades basadas en el mapa
        paredes = new ArrayList<>();
        suelos = new ArrayList<>();
        cajas = new ArrayList<>();

        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                if (mapa[y][x] == 1) {
                    paredes.add(new Pared(x, y));
                } else {
                    suelos.add(new Suelo(x, y));
                }
            }
        }

        // Añadir caja y objetivo (estos vendrían definidos en el nivel)
        cajas.add(new Caja(4, 6, 8, 6));
        objetivo = new Objetivo(8, 6);
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
        // Las cajas se actualizan en el hilo de animación
        for (Caja caja : cajas) {
            caja.actualizar(delta);
        }
    }

    public void render() {
        SpriteBatch batch = new SpriteBatch();
        batch.begin();

        // Renderizar suelos
        for (Suelo suelo : suelos) {
            suelo.render(batch, TILE);
        }

        // Renderizar paredes
        for (Pared pared : paredes) {
            pared.render(batch, TILE);
        }

        // Renderizar objetivo
        objetivo.render(batch, TILE);

        // Renderizar cajas
        for (Caja caja : cajas) {
            caja.render(batch);
        }

        batch.end();
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
        objetivo.dispose();
    }

    // Getters...
    public int getPosicionJugadorX() {
        return 2;
    }

    public int getPosicionJugadorY() {
        return 2;
    }

    public List<Pared> getParedes() {
        return paredes;
    }

    public List<Caja> getCajas() {
        return cajas;
    }
}
