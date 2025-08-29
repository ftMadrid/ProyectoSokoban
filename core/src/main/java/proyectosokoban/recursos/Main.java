package proyectosokoban.recursos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture jugadorTex, cajaTex, sueloTex, paredTex;
    private Music musicafondo;
    private Sound audiomove;

    private final int TILE = 100; // size de cada celda en pixeles
    private final int FILAS = 8;
    private final int COLUMNAS = 10;

    // 0 = suelo, 1 = pared
    private int[][] mapa = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 1, 1, 1, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 1, 0, 1},
        {1, 0, 0, 1, 1, 1, 0, 0, 0, 1},
        {1, 0, 0, 1, 0, 1, 1, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},};

    private int jugadorX = 2, jugadorY = 2;
    private int cajaX = 4, cajaY = 6;

    private float tiempoDesdeUltimoMovimiento = 0f;
    private final float delayMovimiento = 0.2f; // segundos

    @Override
    public void create() {

        musicafondo = Gdx.audio.newMusic(Gdx.files.internal("audiofondo.mp3"));
        musicafondo.setLooping(true);
        musicafondo.setVolume(0.5f);

        audiomove = Gdx.audio.newSound(Gdx.files.internal("movimiento.mp3"));

        musicafondo.play();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        jugadorTex = new Texture("muneco.png");
        cajaTex = new Texture("caja.png");
        sueloTex = new Texture("suelo.png");
        paredTex = new Texture("pared.png");
    }

    private void moverJugador(int dx, int dy) {
        int nuevoX = jugadorX + dx;
        int nuevoY = jugadorY + dy;

        // es pared?
        if (mapa[nuevoY][nuevoX] == 1) {
            return;
        }

        // hay caja?
        if (nuevoX == cajaX && nuevoY == cajaY) {
            int nuevoCajaX = cajaX + dx;
            int nuevoCajaY = cajaY + dy;

            // chequeo de celda libre
            if (mapa[nuevoCajaY][nuevoCajaX] == 0) {
                cajaX = nuevoCajaX;
                cajaY = nuevoCajaY;
                jugadorX = nuevoX;
                jugadorY = nuevoY;
            }
        } else {
            jugadorX = nuevoX;
            jugadorY = nuevoY;
        }
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();
        tiempoDesdeUltimoMovimiento += delta;

        if (tiempoDesdeUltimoMovimiento >= delayMovimiento) {

            boolean seMovio = false;

            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                moverJugador(1, 0);
                seMovio = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                moverJugador(-1, 0);
                seMovio = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                moverJugador(0, 1);
                seMovio = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                moverJugador(0, -1);
                seMovio = true;
            }

            if (seMovio) {
                audiomove.play(0.6f);
                tiempoDesdeUltimoMovimiento = 0f;
            }

        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // dibujamos el mapa y sprites
        batch.begin();
        for (int y = 0; y < FILAS; y++) {
            for (int x = 0; x < COLUMNAS; x++) {
                if (mapa[y][x] == 1) {
                    batch.draw(paredTex, x * TILE, y * TILE, TILE, TILE);
                } else {
                    batch.draw(sueloTex, x * TILE, y * TILE, TILE, TILE);
                }
            }
        }

        // dimensiones del player
        int jugadorWidth = 64;
        int jugadorHeight = 80;

        // centrar player
        float offsetX = (TILE - jugadorWidth) / 2f;
        float offsetY = (TILE - jugadorHeight) / 2f;

        batch.draw(jugadorTex,
                jugadorX * TILE + offsetX,
                jugadorY * TILE + offsetY,
                jugadorWidth,
                jugadorHeight
        );

        batch.draw(cajaTex, cajaX * TILE, cajaY * TILE, TILE, TILE);
        batch.end();

        // interlineados
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                shapeRenderer.rect(col * TILE, fila * TILE, TILE, TILE);
            }
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        jugadorTex.dispose();
        cajaTex.dispose();
        sueloTex.dispose();
        paredTex.dispose();
    }
}
