package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;

public class GameScreen implements Screen {

    final Main main;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture jugadorTex, cajaTex, sueloTex, paredTex;
    private Music musicafondo;
    private Sound audiomove;

    private final int TILE = 100;
    private final int FILAS = 8;
    private final int COLUMNAS = 10;

    private int[][] mapa = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
        {1, 0, 1, 0, 0, 1, 1, 0, 0, 1},
        {1, 0, 1, 0, 0, 1, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    private int jugadorX = 2, jugadorY = 2;
    private int cajaX = 4, cajaY = 6;
    private float tiempoDesdeUltimoMovimiento = 0f;
    private final float delayMovimiento = 0.2f;

    // UI para volver al menú
    private Stage stage;
    private Skin skin;

    public GameScreen(final Main main) {
        this.main = main;

        // Inicializar recursos del juego
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Cargar texturas
        jugadorTex = new Texture("muneco.png");
        cajaTex = new Texture("caja.png");
        sueloTex = new Texture("suelo.png");
        paredTex = new Texture("pared.png");

        // Audio
        musicafondo = Gdx.audio.newMusic(Gdx.files.internal("audiofondo.mp3"));
        musicafondo.setLooping(true);
        musicafondo.setVolume(0.5f);
        musicafondo.play();

        audiomove = Gdx.audio.newSound(Gdx.files.internal("movimiento.mp3"));

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton botonVolver = new TextButton("VOLVER AL MENU", skin);
        botonVolver.setPosition(10, Gdx.graphics.getHeight() - 60);
        botonVolver.setSize(150, 50);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
                main.setScreen(new MenuScreen(main));
                dispose();
            }
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
            }
        });
        stage.addActor(botonVolver);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void moverJugador(int dx, int dy) {
        int nuevoX = jugadorX + dx;
        int nuevoY = jugadorY + dy;

        if (mapa[nuevoY][nuevoX] == 1) {
            return;
        }

        if (nuevoX == cajaX && nuevoY == cajaY) {
            int nuevoCajaX = cajaX + dx;
            int nuevoCajaY = cajaY + dy;

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
    public void render(float delta) {
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

        int jugadorWidth = 64;
        int jugadorHeight = 80;
        float offsetX = (TILE - jugadorWidth) / 2f;
        float offsetY = (TILE - jugadorHeight) / 2f;

        batch.draw(jugadorTex,
                jugadorX * TILE + offsetX,
                jugadorY * TILE + offsetY,
                jugadorWidth,
                jugadorHeight);

        batch.draw(cajaTex, cajaX * TILE, cajaY * TILE, TILE, TILE);
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.LIGHT_GRAY);
        for (int fila = 0; fila < FILAS; fila++) {
            for (int col = 0; col < COLUMNAS; col++) {
                shapeRenderer.rect(col * TILE, fila * TILE, TILE, TILE);
            }
        }
        shapeRenderer.end();
        stage.act(delta);
        stage.draw();

        main.batch.begin();
        main.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        jugadorTex.dispose();
        cajaTex.dispose();
        sueloTex.dispose();
        paredTex.dispose();
        musicafondo.dispose();
        audiomove.dispose();
        stage.dispose();
        skin.dispose();
    }
}
