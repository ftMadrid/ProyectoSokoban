package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

public class Nivel {

    private int numero;
    private String[][] mapa;
    private List<Pared> paredes;
    private List<Suelo> suelos;
    private List<Caja> cajas;
    private List<Objetivo> objetivos;
    private boolean completado = false;

    private int spawnJugadorX;
    private int spawnJugadorY;

    private int TILE;
    private int FILAS;
    private int COLUMNAS;

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

                FILAS = 16;
                COLUMNAS = 12;
                TILE = 48;

                mapa = new String[][]{
                    {" ", " ", " ", " ", " ", " ", "#", "#", "#", "#", "#", " "},
                    {"#", "#", "#", "#", "#", "#", "#", " ", " ", " ", "#", " "},
                    {"#", " ", " ", "#", " ", "#", "#", " ", "C", " ", "#", " "},
                    {"#", " ", " ", " ", " ", " ", " ", " ", " ", "#", "#", " "},
                    {"#", " ", "#", "C", " ", "C", "C", "#", " ", "#", "#", "#"},
                    {"#", " ", "C", "C", "C", " ", " ", "#", " ", "C", " ", "#"},
                    {"#", "#", "#", " ", " ", "C", "C", "#", " ", "#", " ", "#"},
                    {" ", " ", "#", " ", "C", "C", " ", " ", " ", " ", " ", "#"},
                    {" ", " ", "#", "#", "#", " ", "#", "#", "#", "#", "#", "#"},
                    {" ", " ", " ", "#", "#", " ", "#", "#", " ", " ", " ", " "},
                    {" ", " ", " ", "#", " ", " ", " ", "#", " ", " ", " ", " "},
                    {" ", " ", " ", "#", " ", " ", " ", "#", " ", " ", " ", " "},
                    {" ", " ", " ", "#", " ", " ", " ", "#", " ", " ", " ", " "},
                    {" ", " ", " ", "#", " ", " ", " ", "#", " ", " ", " ", " "},
                    {" ", " ", " ", "#", " ", " ", " ", "#", " ", " ", " ", " "},
                    {" ", " ", " ", "#", "#", "#", "#", "#", " ", " ", " ", " "}
                };
                spawnJugadorX = 4;
                spawnJugadorY = 2;

                // Cajas
                cajas.add(new Caja(8, 2, TILE));
                cajas.add(new Caja(3, 4, TILE));
                cajas.add(new Caja(5, 4, TILE));
                cajas.add(new Caja(6, 4, TILE));
                cajas.add(new Caja(2, 5, TILE));
                cajas.add(new Caja(3, 5, TILE));
                cajas.add(new Caja(4, 5, TILE));
                cajas.add(new Caja(9, 5, TILE));
                cajas.add(new Caja(5, 6, TILE));
                cajas.add(new Caja(6, 6, TILE));
                cajas.add(new Caja(4, 7, TILE));
                cajas.add(new Caja(5, 7, TILE));

                // Objetivos
                objetivos.add(new Objetivo(4, 11));
                objetivos.add(new Objetivo(4, 12));
                objetivos.add(new Objetivo(4, 13));
                objetivos.add(new Objetivo(4, 14));
                objetivos.add(new Objetivo(5, 11));
                objetivos.add(new Objetivo(5, 12));
                objetivos.add(new Objetivo(5, 13));
                objetivos.add(new Objetivo(5, 14));
                objetivos.add(new Objetivo(6, 11));
                objetivos.add(new Objetivo(6, 12));
                objetivos.add(new Objetivo(6, 13));
                objetivos.add(new Objetivo(6, 14));
                break;

            case 2:
                FILAS = 6;
                COLUMNAS = 6;
                TILE = 60;

                mapa = new String[][]{
                    {"#", "#", "#", "#", "#", "#"},
                    {"#", " ", " ", " ", " ", "#"},
                    {"#", " ", " ", " ", " ", "#"},
                    {"#", " ", " ", " ", " ", "#"},
                    {"#", " ", " ", " ", " ", "#"},
                    {"#", "#", "#", "#", "#", "#"},};

                spawnJugadorX = 1;
                spawnJugadorY = 1;

                cajas.add(new Caja(2, 2, TILE));
                cajas.add(new Caja(2, 3, TILE));

                objetivos.add(new Objetivo(4, 2));
                objetivos.add(new Objetivo(4, 3));
                break;

            // Agregar más niveles con sus cajas, objetivos y spawn
            default:
                System.out.println("Nivel no definido");
                return;
        }

        // Crear paredes y suelos
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                if (mapa[y][x].equals("#")) {
                    switch (numero) {
                        case 1:
                            paredes.add(new Pared(x, y, "pared.png"));
                            break;
                        case 2:
                            paredes.add(new Pared(x, y, "pared.png"));
                            break;
                        default:
                            break;
                    }
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
            caja.render(batch, TILE);
        }

    }

    public boolean verificarVictoria() {
        if (completado) {
            return true;
        }

        // Para cada objetivo, debe haber alguna caja encima
        for (Objetivo obj : objetivos) {
            boolean encontrado = false;
            for (Caja caja : cajas) {
                if (caja.getX() == obj.getX() && caja.getY() == obj.getY()) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                return false; // Al menos un objetivo no tiene caja
            }
        }

        completado = true;
        return true;
    }

    public boolean esPared(int x, int y) {
        if (x < 0 || x >= COLUMNAS || y < 0 || y >= FILAS) {
            return true;
        }
        return mapa[y][x].equals("#");
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
    public int getTILE() {
        return TILE;
    }

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
