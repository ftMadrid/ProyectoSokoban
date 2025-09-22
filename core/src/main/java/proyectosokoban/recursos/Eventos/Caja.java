package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;

public class Caja {

    private int x, y;
    private Texture textura;
    private Texture texturaEnObjetivo;
    private boolean enObjetivo = false;

    private float visualX, visualY;
    private float startX, startY;
    private boolean isMoving = false;
    private float moveTimer = 0f;
    private static final float MOVE_DURATION = 0.5f;
    private int TILE;

    private Sound sonidoObjetivo;

    public Caja(int x, int y, int TILE) {
        this.x = x;
        this.y = y;
        this.TILE = TILE;
        this.visualX = x * TILE;
        this.visualY = y * TILE;
        this.textura = new Texture("Juego/caja.png");
        this.texturaEnObjetivo = new Texture("Juego/caja.png");

        FileHandle soundFile = Gdx.files.internal("Juego/audios/caja_objetivo.mp3");
        if (soundFile.exists()) {
            sonidoObjetivo = Gdx.audio.newSound(soundFile);
        }
    }

    public void render(SpriteBatch batch, int TILE) {
        if (enObjetivo) {
            batch.setColor(Color.GREEN);
        } else {
            batch.setColor(Color.WHITE);
        }
        batch.draw(textura, visualX, visualY, TILE, TILE);
        batch.setColor(Color.WHITE);
    }

    public void mover(int nuevoX, int nuevoY) {
        this.x = nuevoX;
        this.y = nuevoY;
        this.startX = visualX;
        this.startY = visualY;
        isMoving = true;
        moveTimer = 0f;
    }

    public void actualizar(float delta) {
        if (isMoving) {
            moveTimer += delta;
            float progress = Math.min(1f, moveTimer / MOVE_DURATION);
            visualX = Interpolation.linear.apply(startX, x * TILE, progress);
            visualY = Interpolation.linear.apply(startY, y * TILE, progress);

            if (progress >= 1f) {
                isMoving = false;
                visualX = x * TILE;
                visualY = y * TILE;
            }
        }
    }

    public void setEnObjetivo(boolean enObjetivo, float volume) {
        boolean estabaEnObjetivo = this.enObjetivo;
        this.enObjetivo = enObjetivo;

        if (enObjetivo && !estabaEnObjetivo && sonidoObjetivo != null) {
            sonidoObjetivo.play(volume);
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
        if(sonidoObjetivo != null) {
            sonidoObjetivo.dispose();
        }
    }
}