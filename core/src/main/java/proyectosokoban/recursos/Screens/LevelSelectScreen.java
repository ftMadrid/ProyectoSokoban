package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import proyectosokoban.recursos.SelectorNiveles.MapaSelector;
import proyectosokoban.recursos.SelectorNiveles.SelectorNiveles;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;

public class LevelSelectScreen implements Screen {

    final Main main;
    private Stage stage;
    private Skin skin;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private GestorIdiomas gestorIdiomas;

    private MapaSelector mapa;
    private SelectorNiveles selector;
    private int TILE = 64;

    private float tiempoDesdeUltimoMovimiento = 0f;
    private final float delayMovimiento = 0.2f;

    private int keyUp, keyDown, keyLeft, keyRight;

    // Variables para controlar la vista del mapa
    private int mapaAncho, mapaAlto;

    // Bandera para controlar si la pantalla está activa
    private boolean active = true;

    public LevelSelectScreen(final Main main) {
        this.main = main;
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();
        cargarControles();
        inicializar();
    }

    private void cargarControles() {
        this.keyUp = Input.Keys.UP;
        this.keyDown = Input.Keys.DOWN;
        this.keyLeft = Input.Keys.LEFT;
        this.keyRight = Input.Keys.RIGHT;
    }

    private void inicializar() {
        batch = new SpriteBatch();

        // Pasar el nombre de usuario al MapaSelector
        mapa = new MapaSelector(TILE, main.username);
        selector = new SelectorNiveles(7, 4, TILE, mapa);

        // Calcular dimensiones del mapa
        mapaAncho = mapa.getColumnas() * TILE;
        mapaAlto = mapa.getFilas() * TILE;

        // Configurar la cámara para mostrar todo el mapa
        camera = new OrthographicCamera();
        camera.setToOrtho(false, mapaAncho, mapaAlto);
        camera.position.set(mapaAncho / 2, mapaAlto / 2, 0);
        camera.update();

        inicializarUI();
    }

    private void inicializarUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        stage.addActor(tablaPrincipal);

        Table panelSuperior = new Table(skin);
        panelSuperior.setBackground("default-pane");
        panelSuperior.pad(10);

        TextButton botonVolver = new TextButton(gestorIdiomas.setTexto("back.button"), skin);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                active = false; // Marcar como inactiva antes de cambiar
                main.setScreen(new MenuScreen(main));
                dispose();
            }
        });

        panelSuperior.add(botonVolver).width(200).height(50).align(com.badlogic.gdx.utils.Align.right);

        tablaPrincipal.add(panelSuperior).growX().pad(10).row();
        tablaPrincipal.add().expand().fill();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Si la pantalla no está activa, no renderizar
        if (!active) {
            return;
        }

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        mapa.render(batch);
        selector.render(batch);
        batch.end();

        selector.actualizar(delta);
        tiempoDesdeUltimoMovimiento += delta;

        if (tiempoDesdeUltimoMovimiento >= delayMovimiento && !selector.estaMoviendose()) {
            boolean seMovio = false;

            if (Gdx.input.isKeyJustPressed(keyRight)) {
                seMovio = selector.mover(1, 0);
            } else if (Gdx.input.isKeyJustPressed(keyLeft)) {
                seMovio = selector.mover(-1, 0);
            } else if (Gdx.input.isKeyJustPressed(keyUp)) {
                seMovio = selector.mover(0, 1);
            } else if (Gdx.input.isKeyJustPressed(keyDown)) {
                seMovio = selector.mover(0, -1);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                int nivelSeleccionado = selector.getNivelSeleccionado();
                if (nivelSeleccionado > 0) {
                    // Verificar si el nivel está desbloqueado antes de cambiar de pantalla
                    if (mapa.isNivelDesbloqueado(nivelSeleccionado)) {
                        active = false; // Marcar como inactiva antes de cambiar
                        main.setScreen(new GameScreen(main, nivelSeleccionado));
                        dispose();
                    } else {
                        // Aquí puedes agregar un sonido o mensaje indicando que el nivel está bloqueado
                        System.out.println("Nivel " + nivelSeleccionado + " está bloqueado");
                    }
                }
            }

            if (seMovio) {
                tiempoDesdeUltimoMovimiento = 0f;
            }
        }

        // Solo actuar y dibujar si el stage no es nulo
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }

        // Ajustar la cámara para mantener la relación de aspecto y mostrar todo el mapa
        float aspectRatio = (float) width / height;
        float mapAspectRatio = (float) mapaAncho / mapaAlto;

        if (aspectRatio > mapAspectRatio) {
            // La pantalla es más ancha que el mapa
            camera.viewportHeight = mapaAlto;
            camera.viewportWidth = mapaAlto * aspectRatio;
        } else {
            // La pantalla es más alta que el mapa
            camera.viewportWidth = mapaAncho;
            camera.viewportHeight = mapaAncho / aspectRatio;
        }

        camera.position.set(mapaAncho / 2, mapaAlto / 2, 0);
        camera.update();

        // Actualizar la proyección del batch
        if (batch != null) {
            batch.setProjectionMatrix(camera.combined);
        }
    }

    @Override
    public void show() {
        active = true;
        main.playMenuMusic();
    }

    @Override
    public void hide() {
        active = false;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }

        if (skin != null) {
            skin.dispose();
            skin = null;
        }

        if (batch != null) {
            batch.dispose();
            batch = null;
        }

        if (mapa != null) {
            mapa.dispose();
            mapa = null;
        }

        if (selector != null) {
            selector.dispose();
            selector = null;
        }
    }
}
