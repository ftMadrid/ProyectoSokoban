package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import java.util.concurrent.atomic.AtomicBoolean;

public class Caja {

    private Texture textura;
    private volatile int x, y;
    private volatile float renderX, renderY;

    // Variables para la animación
    private final AtomicBoolean estaMoviendose = new AtomicBoolean(false);
    private volatile float tiempoAnimacion = 0f;
    private final float duracionAnimacion = 0.7f;
    private volatile float startX, startY, targetX, targetY;

    private int TILE;
    private boolean enObjetivo = false;

    private Objetivo objetivo; // referencia al objetivo correspondiente

    public Caja(int x, int y, int tile) {
        this.x = x;
        this.y = y;
        this.TILE = tile;
        this.renderX = x * TILE;
        this.renderY = y * TILE;
        this.textura = new Texture("caja.png");
    }

    public void mover(int nuevoX, int nuevoY) {
        this.x = nuevoX;
        this.y = nuevoY;

        startX = renderX;
        startY = renderY;
        targetX = x * TILE;
        targetY = y * TILE;

        estaMoviendose.set(true);
        tiempoAnimacion = 0f;
    }

    public void actualizar(float delta) {
        if (estaMoviendose.get()) {
            tiempoAnimacion += delta;

            float progreso = Math.min(tiempoAnimacion / duracionAnimacion, 1.0f);
            progreso = 1f - (1f - progreso) * (1f - progreso);

            // Actualizar posiciones de renderizado
            renderX = com.badlogic.gdx.math.MathUtils.lerp(startX, targetX, progreso);
            renderY = com.badlogic.gdx.math.MathUtils.lerp(startY, targetY, progreso);

            // Verificar si la animación terminó
            if (progreso >= 1.0f) {
                estaMoviendose.set(false);
                renderX = targetX;
                renderY = targetY;
            }
        }
    }

    public void render(SpriteBatch batch, int TILE) {
        if (enObjetivo) {
            batch.setColor(0.5f, 1f, 0.5f, 1f);
        }

        batch.draw(textura, renderX, renderY, TILE, TILE);
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }

    // Getters y setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean estaEnObjetivo() {
        return enObjetivo;
    }
}
