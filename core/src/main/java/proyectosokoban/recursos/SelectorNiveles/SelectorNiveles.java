package proyectosokoban.recursos.SelectorNiveles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.concurrent.atomic.AtomicBoolean;
import com.badlogic.gdx.math.MathUtils;

public class SelectorNiveles {

    private Texture[] texturasNormales;
    private Texture[][] texturasMovimiento;

    private volatile int x, y;
    private volatile float renderX, renderY;
    private volatile int direccion;

    private final AtomicBoolean estaMoviendose = new AtomicBoolean(false);
    private volatile float tiempoAnimacion = 0f;
    private final float duracionAnimacion = 0.3f;
    private volatile float startX, startY, targetX, targetY;

    private volatile float tiempoAnimacionMovimiento = 0f;
    private volatile int frameActualMovimiento = 0;

    private int TILE;
    private MapaSelector mapa;

    public SelectorNiveles(int x, int y, int TILE, MapaSelector mapa) {
        this.x = x;
        this.y = y;
        this.TILE = TILE;
        this.mapa = mapa;
        this.renderX = x * TILE;
        this.renderY = y * TILE;
        this.direccion = 0;
        cargarTexturas();
    }

    private void cargarTexturas() {
        texturasNormales = new Texture[4];
        texturasNormales[0] = new Texture("Juego/muneco/south.png");
        texturasNormales[1] = new Texture("Juego/muneco/north.png");
        texturasNormales[2] = new Texture("Juego/muneco/west.png"); 
        texturasNormales[3] = new Texture("Juego/muneco/east.png"); 

        texturasMovimiento = new Texture[4][4];
        for (int i = 0; i < 4; i++) {
            texturasMovimiento[0][i] = new Texture("Juego/muneco/moves/south_00" + i + ".png");
            texturasMovimiento[1][i] = new Texture("Juego/muneco/moves/north_00" + i + ".png");
            texturasMovimiento[2][i] = new Texture("Juego/muneco/moves/west_00" + i + ".png");
            texturasMovimiento[3][i] = new Texture("Juego/muneco/moves/east_00" + i + ".png");
        }
    }

    public boolean mover(int dx, int dy) {
        if (estaMoviendose.get()) {
            return false;
        }

        int nuevoX = x + dx;
        int nuevoY = y + dy;

        // verificar si la nueva posicion es valida
        if (!mapa.esPosicionValida(nuevoX, nuevoY)) {
            return false;
        }

        // actualizar direccion SISISJDISHDJISD
        if (dx > 0) {
            direccion = 3;
        } else if (dx < 0) {
            direccion = 2;
        } else if (dy > 0) {
            direccion = 1;
        } else if (dy < 0) {
            direccion = 0;
        }

        x = nuevoX;
        y = nuevoY;

        startX = renderX;
        startY = renderY;
        targetX = x * TILE;
        targetY = y * TILE;

        estaMoviendose.set(true);
        tiempoAnimacion = 0f;
        frameActualMovimiento = 0;
        tiempoAnimacionMovimiento = 0f;

        return true;
    }

    public void actualizar(float delta) {
        if (estaMoviendose.get()) {
            tiempoAnimacion += delta;
            float progreso = Math.min(tiempoAnimacion / duracionAnimacion, 1f);
            progreso = 1f - (1f - progreso) * (1f - progreso);
            renderX = MathUtils.lerp(startX, targetX, progreso);
            renderY = MathUtils.lerp(startY, targetY, progreso);

            tiempoAnimacionMovimiento += delta;
            if (tiempoAnimacionMovimiento >= duracionAnimacion / 4f) {
                frameActualMovimiento = (frameActualMovimiento + 1) % texturasMovimiento[direccion].length;
                tiempoAnimacionMovimiento = 0f;
            }

            if (progreso >= 1f) {
                estaMoviendose.set(false);
                renderX = targetX;
                renderY = targetY;
                frameActualMovimiento = 0;
                tiempoAnimacionMovimiento = 0f;
            }
        }
    }

    private Texture getTexturaActual() {
        if (estaMoviendose.get()) {
            return texturasMovimiento[direccion][frameActualMovimiento];
        }
        return texturasNormales[direccion];
    }

    public void render(SpriteBatch batch) {
        float ancho = TILE + 20;
        float alto = TILE + 20;

        float offsetX = (TILE - ancho) / 2f;
        float offsetY = (TILE - alto) / 2f;

        batch.draw(getTexturaActual(), renderX + offsetX, renderY + offsetY, ancho, alto);
    }

    public void dispose() {
        for (Texture tex : texturasNormales) {
            if (tex != null) {
                tex.dispose();
            }
        }
        for (int d = 0; d < 4; d++) {
            for (Texture tex : texturasMovimiento[d]) {
                if (tex != null) {
                    tex.dispose();
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNivelSeleccionado() {
        return mapa.getNivelEnPosicion(x, y);
    }

    public boolean estaMoviendose() {
        return estaMoviendose.get();
    }
}
