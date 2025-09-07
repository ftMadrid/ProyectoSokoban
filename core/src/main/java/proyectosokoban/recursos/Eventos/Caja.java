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
    private final float duracionAnimacion = 0.3f;
    private volatile float startX, startY, targetX, targetY;

    private final int TILE = 90;
    private int objetivoX, objetivoY;
    private boolean enObjetivo = false;

    public Caja(int x, int y, int objetivoX, int objetivoY) {
        this.x = x;
        this.y = y;
        this.renderX = x * TILE;
        this.renderY = y * TILE;
        this.objetivoX = objetivoX;
        this.objetivoY = objetivoY;
        this.textura = new Texture("caja.png");
        verificarObjetivo();
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

        verificarObjetivo();
    }

    private void verificarObjetivo() {
        enObjetivo = (x == objetivoX && y == objetivoY);
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

    public void render(SpriteBatch batch) {
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
        verificarObjetivo();
    }

    public void setY(int y) {
        this.y = y;
        verificarObjetivo();
    }

    public boolean estaEnObjetivo() {
        return enObjetivo;
    }
}
