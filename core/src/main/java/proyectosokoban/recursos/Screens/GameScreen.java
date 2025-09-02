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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameScreen implements Screen {

    final Main main;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture jugadorTex, cajaTex, sueloTex, paredTex, objetivoTex;
    private Music musicafondo;
    private Sound audiomove, sonidoVictoria;

    private final int TILE = 100;
    private final int FILAS = 8;
    private final int COLUMNAS = 10;
    private int movimientos = 0;

    Label cantmoves;

    private int[][] mapa = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 1, 0, 0, 1},
        {1, 0, 1, 1, 1, 1, 1, 0, 0, 1},
        {1, 0, 0, 0, 1, 0, 1, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    // posiciones logicas
    private int jugadorX = 2, jugadorY = 2;
    private int cajaX = 4, cajaY = 6;

    // posiciones para las animaciones
    private float jugadorRenderX = 2 * TILE, jugadorRenderY = 2 * TILE;
    private float cajaRenderX = 4 * TILE, cajaRenderY = 6 * TILE;

    // var para la animacion pue
    private boolean estaMoviendose = false;
    private float tiempoAnimacion = 0f;
    private final float duracionAnimacion = 0.15f; // aqui seteo la duracion (estoy en testing todavia por si se buguea)

    // obtenemos la psocion de inicio para evitar errores graficos
    private float jugadorStartX, jugadorStartY, jugadorTargetX, jugadorTargetY;
    private float cajaStartX, cajaStartY, cajaTargetX, cajaTargetY;
    private boolean cajaSeMovioTambien = false;

    // contorl de la direccion
    private boolean jugadorMiraIzquierda = false; // 'true' mira izquierda y 'false' mira derecha

    // posicion donde seteo la caja
    private int objetivoX = 8, objetivoY = 6;

    private float tiempoDesdeUltimoMovimiento = 0f;
    private final float delayMovimiento = 0.2f;

    private boolean juegoGanado = false;

    private Stage stage;
    private Skin skin;

    public GameScreen(final Main main) {
        this.main = main;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        jugadorTex = new Texture("muneco.png");
        cajaTex = new Texture("caja.png");
        sueloTex = new Texture("suelo.png");
        paredTex = new Texture("pared.png");

        objetivoTex = new Texture("objetivo.png");

        musicafondo = Gdx.audio.newMusic(Gdx.files.internal("audiofondo.mp3"));
        musicafondo.setLooping(true);
        musicafondo.setVolume(0.3f);
        musicafondo.play();

        audiomove = Gdx.audio.newSound(Gdx.files.internal("movimiento.mp3"));

        try {
            sonidoVictoria = Gdx.audio.newSound(Gdx.files.internal("audiovictoria.mp3"));
        } catch (Exception e) {
            sonidoVictoria = null;
        }

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        cantmoves = new Label("Movimientos: 0", skin);
        cantmoves.setPosition(300, Gdx.graphics.getHeight() - 60);

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
        stage.addActor(cantmoves);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void moverJugador(int dx, int dy) {
        if (juegoGanado || estaMoviendose) {
            return; // bloqueamos movimientos mientras se cargan otras cosas
        }

        // actualizar sprite (pa la animacion)
        if (dx > 0) {
            jugadorMiraIzquierda = false; // ve derecha
        } else if (dx < 0) {
            jugadorMiraIzquierda = true;  // ve izquierda
        }

        int nuevoX = jugadorX + dx;
        int nuevoY = jugadorY + dy;

        if (mapa[nuevoY][nuevoX] == 1) {
            return;
        }

        // setear posiiciones de inicio
        jugadorStartX = jugadorRenderX;
        jugadorStartY = jugadorRenderY;
        cajaStartX = cajaRenderX;
        cajaStartY = cajaRenderY;
        cajaSeMovioTambien = false;

        if (nuevoX == cajaX && nuevoY == cajaY) {
            int nuevoCajaX = cajaX + dx;
            int nuevoCajaY = cajaY + dy;

            if (mapa[nuevoCajaY][nuevoCajaX] == 0) {
                // update de posiciones
                cajaX = nuevoCajaX;
                cajaY = nuevoCajaY;
                jugadorX = nuevoX;
                jugadorY = nuevoY;

                // destinos de animacion
                jugadorTargetX = jugadorX * TILE;
                jugadorTargetY = jugadorY * TILE;
                cajaTargetX = cajaX * TILE;
                cajaTargetY = cajaY * TILE;
                cajaSeMovioTambien = true;

                // ready la animacion
                estaMoviendose = true;
                tiempoAnimacion = 0f;
            }
        } else {
            jugadorX = nuevoX;
            jugadorY = nuevoY;

            // destino anima
            jugadorTargetX = jugadorX * TILE;
            jugadorTargetY = jugadorY * TILE;

            estaMoviendose = true;
            tiempoAnimacion = 0f;
        }

        movimientos++;
    }

    private void actualizarAnimacion(float delta) {
        if (estaMoviendose) {
            tiempoAnimacion += delta;

            // progreso de animacion (tengo que testear pq no se si se puede buguear el calculo?)
            float progreso = Math.min(tiempoAnimacion / duracionAnimacion, 1.0f);

            // NO TOCAR
            progreso = 1f - (1f - progreso) * (1f - progreso);

            // obtengo posiciones de la animacion
            jugadorRenderX = MathUtils.lerp(jugadorStartX, jugadorTargetX, progreso);
            jugadorRenderY = MathUtils.lerp(jugadorStartY, jugadorTargetY, progreso);

            // lo mismo de arriba pero para la caja
            if (cajaSeMovioTambien) {
                cajaRenderX = MathUtils.lerp(cajaStartX, cajaTargetX, progreso);
                cajaRenderY = MathUtils.lerp(cajaStartY, cajaTargetY, progreso);
            }

            // ver si animacion termino
            if (progreso >= 1.0f) {
                estaMoviendose = false;

                // pequeno chequeo de posiciones
                jugadorRenderX = jugadorTargetX;
                jugadorRenderY = jugadorTargetY;
                if (cajaSeMovioTambien) {
                    cajaRenderX = cajaTargetX;
                    cajaRenderY = cajaTargetY;
                    verificarVictoria();
                }
            }
        }
    }

    private void verificarVictoria() {
        if (cajaX == objetivoX && cajaY == objetivoY && !juegoGanado) {
            juegoGanado = true;

            if (sonidoVictoria != null) {
                sonidoVictoria.play(0.3f);
            }

            musicafondo.pause();
            mostrarDialogoVictoria();
        }
    }

    private void mostrarDialogoVictoria() {
        String mensaje = "FELICIDADES!\n\nHas completado el nivel en " + movimientos + " movimientos.\n\nQuieres jugar de nuevo?";

        Dialog dialogo = new Dialog("HAS GANADO!", skin);

        Label mensajeLabel = new Label(mensaje, skin);
        mensajeLabel.setWrap(true);
        
        TextButton botonJugarDeNuevo = new TextButton("Jugar de nuevo", skin);
        TextButton botonVolver = new TextButton("Volver al menu", skin);
        
        botonJugarDeNuevo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
                main.setScreen(new GameScreen(main));
                dispose();
                dialogo.hide();
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

        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
                main.setScreen(new MenuScreen(main));
                dispose();
                dialogo.hide();
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

        Table contentTable = dialogo.getContentTable();
        contentTable.add(mensajeLabel).width(400).pad(20).row();

        Table buttonTable = dialogo.getButtonTable();
        buttonTable.clearChildren();

        buttonTable.add(botonJugarDeNuevo).size(150, 50).pad(10);
        buttonTable.add(botonVolver).size(150, 50).pad(10);

        dialogo.show(stage);
    }

    @Override
    public void render(float delta) {

        actualizarAnimacion(delta);

        tiempoDesdeUltimoMovimiento += delta;

        if (tiempoDesdeUltimoMovimiento >= delayMovimiento && !juegoGanado && !estaMoviendose) {
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

            cantmoves.setText("Movimientos: " + movimientos);
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

        // objetivo para caja
        batch.draw(objetivoTex, objetivoX * TILE + 30, objetivoY * TILE + 30, 40, 40);

        int jugadorWidth = 84;
        int jugadorHeight = 84;
        float offsetX = (TILE - jugadorWidth) / 2f;
        float offsetY = (TILE - jugadorHeight) / 2f;

        float jugadorPosX = jugadorRenderX + offsetX;
        float jugadorPosY = jugadorRenderY + offsetY;

        // efecto espejo para las direcciones
        if (jugadorMiraIzquierda) {
            batch.draw(jugadorTex,
                    jugadorPosX + jugadorWidth, jugadorPosY,
                    -jugadorWidth, jugadorHeight);
        } else {
            // imagen en estado defautl
            batch.draw(jugadorTex,
                    jugadorPosX, jugadorPosY,
                    jugadorWidth, jugadorHeight);
        }

        // colorear si esta en posicion de encaje
        if (cajaX == objetivoX && cajaY == objetivoY) {
            batch.setColor(0.5f, 1f, 0.5f, 1f);
        }
        batch.draw(cajaTex, cajaRenderX, cajaRenderY, TILE, TILE);
        batch.setColor(Color.WHITE);

        batch.end();

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
        if (objetivoTex != null) {
            objetivoTex.dispose();
        }
        musicafondo.dispose();
        audiomove.dispose();
        if (sonidoVictoria != null) {
            sonidoVictoria.dispose();
        }
        stage.dispose();
        skin.dispose();
    }
}
