/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

public class ScreenTesting implements Screen {

    final Main main;
    private Stage stage;
    private Skin skin;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private MapaSelector mapa;
    private SelectorNiveles selector;
    private int TILE = 64;

    private float tiempoDesdeUltimoMovimiento = 0f;
    private final float delayMovimiento = 0.2f;

    private int keyUp, keyDown, keyLeft, keyRight;

    public ScreenTesting(final Main main) {
        this.main = main;
        cargarControles();
        inicializar();
    }

    private void cargarControles() {
        // Aquí puedes cargar los controles personalizados si es necesario
        // Por ahora usaremos las flechas por defecto
        this.keyUp = Input.Keys.UP;
        this.keyDown = Input.Keys.DOWN;
        this.keyLeft = Input.Keys.LEFT;
        this.keyRight = Input.Keys.RIGHT;
    }

    private void inicializar() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();

        mapa = new MapaSelector(TILE);
        selector = new SelectorNiveles(0, 4, TILE, mapa); // Posición inicial

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

        TextButton botonVolver = new TextButton("VOLVER AL MENU", skin);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar cámara
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Renderizar mapa y selector
        batch.begin();
        mapa.render(batch);
        selector.render(batch);
        batch.end();

        // Actualizar lógica
        selector.actualizar(delta);
        tiempoDesdeUltimoMovimiento += delta;

        // Manejar entrada
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
                    main.setScreen(new GameScreen(main, nivelSeleccionado));
                    dispose();
                }
            }

            if (seMovio) {
                tiempoDesdeUltimoMovimiento = 0f;
            }
        }

        // Renderizar UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void show() {
        main.playMenuMusic();
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
        stage.dispose();
        skin.dispose();
        batch.dispose();
        mapa.dispose();
        selector.dispose();
    }
}
