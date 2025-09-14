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

                FILAS = 12;
                COLUMNAS = 16;
                TILE = 58;

                mapa = new String[][]{
                    {" ", " ", " ", " ", " ", " ", " ", " ", " ", "#", "#", "#", "#", "#", "#", " "},
                    {" ", " ", " ", " ", " ", " ", " ", " ", " ", "#", " ", " ", " ", " ", "#", " "},
                    {" ", " ", " ", " ", " ", " ", " ", "#", "#", "#", "C", "#", " ", " ", "#", " "},
                    {"#", "#", "#", "#", "#", "#", "#", "#", " ", " ", "C", "C", " ", "#", "#", " "},
                    {"#", "O", "O", "O", "O", " ", "#", "#", "C", " ", "C", " ", " ", "P", "#", " "},
                    {"#", "O", "O", "O", "O", " ", " ", " ", "C", "C", " ", "C", " ", "#", "#", " "},
                    {"#", "O", "O", "O", "O", " ", "#", "#", " ", "C", " ", "C", " ", "#", "#", "#"},
                    {"#", "#", "#", "#", "#", "#", "#", "#", " ", "#", "#", "#", " ", " ", " ", "#"},
                    {" ", " ", " ", " ", " ", " ", " ", "#", " ", " ", " ", " ", " ", "C", " ", "#"},
                    {" ", " ", " ", " ", " ", " ", " ", "#", " ", "#", "C", "#", "#", " ", " ", "#"},
                    {" ", " ", " ", " ", " ", " ", " ", "#", " ", " ", " ", "#", "#", "#", "#", "#"},
                    {" ", " ", " ", " ", " ", " ", " ", "#", "#", "#", "#", "#", " ", " ", " ", " "}
                };

                cargarMapa(mapa, TILE);
                break;

            case 2:
                FILAS = 16;
                COLUMNAS = 17;
                TILE = 45;

                mapa = new String[][]{
                    {"#", "#", "#", "#", "#", "#", "#", "#", "#", " ", " ", " ", " ", " ", " ", " ", " "},
                    {"#", "O", "O", "O", "O", " ", " ", " ", "#", " ", " ", " ", " ", " ", " ", " ", " "},
                    {"#", "O", "O", "O", "O", " ", " ", " ", "#", " ", " ", " ", " ", " ", " ", " ", " "},
                    {"#", "O", "#", "O", "O", "O", " ", "#", "#", " ", " ", " ", " ", " ", " ", " ", " "},
                    {"#", "O", "O", "O", "O", "#", " ", " ", "#", "#", "#", "#", "#", "#", "#", "#", " "},
                    {"#", "#", "#", "#", "#", "#", "C", "C", " ", " ", " ", "#", " ", " ", " ", "#", " "},
                    {" ", " ", " ", "#", " ", " ", " ", " ", "#", "#", " ", "C", "C", "#", " ", "#", " "},
                    {" ", " ", " ", "#", " ", " ", "#", " ", " ", "#", "#", " ", " ", "C", " ", "#", " "},
                    {" ", " ", " ", "#", "#", " ", "C", " ", "C", " ", " ", " ", "#", "#", " ", "#", " "},
                    {" ", " ", " ", "#", " ", " ", " ", "#", " ", "C", " ", " ", " ", "#", " ", "#", " "},
                    {" ", " ", " ", "#", " ", " ", " ", "C", " ", "#", "C", " ", " ", "#", " ", "#", " "},
                    {" ", " ", " ", "#", "#", " ", "#", "#", " ", "#", " ", "C", " ", "#", " ", "#", " "},
                    {" ", " ", " ", " ", "#", "C", " ", "#", "C", "#", " ", " ", "#", "#", " ", "#", "#"},
                    {" ", " ", " ", " ", "#", " ", " ", "#", " ", "#", "C", "C", " ", "C", " ", " ", "#"},
                    {" ", " ", " ", " ", "#", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", "P", "#"},
                    {" ", " ", " ", " ", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", " "}};

                cargarMapa(mapa, TILE);
                break;

            case 3:

                FILAS = 15;
                COLUMNAS = 16;
                TILE = 50;

                mapa = new String[][]{
                    {"#", "#", "#", "#", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "},
                    {"#", " ", " ", "#", " ", " ", " ", " ", "#", "#", "#", "#", " ", " ", " ", " "},
                    {"#", " ", " ", " ", "#", "#", "#", "#", "#", " ", " ", "#", "#", "#", " ", " "},
                    {"#", " ", " ", " ", "#", " ", "C", " ", "C", "C", " ", " ", " ", "#", " ", " "},
                    {"#", " ", "C", "C", " ", " ", " ", "C", " ", " ", " ", " ", "C", "#", "#", "#"},
                    {"#", " ", " ", " ", "C", " ", " ", "C", " ", " ", " ", " ", " ", "C", " ", "#"},
                    {"#", " ", " ", " ", "#", " ", "#", "C", " ", " ", "C", " ", " ", " ", "P", "#"},
                    {"#", " ", " ", "#", "#", " ", "#", " ", "#", "#", "#", " ", "#", "#", "#", "#"},
                    {"#", " ", "#", "#", " ", " ", " ", " ", "#", " ", " ", "C", " ", " ", "#", " "},
                    {"#", "#", "#", " ", " ", " ", " ", " ", "#", " ", " ", " ", " ", " ", "#", " "},
                    {"#", "#", "O", "O", "O", "O", " ", "#", "#", " ", " ", " ", "C", " ", "#", " "},
                    {"#", "O", "O", "O", "O", "O", "#", "#", " ", "C", " ", " ", "C", "#", " ", " "},
                    {"#", "O", "O", "O", "O", "#", "#", "#", " ", " ", "C", " ", " ", "#", " ", " "},
                    {"#", "O", "O", "O", "O", "#", " ", "#", " ", " ", "#", "#", "#", "#", " ", " "},
                    {"#", "#", "#", "#", "#", "#", " ", "#", "#", "#", "#", " ", " ", " ", " ", " "}};

                cargarMapa(mapa, TILE);

                break;
            case 4:
                
                FILAS = 16;
                COLUMNAS = 18;
                TILE = 48;
                
                mapa = new String[][]{
                    {" ", " ", " ", " ", " ", "#", "#", "#", "#", "#", " ", " ", " ", " ", " ", " ", " ", " "},
                    {"#", "#", "#", "#", "#", "#", " ", " ", " ", "#", " ", " ", " ", " ", " ", " ", " ", " "},
                    {"#", " ", " ", "#", " ", " ", "C", " ", " ", "#", " ", " ", " ", " ", " ", " ", " ", " "},
                    {"#", " ", " ", "C", " ", " ", "#", " ", " ", "#", " ", " ", " ", " ", " ", " ", " ", " "},
                    {"#", " ", " ", " ", "#", "#", "#", " ", "C", "#", "#", "#", "#", "#", "#", " ", " ", " "},
                    {"#", "#", " ", " ", "#", "O", "O", "O", "O", "O", " ", "#", " ", " ", "#", " ", " ", " "},
                    {" ", "#", " ", " ", "C", "O", "O", "O", "O", "O", "C", " ", " ", " ", "#", "#", "#", "#"},
                    {" ", "#", " ", "C", "#", "O", "O", "O", "O", "O", "#", "C", "#", " ", "C", " ", " ", "#"},
                    {" ", "#", "#", " ", "#", "#", "#", "#", "P", "#", "#", " ", "#", " ", " ", " ", " ", "#"},
                    {" ", "#", " ", " ", "#", " ", " ", " ", " ", " ", " ", " ", "C", " ", "#", "#", "#", "#"},
                    {" ", "#", " ", " ", "#", "#", " ", "C", " ", "C", "#", "#", " ", " ", "#", " ", " ", " "},
                    {" ", "#", " ", " ", "#", " ", " ", "#", "#", " ", "C", " ", " ", " ", "#", " ", " ", " "},
                    {" ", "#", " ", "C", " ", " ", " ", " ", " ", " ", "#", "#", "#", "#", "#", " ", " ", " "},
                    {" ", " ", "#", " ", " ", "C", "#", "C", " ", " ", " ", "#", " ", " ", " ", " ", " ", " "},
                    {" ", " ", "#", " ", "#", " ", " ", " ", " ", " ", " ", "#", " ", " ", " ", " ", " ", " "},
                    {" ", " ", " ", "#", "#", "#", "#", "#", "#", "#", "#", "#", " ", " ", " ", " ", " ", " "}
                };
                
                cargarMapa(mapa, TILE);
                break;
                
            case 5:
                
                FILAS = 15;
                COLUMNAS = 19;
                TILE = 50;
                
                mapa = new String[][]{
                    {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", " ", " ", " ", "#", "#", "#", "#", "#", "#"},
                    {"#", " ", " ", " ", " ", " ", " ", " ", " ", "#", " ", " ", " ", "#", " ", " ", " ", "#", "#"},
                    {"#", " ", "#", "#", "#", "#", "#", "#", "C", "#", "#", "#", "#", "#", " ", "C", " ", "#", "#"},
                    {"#", " ", "#", " ", " ", " ", "#", "#", " ", "#", "#", " ", " ", " ", " ", "#", "#", "#", "#"},
                    {"#", " ", "#", " ", " ", " ", " ", "C", " ", " ", " ", " ", " ", "#", " ", " ", " ", "#", " "},
                    {"#", " ", "#", "#", "#", "#", " ", "#", "C", "C", "C", "C", "C", " ", " ", " ", " ", "#", " "},
                    {"#", " ", " ", " ", " ", " ", " ", "#", " ", " ", " ", " ", "#", "C", "#", "C", "#", "#", "#"},
                    {"#", " ", " ", "C", " ", "#", "C", "C", "C", "#", "#", " ", " ", " ", " ", " ", " ", " ", "#"},
                    {"#", "#", "C", " ", "#", " ", "C", " ", " ", " ", "#", "#", "#", "#", "#", "#", " ", " ", "#"},
                    {"#", " ", "C", " ", "#", " ", " ", " ", " ", " ", "#", "#", "#", "#", "#", "#", " ", "P", "#"},
                    {"#", " ", " ", " ", "#", "#", "C", "#", "#", "#", "#", "#", "#", " ", " ", "#", "#", "#", "#"},
                    {"#", "#", "C", "#", " ", " ", " ", " ", "O", "O", "O", "O", "O", "O", "O", "O", "#", " ", " "},
                    {"#", " ", " ", "#", " ", "#", " ", "#", "#", "#", "#", "#", " ", "O", "O", "O", "#", " ", " "},
                    {"#", " ", " ", "#", " ", " ", " ", "O", "O", "#", "O", "O", "O", "O", "O", "O", "#", " ", " "},
                    {"#", " ", " ", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", " ", " "},
                    {"#", "#", "#", "#", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}
                };
                
                cargarMapa(mapa, TILE);
                break;
                
            case 6:
                
                FILAS = 16;
                COLUMNAS = 19;
                TILE = 45;
                
                mapa = new String[][]{
                    {" ", " ", " ", " ", "#", "#", "#", "#", "#", "#", "#", " ", " ", " ", " ", " ", " ", " ", " "},
                    {" ", " ", " ", " ", "#", " ", " ", " ", " ", " ", "#", "#", " ", " ", " ", " ", " ", " ", " "},
                    {" ", " ", " ", " ", "#", "C", " ", " ", "C", " ", " ", "#", "#", "#", "#", "#", "#", "#", "#"},
                    {"#", "#", "#", "#", "#", " ", "C", "#", "#", "#", "C", " ", " ", "#", "O", "O", "O", "O", "#"},
                    {"#", " ", " ", " ", " ", " ", "C", "#", " ", " ", " ", "C", "C", "#", "O", " ", "#", "O", "#"},
                    {"#", " ", "#", "C", " ", " ", " ", "C", " ", " ", "C", " ", " ", "#", "C", " ", "O", "O", "#"},
                    {"#", " ", "#", " ", " ", " ", " ", "#", "C", " ", "#", "#", "#", "#", "O", " ", "#", "O", "#"},
                    {"#", " ", "#", "#", "#", " ", " ", "#", " ", " ", "#", " ", " ", "#", "C", " ", "O", "O", "#"},
                    {"#", " ", "#", " ", " ", "#", "#", "#", " ", "#", "#", " ", " ", " ", " ", " ", "#", "O", "#"},
                    {"#", " ", "#", " ", "C", " ", " ", "#", " ", " ", " ", " ", " ", "#", "C", " ", "O", "O", "#"},
                    {"#", " ", "#", " ", "C", " ", " ", "C", " ", " ", "#", " ", " ", "#", "C", " ", "#", "O", "#"},
                    {"#", " ", "#", " ", " ", " ", " ", "#", "#", " ", "#", " ", " ", "#", "C", " ", "O", "O", "#"},
                    {"#", " ", "#", "#", "#", "#", "#", "#", "#", "C", "#", "#", "#", "#", "C", " ", "#", "#", "#"},
                    {"#", " ", " ", " ", " ", " ", "#", " ", " ", " ", "C", " ", " ", "#", " ", "P", "#", " ", " "},
                    {"#", "#", "#", "#", "#", " ", " ", " ", " ", " ", " ", " ", " ", "#", "#", "#", "#", " ", " "},
                    {" ", " ", " ", " ", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", " ", " ", " ", " ", " "},
                };
                
                cargarMapa(mapa, TILE);
                break;
                
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
                        case 3:
                            paredes.add(new Pared(x, y, "pared.png"));
                            break;
                        case 4:
                            paredes.add(new Pared(x, y, "pared.png"));
                            break;
                        case 5:
                            paredes.add(new Pared(x, y, "pared.png"));
                            break;
                        case 6:
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

    public void cargarMapa(String[][] mapa, int TILE) {
        cajas.clear();
        objetivos.clear();

        for (int y = 0; y < mapa.length; y++) {
            for (int x = 0; x < mapa[y].length; x++) {
                String celda = mapa[y][x];

                switch (celda) {
                    case "C":
                        cajas.add(new Caja(x, y, TILE));
                        break;
                    case "O":
                        objetivos.add(new Objetivo(x, y));
                        break;
                    case "P":
                        spawnJugadorX = x;
                        spawnJugadorY = y;
                        break;
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
