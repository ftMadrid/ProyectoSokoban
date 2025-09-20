package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import java.util.concurrent.atomic.AtomicBoolean;

public class Caja {

    private Texture textura;
    private volatile int x, y;
    private volatile float renderX, renderY;

    private final AtomicBoolean estaMoviendose = new AtomicBoolean(false);
    private volatile float tiempoAnimacion = 0f;
    private final float duracionAnimacion = 0.7f;
    private volatile float startX, startY, targetX, targetY;

    private int TILE;
    private boolean enObjetivo = false;
    private Sound sonidoObjetivo;
    private boolean sonidoReproducido = false;

    public Caja(int x, int y, int tile) {
        this.x = x;
        this.y = y;
        this.TILE = tile;
        this.renderX = x * TILE;
        this.renderY = y * TILE;
        this.textura = new Texture("Juego/caja.png");
        
        try {
            this.sonidoObjetivo = Gdx.audio.newSound(Gdx.files.internal("Juego/audios/caja_objetivo.mp3"));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el sonido de caja en objetivo: " + e.getMessage());
            this.sonidoObjetivo = null;
        }
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
        sonidoReproducido = false;
    }

    public void actualizar(float delta) {
        if (estaMoviendose.get()) {
            tiempoAnimacion += delta;

            float progreso = Math.min(tiempoAnimacion / duracionAnimacion, 1.0f);
            progreso = 1f - (1f - progreso) * (1f - progreso);

            renderX = com.badlogic.gdx.math.MathUtils.lerp(startX, targetX, progreso);
            renderY = com.badlogic.gdx.math.MathUtils.lerp(startY, targetY, progreso);

            if (progreso >= 1.0f) {
                estaMoviendose.set(false);
                renderX = targetX;
                renderY = targetY;
            }
        }
    }

    public void render(SpriteBatch batch, int TILE) {
        if (enObjetivo) {
            batch.setColor(0.6f, 1f, 0.6f, 1f);
        }

        batch.draw(textura, renderX+2, renderY+2, TILE-5, TILE-5);
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
        if (sonidoObjetivo != null) {
            sonidoObjetivo.dispose();
        }
    }

    public void setEnObjetivo(boolean enObjetivo, float volume) {
        boolean estabaEnObjetivo = this.enObjetivo;
        this.enObjetivo = enObjetivo;
        
        if (enObjetivo && !estabaEnObjetivo && sonidoObjetivo != null && !sonidoReproducido) {
            sonidoObjetivo.play(volume);
            sonidoReproducido = true;
        }
        
        if (!enObjetivo) {
            sonidoReproducido = false;
        }
    }

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
