package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Eventos.Sokoban;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class GameScreen implements Screen {

    final Main main;
    private Sokoban juegoSokoban;
    private Stage stage;
    private Skin skin;
    private Label cantmoves;
    private Label cantempujes;
    private Label scoreLabel;
    private Label timeLabel;

    private float tiempoDesdeUltimoMovimiento = 0f;
    private final float delayMovimiento = 0.2f;
    private boolean victoriaMostrada = false;

    private int nivelActual;
    private int score;
    private float tiempoDeJuego;
    private int scoreBase = 10000;
    
    private int keyUp, keyDown, keyLeft, keyRight;

    public GameScreen(final Main main, int nivel) {
        this.main = main;
        this.nivelActual = nivel;
        this.juegoSokoban = new Sokoban(main, nivel);
        this.tiempoDeJuego = 0;
        this.score = scoreBase;
        
        loadControls(); 
        initializeUI();
        juegoSokoban.inicializarRecursos();
    }

    private void loadControls() {
        LogicaUsuarios lu = new LogicaUsuarios();
        int[] prefs = lu.getPreferencias(main.username);
        this.keyUp = prefs[4];
        this.keyDown = prefs[5];
        this.keyLeft = prefs[6];
        this.keyRight = prefs[7];
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
                main.setScreen(new MenuScreen(main));
                dispose();
            }
        });

        panelControles.add(cantmoves).expandX().align(Align.left);
        panelControles.add(cantempujes).expandX().align(Align.left);
        panelControles.add(scoreLabel).expandX().align(Align.center);
        panelControles.add(timeLabel).expandX().align(Align.right);
        panelControles.add(botonVolver).width(200).height(50).align(Align.right);
        
        tablaPrincipal.add(panelControles).growX().pad(10).row();
        tablaPrincipal.add().expand().fill();

        Gdx.input.setInputProcessor(stage);
    }
    
    private void mostrarDialogoVictoria() {
        String mensaje = "FELICIDADES!\nHas completado el nivel con un puntaje de " + Math.max(0, score) + ".\n\nQuieres jugar de nuevo?";

        Dialog dialogo = new Dialog("HAS GANADO!", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    main.setScreen(new GameScreen(main, nivelActual));
                } else {
                    main.setScreen(new MenuScreen(main));
                }
                dispose();
            }
        };
        dialogo.text(mensaje);
        dialogo.button("Jugar de nuevo", true);
        dialogo.button("Volver al menu", false);
        dialogo.show(stage);
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
            if (Gdx.input.isKeyJustPressed(keyRight)) {
                juegoSokoban.moverJugador(1, 0);
                seMovio = true;
            }
            if (Gdx.input.isKeyJustPressed(keyLeft)) {
                juegoSokoban.moverJugador(-1, 0);
                seMovio = true;
            }
            if (Gdx.input.isKeyJustPressed(keyUp)) {
                juegoSokoban.moverJugador(0, 1);
                seMovio = true;
            }
            if (Gdx.input.isKeyJustPressed(keyDown)) {
                juegoSokoban.moverJugador(0, -1);
                seMovio = true;
            }

            if (seMovio) tiempoDesdeUltimoMovimiento = 0f;
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
    public void show() {
        main.playGameMusic();
    }

    @Override
    public void hide() {
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        juegoSokoban.resize(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        juegoSokoban.dispose();
        stage.dispose();
        skin.dispose();
    }
}