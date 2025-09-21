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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Eventos.Sokoban;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class GameScreen implements Screen {

    private final Main main;
    private final int nivelActual;
    private final Sokoban juegoSokoban;
    private Stage stage; // UI
    private Skin skin;
    private BitmapFont pixelFont;

    private Label cantmoves, cantempujes, scoreLabel, timeLabel;
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
        parameter.size = 30; parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);
        generator.dispose();

        initializeUI();
        juegoSokoban.inicializarRecursos();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void loadControls() {
        int[] prefs = new LogicaUsuarios().getPreferencias(main.username);
        this.keyUp = prefs[4]; this.keyDown = prefs[5]; this.keyLeft = prefs[6]; this.keyRight = prefs[7];
    }

    private void initializeUI() {
        stage = new Stage(new ScreenViewport()); // UI ocupa toda la pantalla
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        skin.add("default-font", pixelFont, BitmapFont.class);

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, pixelFont.getColor());

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table panel = new Table(skin);
        panel.setBackground("default-pane");
        panel.pad(10).defaults().pad(5);

        cantmoves = new Label(gestorIdiomas.setTexto("game.movimientos") + "0", labelStyle);
        cantempujes = new Label(gestorIdiomas.setTexto("game.empujes") + "0", labelStyle);
        scoreLabel = new Label(gestorIdiomas.setTexto("game.score") + scoreBase, labelStyle);
        timeLabel = new Label(gestorIdiomas.setTexto("game.tiempo") + "0s", labelStyle);

        TextButton botonVolver = new TextButton(gestorIdiomas.setTexto("game.menu_boton"), skin);
        botonVolver.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        });

        panel.add(cantmoves).expandX().align(Align.left);
        panel.add(cantempujes).expandX().align(Align.left);
        panel.add(scoreLabel).expandX().align(Align.center);
        panel.add(timeLabel).expandX().align(Align.right);
        panel.add(botonVolver).width(140).height(44).padLeft(10);

        root.top().add(panel).expandX().fillX();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(keyUp)) juegoSokoban.moverJugador(0, 1);
        if (Gdx.input.isKeyJustPressed(keyDown)) juegoSokoban.moverJugador(0, -1);
        if (Gdx.input.isKeyJustPressed(keyLeft)) juegoSokoban.moverJugador(-1, 0);
        if (Gdx.input.isKeyJustPressed(keyRight)) juegoSokoban.moverJugador(1, 0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) mostrarDialogoSalida();
    }

    private void mostrarDialogoSalida() {
        Dialog d = new Dialog("SOKOMINE", skin) {
            @Override protected void result(Object obj) {
                boolean volver = !(Boolean)obj;
                if (volver) transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        };
        d.text("\n" + gestorIdiomas.setTexto("game.dialogo_victoria_menu") + "?\n\n");
        d.button("OK", false); d.button("CANCEL", true);
        d.show(stage);
    }

    private void mostrarDialogoVictoria() {
        String mensaje = gestorIdiomas.setTexto("game.dialogo_victoria_mensaje", score);
        Dialog dialogo = new Dialog("SOKOMINE", skin) {
            @Override protected void result(Object obj) {
                boolean reintentar = (Boolean)obj;
                if (reintentar) transicionSuave.fadeOutAndChangeScreen(main, stage, new GameScreen(main, nivelActual));
                else transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        };
        dialogo.text(mensaje);
        dialogo.button(gestorIdiomas.setTexto("game.dialogo_victoria_reintentar"), true);
        dialogo.button(gestorIdiomas.setTexto("game.dialogo_victoria_menu"), false);
        dialogo.show(stage);
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playGameMusic();
        transicionSuave.fadeIn(stage);
    }

    @Override public void render(float delta) {
        if (!juegoSokoban.isJuegoGanado()) {
            tiempoDeJuego += delta;
            score = scoreBase - (juegoSokoban.getMovimientos() * 5) - (int)(tiempoDeJuego * 2);
            scoreLabel.setText(gestorIdiomas.setTexto("game.score") + Math.max(0, score));
            timeLabel.setText(String.format(gestorIdiomas.setTexto("game.tiempo") + "%.0fs", tiempoDeJuego));
        }

        juegoSokoban.actualizar(delta);
        if (!juegoSokoban.isJuegoGanado()) handleInput();

        if (juegoSokoban.isJuegoGanado()) {
            LogicaUsuarios lu = new LogicaUsuarios();
            lu.guardarScore(main.username, nivelActual, Math.max(0, score));
            lu.marcarNivelPasado(main.username, nivelActual);
            mostrarDialogoVictoria();
        }

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        juegoSokoban.render(delta);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        juegoSokoban.resize(width, height);
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override public void dispose() {
        juegoSokoban.dispose(); stage.dispose(); skin.dispose(); pixelFont.dispose();
    }
}
