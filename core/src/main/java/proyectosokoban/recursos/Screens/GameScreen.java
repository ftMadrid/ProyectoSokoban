package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Eventos.Sokoban;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class GameScreen implements Screen {

    final Main main;
    private Sokoban juegoSokoban;
    private Stage stage;
    private Skin skin;
    private Label cantmoves;
    private Label cantempujes;

    private volatile float tiempoDesdeUltimoMovimiento = 0f;
    private final float delayMovimiento = 0.2f;

    public GameScreen(final Main main) {
        this.main = main;
        this.juegoSokoban = new Sokoban(main);

        initializeUI();
        juegoSokoban.inicializarRecursos();
    }

    private void initializeUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        stage.addActor(tablaPrincipal);

        // --- Panel de Controles y Estadísticas (ARRIBA) ---
        Table panelControles = new Table(skin);
        panelControles.setBackground("default-pane");
        panelControles.pad(10);
        panelControles.defaults().pad(5);

        cantmoves = new Label("Movimientos: 0", skin);
        cantempujes = new Label("Empujes: 0", skin);

        TextButton botonVolver = new TextButton("VOLVER AL MENU", skin);
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

        // Agrupamos los labels y el botón en una fila en el panel de controles
        panelControles.add(cantmoves).expandX().align(Align.left);
        panelControles.add(cantempujes).expandX().align(Align.left);
        panelControles.add(botonVolver).width(200).height(50).align(Align.right);

        // --- Panel de Juego (DEBAJO) ---
        Table panelJuego = new Table(skin);

        // --- Estructura de la Tabla Principal ---
        // Fila 1: Panel de Controles
        tablaPrincipal.add(panelControles).growX().pad(10).row();

        // Fila 2: El área de juego que ocupa el resto del espacio
        tablaPrincipal.add(panelJuego).expand().fill();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void mostrarDialogoVictoria() {
        String mensaje = "FELICIDADES!\n\nHas completado el nivel en " + juegoSokoban.getMovimientos() + " movimientos \nEmpujes realizados: " + juegoSokoban.getEmpujes() + ".\n\nQuieres jugar de nuevo?";

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
        Gdx.input.setInputProcessor(stage);
    }

    private boolean victoriaMostrada = false; // bandera

    @Override
    public void render(float delta) {
        // Actualizar el juego

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        juegoSokoban.renderizar();
        juegoSokoban.actualizar(delta);

        tiempoDesdeUltimoMovimiento += delta;

        // Manejar entrada del usuario
        if (tiempoDesdeUltimoMovimiento >= delayMovimiento && !juegoSokoban.isJuegoGanado()) {
            boolean seMovio = false;

            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                juegoSokoban.moverJugador(1, 0);
                seMovio = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                juegoSokoban.moverJugador(-1, 0);
                seMovio = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                juegoSokoban.moverJugador(0, 1);
                seMovio = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                juegoSokoban.moverJugador(0, -1);
                seMovio = true;
            }

            if (seMovio) {
                tiempoDesdeUltimoMovimiento = 0f;
            }
        }

        // Mostrar diálogo solo una vez
        if (juegoSokoban.isJuegoGanado() && !victoriaMostrada) {
            mostrarDialogoVictoria();
            victoriaMostrada = true;
        }

        // Renderizar
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        juegoSokoban.renderizar();

        // Actualizar UI
        cantmoves.setText("Movimientos: " + juegoSokoban.getMovimientos());
        cantempujes.setText("Empujes: " + juegoSokoban.getEmpujes());
        stage.act(delta);
        stage.draw();
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
        juegoSokoban.dispose();
        stage.dispose();
        skin.dispose();
    }
}
