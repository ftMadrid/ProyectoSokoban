package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    private Stage stage;
    private BitmapFont pixelFont;

    private Label cantmoves, cantempujes, scoreLabel, timeLabel;
    private int score;
    private float tiempoDeJuego;
    private final int scoreBase = 10000;
    private int keyUp, keyDown, keyLeft, keyRight;
    private GestorIdiomas gestorIdiomas;

    private boolean victoriaMostrada = false;
    private Texture backgroundTexture;
    private Table pausePanel;
    private boolean isPaused = false;
    
    private Label pauseTitle;
    private TextButton resumeButton, levelSelectButton, menuButton;

    public GameScreen(final Main main, int nivel) {
        this.main = main;
        this.nivelActual = nivel;
        this.juegoSokoban = new Sokoban(main, nivel, main.username);
        this.juegoSokoban.soundVolume = main.getVolume();
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();

        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        loadControls();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);
        generator.dispose();

        initializeUI();
        juegoSokoban.inicializarRecursos();
    }

    private void loadControls() {
        this.keyUp = main.keyUp;
        this.keyDown = main.keyDown;
        this.keyLeft = main.keyLeft;
        this.keyRight = main.keyRight;
    }

    private void initializeUI() {
        stage = new Stage(new ScreenViewport());

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, pixelFont.getColor());

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table panel = new Table();
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.5f);
        bgPixmap.fill();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap))));
        bgPixmap.dispose();

        cantmoves = new Label(gestorIdiomas.setTexto("game.movimientos") + "0", labelStyle);
        cantempujes = new Label(gestorIdiomas.setTexto("game.empujes") + "0", labelStyle);
        scoreLabel = new Label(gestorIdiomas.setTexto("game.score") + scoreBase, labelStyle);
        timeLabel = new Label(gestorIdiomas.setTexto("game.tiempo") + "0s", labelStyle);

        panel.add(cantmoves).expandX().left().padLeft(20);
        panel.add(cantempujes).expandX().left().padLeft(20);
        panel.add(scoreLabel).expandX().center();
        panel.add(timeLabel).expandX().right().padRight(20);

        root.top().add(panel).expandX().fillX().height(50);
        buildPauseMenu();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (juegoSokoban.isJuegoGanado()) return;
            if (isPaused) {
                resume();
            } else {
                pause();
            }
            return;
        }

        if (isPaused || juegoSokoban.isJuegoGanado()) return;

        if (Gdx.input.isKeyJustPressed(keyUp)) juegoSokoban.moverJugador(0, 1);
        if (Gdx.input.isKeyJustPressed(keyDown)) juegoSokoban.moverJugador(0, -1);
        if (Gdx.input.isKeyJustPressed(keyLeft)) juegoSokoban.moverJugador(-1, 0);
        if (Gdx.input.isKeyJustPressed(keyRight)) juegoSokoban.moverJugador(1, 0);
    }

    private Window.WindowStyle createDialogStyle() {
        Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = pixelFont;
        style.titleFontColor = Color.WHITE;
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        bgPixmap.fill();
        style.background = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        bgPixmap.dispose();
        return style;
    }

    private void mostrarDialogoVictoria() {
        if(victoriaMostrada) return;
        victoriaMostrada = true;

        LogicaUsuarios lu = new LogicaUsuarios();
        lu.guardarScore(main.username, nivelActual, Math.max(0, score));
        lu.marcarNivelPasado(main.username, nivelActual);

        String mensaje = gestorIdiomas.setTexto("game.dialogo_victoria_mensaje", score, juegoSokoban.getMovimientos(), juegoSokoban.getEmpujes(), String.format("%.0fs", tiempoDeJuego));
        Dialog dialogo = new Dialog(gestorIdiomas.setTexto("game.dialogo_victoria_titulo"), createDialogStyle()) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    transicionSuave.fadeOutAndChangeScreen(main, stage, new GameScreen(main, nivelActual));
                } else {
                    transicionSuave.fadeOutAndChangeScreen(main, stage, new LevelSelectScreen(main));
                }
            }
        };

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        dialogo.text(new Label(mensaje, labelStyle));
        dialogo.button(gestorIdiomas.setTexto("game.dialogo_victoria_reintentar"), true);
        dialogo.button(gestorIdiomas.setTexto("game.dialogo_victoria_menu"), false);

        ((TextButton) dialogo.getButtonTable().getCells().get(0).getActor()).setStyle(btnStyle);
        ((TextButton) dialogo.getButtonTable().getCells().get(1).getActor()).setStyle(btnStyle);

        dialogo.show(stage);
    }

    private void buildPauseMenu() {
        if (pausePanel != null) pausePanel.remove();

        pausePanel = new Table();
        pausePanel.setFillParent(true);
        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0,0,0,0.7f);
        bgPixmap.fill();
        pausePanel.setBackground(new TextureRegionDrawable(new Texture(bgPixmap)));
        bgPixmap.dispose();

        Table container = new Table();
        container.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        container.pad(20);

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        pauseTitle = new Label("", labelStyle);
        container.add(pauseTitle).colspan(2).center().padBottom(20).row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        resumeButton = new TextButton("", btnStyle);
        resumeButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                resume();
            }
        });

        levelSelectButton = new TextButton("", btnStyle);
        levelSelectButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                resume();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LevelSelectScreen(main));
            }
        });

        menuButton = new TextButton("", btnStyle);
        menuButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                resume();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        });

        container.add(resumeButton).width(250).height(50).pad(10).row();
        container.add(levelSelectButton).width(250).height(50).pad(10).row();
        container.add(menuButton).width(250).height(50).pad(10).row();

        pausePanel.add(container);
        stage.addActor(pausePanel);
        pausePanel.setVisible(false);
    }
    
    private void updatePauseMenuLanguage(){
        if(pauseTitle == null) return;
        pauseTitle.setText(gestorIdiomas.setTexto("pause.title"));
        resumeButton.setText(gestorIdiomas.setTexto("pause.resume"));
        levelSelectButton.setText(gestorIdiomas.setTexto("pause.level_select"));
        menuButton.setText(gestorIdiomas.setTexto("pause.main_menu"));
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playGameMusic();
        transicionSuave.fadeIn(stage);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        if (!isPaused) {
            if (!juegoSokoban.isJuegoGanado()) {
                tiempoDeJuego += delta;
                score = scoreBase - (juegoSokoban.getMovimientos() * 5) - (int)(tiempoDeJuego * 2);
                cantmoves.setText(gestorIdiomas.setTexto("game.movimientos") + juegoSokoban.getMovimientos());
                cantempujes.setText(gestorIdiomas.setTexto("game.empujes") + juegoSokoban.getEmpujes());
                scoreLabel.setText(gestorIdiomas.setTexto("game.score") + Math.max(0, score));
                timeLabel.setText(String.format(gestorIdiomas.setTexto("game.tiempo") + "%.0fs", tiempoDeJuego));
            }

            juegoSokoban.render(delta);

            if (juegoSokoban.isJuegoGanado()) {
                mostrarDialogoVictoria();
            }
        } else {
            juegoSokoban.renderizar();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        juegoSokoban.resize(width, height);
    }

    @Override public void hide() {}

    @Override public void pause() {
        isPaused = true;
        if(pausePanel != null) {
            updatePauseMenuLanguage();
            pausePanel.setVisible(true);
        }
    }

    @Override public void resume() {
        isPaused = false;
        if (pausePanel != null) {
            pausePanel.setVisible(false);
        }
    }

    @Override public void dispose() {
        juegoSokoban.dispose();
        stage.dispose();
        pixelFont.dispose();
        backgroundTexture.dispose();
    }
}