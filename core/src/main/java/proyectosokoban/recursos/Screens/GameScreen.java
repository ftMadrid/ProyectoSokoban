package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport; // Importado
import proyectosokoban.recursos.Eventos.Sokoban;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class GameScreen implements Screen {

    final Main main;
    private Sokoban juegoSokoban;
    private Stage stage;
    private Skin skin;
    private Label cantmoves, cantempujes, scoreLabel, timeLabel;
    private BitmapFont pixelFont;
    private boolean victoriaMostrada = false;
    private int nivelActual;
    private int score;
    private float tiempoDeJuego;
    private final int scoreBase = 10000;
    private int keyUp, keyDown, keyLeft, keyRight;
    private GestorIdiomas gestorIdiomas;

    public GameScreen(final Main main, int nivel) {
        this.main = main;
        this.nivelActual = nivel;
        this.juegoSokoban = new Sokoban(main, nivel, main.username);
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();

        loadControls();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);
        generator.dispose();

        initializeUI();
        juegoSokoban.inicializarRecursos();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void loadControls() {
        int[] prefs = new LogicaUsuarios().getPreferencias(main.username);
        this.keyUp = prefs[4];
        this.keyDown = prefs[5];
        this.keyLeft = prefs[6];
        this.keyRight = prefs[7];
    }

    private void initializeUI() {
        // Unicamente se ha cambiado ScreenViewport por FitViewport
        stage = new Stage(new FitViewport(1280, 720));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.add("default-font", pixelFont, BitmapFont.class);

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, pixelFont.getColor());

        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        stage.addActor(tablaPrincipal);

        Table panelControles = new Table(skin);
        panelControles.setBackground("default-pane");
        panelControles.pad(10).defaults().pad(5);

        cantmoves = new Label(gestorIdiomas.setTexto("game.movimientos") + "0", labelStyle);
        cantempujes = new Label(gestorIdiomas.setTexto("game.empujes") + "0", labelStyle);
        scoreLabel = new Label(gestorIdiomas.setTexto("game.score") + scoreBase, labelStyle);
        timeLabel = new Label(gestorIdiomas.setTexto("game.tiempo") + "0s", labelStyle);

        TextButton botonVolver = new TextButton(gestorIdiomas.setTexto("game.menu_boton"), skin);
        botonVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        });

        panelControles.add(cantmoves).expandX().align(Align.left);
        panelControles.add(cantempujes).expandX().align(Align.left);
        panelControles.add(scoreLabel).expandX().align(Align.center);
        panelControles.add(timeLabel).expandX().align(Align.right);
        panelControles.add(botonVolver).width(150).height(50).align(Align.right);

        tablaPrincipal.add(panelControles).growX().pad(10).row();
        tablaPrincipal.add().expand().fill();
    }

    private void mostrarDialogoVictoria() {
        String mensaje = gestorIdiomas.setTexto("game.dialogo_victoria_mensaje", Math.max(0, score));

        Dialog dialogo = new Dialog(gestorIdiomas.setTexto("game.dialogo_victoria_titulo"), skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    transicionSuave.fadeOutAndChangeScreen(main, stage, new GameScreen(main, nivelActual));
                } else {
                    transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
                }
            }
        };
        dialogo.text(mensaje);
        dialogo.button(gestorIdiomas.setTexto("game.dialogo_victoria_reintentar"), true);
        dialogo.button(gestorIdiomas.setTexto("game.dialogo_victoria_menu"), false);
        dialogo.show(stage);
    }

    @Override
    public void render(float delta) {
        if (!juegoSokoban.isJuegoGanado()) {
            tiempoDeJuego += delta;
            score = scoreBase - (juegoSokoban.getMovimientos() * 5) - (int) (tiempoDeJuego * 2);
            scoreLabel.setText(gestorIdiomas.setTexto("game.score") + Math.max(0, score));
            timeLabel.setText(String.format(gestorIdiomas.setTexto("game.tiempo") + "%.0fs", tiempoDeJuego));
        }

        juegoSokoban.actualizar(delta);

        if (!juegoSokoban.isJuegoGanado()) {
            handleInput();
        }

        if (juegoSokoban.isJuegoGanado() && !victoriaMostrada) {
            LogicaUsuarios lu = new LogicaUsuarios();
            lu.guardarScore(main.username, nivelActual, Math.max(0, score));
            lu.marcarNivelPasado(main.username, nivelActual);
            mostrarDialogoVictoria();
            victoriaMostrada = true;
        }

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        juegoSokoban.renderizar();

        cantmoves.setText(gestorIdiomas.setTexto("game.movimientos") + juegoSokoban.getMovimientos());
        cantempujes.setText(gestorIdiomas.setTexto("game.empujes") + juegoSokoban.getEmpujes());
        stage.act(delta);
        stage.draw();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(keyRight)) {
            juegoSokoban.moverJugador(1, 0);
        } else if (Gdx.input.isKeyJustPressed(keyLeft)) {
            juegoSokoban.moverJugador(-1, 0);
        } else if (Gdx.input.isKeyJustPressed(keyUp)) {
            juegoSokoban.moverJugador(0, 1);
        } else if (Gdx.input.isKeyJustPressed(keyDown)) {
            juegoSokoban.moverJugador(0, -1);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playGameMusic();
        transicionSuave.fadeIn(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        juegoSokoban.resize(width, height);
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
        pixelFont.dispose();
    }
}