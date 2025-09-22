package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

public class Caja {

    private int x, y;
    private Texture textura;
    private Texture texturaEnObjetivo;
    private boolean enObjetivo = false;

    private float visualX, visualY;
    private boolean isMoving = false;
    private float moveTimer = 0f;
    private static final float MOVE_DURATION = 0.2f; // Duración de la animación en segundos

    private static Sound sonidoObjetivo; // Estático para cargar el sonido una sola vez
    private boolean sonidoReproducido = false;

    public Caja(int x, int y, int TILE) {
        this.x = x;
        this.y = y;
        this.visualX = x * TILE;
        this.visualY = y * TILE;
        this.textura = new Texture("Juego/caja.png");
        this.texturaEnObjetivo = new Texture("Juego/caja_objetivo.png");

        // Carga el sonido una sola vez para todas las cajas
        if (sonidoObjetivo == null) {
            try {
                sonidoObjetivo = Gdx.audio.newSound(Gdx.files.internal("Juego/audios/correct.mp3"));
            } catch (Exception e) {
                Gdx.app.error("Caja", "No se pudo cargar el sonido 'correct.mp3'", e);
                sonidoObjetivo = null;
            }
        }
    }

    public void render(SpriteBatch batch, int TILE) {
        batch.draw(enObjetivo ? texturaEnObjetivo : textura, visualX, visualY, TILE, TILE);
    }

    public void mover(int nuevoX, int nuevoY) {
        this.x = nuevoX;
        this.y = nuevoY;
        isMoving = true;
        moveTimer = 0f;
    }

    public void actualizar(float delta) {
        if (isMoving) {
            moveTimer += delta;
            float progress = Math.min(1f, moveTimer / MOVE_DURATION);
            visualX = Interpolation.linear.apply(visualX, x * 64, progress);
            visualY = Interpolation.linear.apply(visualY, y * 64, progress);

            if (progress >= 1f) {
                isMoving = false;
                visualX = x * 64;
                visualY = y * 64;
            }
        }
    }

    /**
     * Actualiza el estado de la caja y reproduce un sonido si llega a un objetivo.
     * @param enObjetivo True si la caja está en un objetivo.
     * @param volume El volumen al que se debe reproducir el sonido.
     */
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

    public void dispose() {
        textura.dispose();
        texturaEnObjetivo.dispose();
        // El sonido estático no se libera aquí, sino al cerrar el juego si es necesario.
    }
}