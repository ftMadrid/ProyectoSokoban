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
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
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
    private Label scoreLabel;
    private Label timeLabel;

    private volatile float tiempoDesdeUltimoMovimiento = 0f;
    private final float delayMovimiento = 0.2f;
    private boolean victoriaMostrada = false;

    private int nivelActual;
    private int score;
    private float tiempoDeJuego;
    private int scoreBase = 10000;

    public GameScreen(final Main main, int nivel) {
        this.main = main;
        this.nivelActual = nivel;
        this.juegoSokoban = new Sokoban(main, nivel);
        this.tiempoDeJuego = 0;
        this.score = scoreBase;
        initializeUI();
        juegoSokoban.inicializarRecursos();
        aplicarPreferenciasDeAudio();
    }

    private void initializeUI() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        stage.addActor(tablaPrincipal);

        Table panelControles = new Table(skin);
        panelControles.setBackground("default-pane");
        panelControles.pad(10);
        panelControles.defaults().pad(5);

        cantmoves = new Label("Movimientos: 0", skin);
        cantempujes = new Label("Empujes: 0", skin);
        scoreLabel = new Label("Score: " + scoreBase, skin);
        timeLabel = new Label("Tiempo: 0s", skin);

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

        panelControles.add(cantmoves).expandX().align(Align.left);
        panelControles.add(cantempujes).expandX().align(Align.left);
        panelControles.add(scoreLabel).expandX().align(Align.center);
        panelControles.add(timeLabel).expandX().align(Align.right);
        panelControles.add(botonVolver).width(200).height(50).align(Align.right);

        Table panelJuego = new Table(skin);

        tablaPrincipal.add(panelControles).growX().pad(10).row();
        tablaPrincipal.add(panelJuego).expand().fill();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }
    
    private void aplicarPreferenciasDeAudio() {
        LogicaUsuarios lu = new LogicaUsuarios();
        if (main.username != null) {
            int[] prefs = lu.getPreferencias(main.username);
            int volumen = prefs[0];
            boolean mute = prefs[3] == 1;

            float vol = volumen / 100f;
            juegoSokoban.musicafondo.setVolume(mute ? 0 : vol);
            juegoSokoban.soundVolume = mute ? 0 : vol;
            juegoSokoban.isMuted = mute;

            if (juegoSokoban.sonidoVictoria != null) juegoSokoban.sonidoVictoria.play(0);
        }
    }

    private void mostrarDialogoVictoria() {
        String mensaje = "FELICIDADES!\n\nHas completado el nivel con un puntaje de " + Math.max(0, score) + ".\n\nQuieres jugar de nuevo?";

        Dialog dialogo = new Dialog("HAS GANADO!", skin);
        Label mensajeLabel = new Label(mensaje, skin);
        mensajeLabel.setWrap(true);

        TextButton botonJugarDeNuevo = new TextButton("Jugar de nuevo", skin);
        TextButton botonVolver = new TextButton("Volver al menu", skin);

        botonJugarDeNuevo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
                main.setScreen(new GameScreen(main, nivelActual));
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

    @Override
    public void render(float delta) {
        if (!juegoSokoban.isJuegoGanado()) {
            tiempoDeJuego += delta;
            score = (int) (scoreBase - (juegoSokoban.getMovimientos() * 5) - (tiempoDeJuego * 2));
            scoreLabel.setText("Score: " + Math.max(0, score));
            timeLabel.setText(String.format("Tiempo: %.0fs", tiempoDeJuego));
        }

        juegoSokoban.actualizar(delta);
        tiempoDesdeUltimoMovimiento += delta;

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
        
        if (juegoSokoban.isJuegoGanado() && !victoriaMostrada) {
            LogicaUsuarios lu = new LogicaUsuarios();
            lu.guardarScore(main.username, nivelActual, Math.max(0, score));
            lu.marcarNivelPasado(main.username, nivelActual);
            mostrarDialogoVictoria();
            victoriaMostrada = true;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        juegoSokoban.renderizar();

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