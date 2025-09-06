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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameScreen implements Screen {

    final Main main;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture jugadorTex, cajaTex, sueloTex, paredTex, objetivoTex;
    private Music musicafondo;
    private Sound audiomove, sonidoVictoria;

    private final int TILE = 90;
    private final int FILAS = 8;
    private final int COLUMNAS = 12;
    private volatile int movimientos = 0;

    Label cantmoves;

    private int[][] mapa = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1},
        {1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
        {1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1},
        {1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    // Posiciones lógicas (thread-safe)
    private volatile int jugadorX = 2, jugadorY = 2;
    private volatile int cajaX = 4, cajaY = 6;

    // Posiciones para las animaciones
    private volatile float jugadorRenderX = 2 * TILE, jugadorRenderY = 2 * TILE;
    private volatile float cajaRenderX = 4 * TILE, cajaRenderY = 6 * TILE;

    // Variables para la animación
    private final AtomicBoolean estaMoviendose = new AtomicBoolean(false);
    private volatile float tiempoAnimacion = 0f;
    private final float duracionAnimacion = 0.3f;

    // Posiciones de inicio y destino
    private volatile float jugadorStartX, jugadorStartY, jugadorTargetX, jugadorTargetY;
    private volatile float cajaStartX, cajaStartY, cajaTargetX, cajaTargetY;
    private final AtomicBoolean cajaSeMovioTambien = new AtomicBoolean(false);

    // Control de la dirección
    private volatile boolean jugadorMiraIzquierda = false;

    // Posición objetivo
    private final int objetivoX = 8, objetivoY = 6;

    private volatile float tiempoDesdeUltimoMovimiento = 0f;
    private final float delayMovimiento = 0.2f;

    private final AtomicBoolean juegoGanado = new AtomicBoolean(false);

    private Stage stage;
    private Skin skin;

    // Hilos para manejo de eventos en tiempo real
    private ExecutorService collisionDetector;
    private ExecutorService animationUpdater;
    private final AtomicBoolean gameRunning = new AtomicBoolean(true);

    // Flags para comunicación entre hilos
    private final AtomicBoolean needsVictoryCheck = new AtomicBoolean(false);
    private final AtomicBoolean playMoveSound = new AtomicBoolean(false);

    public GameScreen(final Main main) {
        this.main = main;

        initializeResources();
        initializeUI();
        initializeThreads();
    }

    private void initializeResources() {
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
    }

    private void initializeUI() {
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

    private void initializeThreads() {
        // Hilo para detección de colisiones en tiempo real
        collisionDetector = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "CollisionDetector");
            t.setDaemon(true);
            return t;
        });

        // Hilo para actualización de animaciones
        animationUpdater = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "AnimationUpdater");
            t.setDaemon(true);
            return t;
        });

        startCollisionDetectionThread();
        startAnimationUpdateThread();
    }

    private void startCollisionDetectionThread() {
        collisionDetector.submit(() -> {
            while (gameRunning.get()) {
                try {
                    // Verificar victoria continuamente
                    if (!juegoGanado.get() && cajaX == objetivoX && cajaY == objetivoY) {
                        needsVictoryCheck.set(true);
                    }

                    Thread.sleep(50); // Verificar cada 50ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error en CollisionDetector: " + e.getMessage());
                }
            }
        });
    }

    private void startAnimationUpdateThread() {
        animationUpdater.submit(() -> {
            long lastTime = System.nanoTime();
            while (gameRunning.get()) {
                try {
                    if (estaMoviendose.get()) {
                        long currentTime = System.nanoTime();
                        float delta = (currentTime - lastTime) / 1000000000f;
                        lastTime = currentTime;

                        updateAnimationInThread(delta);
                    } else {
                        lastTime = System.nanoTime(); // Reset cuando no se está moviendo
                    }

                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error en AnimationUpdater: " + e.getMessage());
                }
            }
        });
    }

    private void updateAnimationInThread(float delta) {
        tiempoAnimacion += delta;

        float progreso = Math.min(tiempoAnimacion / duracionAnimacion, 1.0f);
        progreso = 1f - (1f - progreso) * (1f - progreso);

        // Actualizar posiciones de renderizado
        jugadorRenderX = MathUtils.lerp(jugadorStartX, jugadorTargetX, progreso);
        jugadorRenderY = MathUtils.lerp(jugadorStartY, jugadorTargetY, progreso);

        if (cajaSeMovioTambien.get()) {
            cajaRenderX = MathUtils.lerp(cajaStartX, cajaTargetX, progreso);
            cajaRenderY = MathUtils.lerp(cajaStartY, cajaTargetY, progreso);
        }

        // Verificar si la animación terminó
        if (progreso >= 1.0f) {
            estaMoviendose.set(false);

            jugadorRenderX = jugadorTargetX;
            jugadorRenderY = jugadorTargetY;
            if (cajaSeMovioTambien.get()) {
                cajaRenderX = cajaTargetX;
                cajaRenderY = cajaTargetY;
            }
        }
    }

    // Método para mover jugador (ejecutado en el hilo principal)
    private void moverJugador(int dx, int dy) {
        if (juegoGanado.get() || estaMoviendose.get()) {
            return;
        }

        // Actualizar dirección del sprite
        if (dx > 0) {
            jugadorMiraIzquierda = false;
        } else if (dx < 0) {
            jugadorMiraIzquierda = true;
        }

        int nuevoX = jugadorX + dx;
        int nuevoY = jugadorY + dy;

        if (mapa[nuevoY][nuevoX] == 1) {
            return;
        }

        // Configurar posiciones de inicio
        jugadorStartX = jugadorRenderX;
        jugadorStartY = jugadorRenderY;
        cajaStartX = cajaRenderX;
        cajaStartY = cajaRenderY;
        cajaSeMovioTambien.set(false);

        if (nuevoX == cajaX && nuevoY == cajaY) {
            int nuevoCajaX = cajaX + dx;
            int nuevoCajaY = cajaY + dy;

            if (mapa[nuevoCajaY][nuevoCajaX] == 0) {
                cajaX = nuevoCajaX;
                cajaY = nuevoCajaY;
                jugadorX = nuevoX;
                jugadorY = nuevoY;

                jugadorTargetX = jugadorX * TILE;
                jugadorTargetY = jugadorY * TILE;
                cajaTargetX = cajaX * TILE;
                cajaTargetY = cajaY * TILE;
                cajaSeMovioTambien.set(true);

                estaMoviendose.set(true);
                tiempoAnimacion = 0f;
            }
        } else {
            jugadorX = nuevoX;
            jugadorY = nuevoY;

            jugadorTargetX = jugadorX * TILE;
            jugadorTargetY = jugadorY * TILE;

            estaMoviendose.set(true);
            tiempoAnimacion = 0f;
        }

        movimientos++;
        playMoveSound.set(true);
    }

    private void verificarVictoria() {
        if (juegoGanado.getAndSet(true)) {
            return; // Ya se procesó
        }

        if (sonidoVictoria != null) {
            sonidoVictoria.play(0.3f);
        }
        musicafondo.pause();
        mostrarDialogoVictoria();
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
        // Verificar flags de los hilos secundarios
        if (needsVictoryCheck.getAndSet(false)) {
            verificarVictoria();
        }

        if (playMoveSound.getAndSet(false)) {
            audiomove.play(0.6f);
        }

        tiempoDesdeUltimoMovimiento += delta;

        // Manejar entrada del usuario
        if (tiempoDesdeUltimoMovimiento >= delayMovimiento && !juegoGanado.get() && !estaMoviendose.get()) {
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
                tiempoDesdeUltimoMovimiento = 0f;
            }
        }

        cantmoves.setText("Movimientos: " + movimientos);

        // Renderizado
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

        batch.draw(objetivoTex, objetivoX * TILE + 30, objetivoY * TILE + 30, 40, 40);

        int jugadorWidth = 84;
        int jugadorHeight = 84;
        float offsetX = (TILE - jugadorWidth) / 2f;
        float offsetY = (TILE - jugadorHeight) / 2f;

        float jugadorPosX = jugadorRenderX + offsetX;
        float jugadorPosY = jugadorRenderY + offsetY;

        if (jugadorMiraIzquierda) {
            batch.draw(jugadorTex,
                    jugadorPosX + jugadorWidth, jugadorPosY,
                    -jugadorWidth, jugadorHeight);
        } else {
            batch.draw(jugadorTex,
                    jugadorPosX, jugadorPosY,
                    jugadorWidth, jugadorHeight);
        }

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
        gameRunning.set(false);

        if (collisionDetector != null && !collisionDetector.isShutdown()) {
            collisionDetector.shutdown();
        }
        if (animationUpdater != null && !animationUpdater.isShutdown()) {
            animationUpdater.shutdown();
        }

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
