package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    private Stage stage;
    private BitmapFont pixelFont;

    private Label cantmoves, cantempujes, scoreLabel, timeLabel;
    private int score;
    private float tiempoDeJuego;
    private final int scoreBase = 10000;
    private int keyUp, keyDown, keyLeft, keyRight;
    private GestorIdiomas gestorIdiomas;

    private boolean dialogoVictoriaMostrado = false;
    private Texture backgroundTexture;
    private Table pausePanel;
    private boolean isPaused = false;

    private Label pauseTitle;
    private TextButton resumeButton, levelSelectButton, menuButton;

    private boolean partidaRegistrada = false;

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
        scoreLabel = new Label("", labelStyle);
        scoreLabel.setVisible(false);
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
            if (juegoSokoban.isJuegoGanado()) {
                return;
            }
            if (isPaused) {
                resume();
            } else {
                pause();
            }
            return;
        }

        if (isPaused || juegoSokoban.isJuegoGanado()) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(keyUp)) {
            juegoSokoban.moverJugador(0, 1);
        }
        if (Gdx.input.isKeyJustPressed(keyDown)) {
            juegoSokoban.moverJugador(0, -1);
        }
        if (Gdx.input.isKeyJustPressed(keyLeft)) {
            juegoSokoban.moverJugador(-1, 0);
        }
        if (Gdx.input.isKeyJustPressed(keyRight)) {
            juegoSokoban.moverJugador(1, 0);
        }
    }

    private void registrarPartida(boolean exitoFinal) {
        if (partidaRegistrada || main.username == null) {
            return;
        }

        LogicaUsuarios lu = new LogicaUsuarios();
        int intentos = juegoSokoban.getEmpujes();
        long duracionMs = (long) (tiempoDeJuego * 1000L);
        int puntajeFinal = exitoFinal ? Math.max(0, score) : 0;

        lu.registrarPartida(main.username, nivelActual, puntajeFinal, intentos, duracionMs, exitoFinal);
        partidaRegistrada = true;
    }

    private void mostrarDialogoVictoria() {
        if (dialogoVictoriaMostrado) {
            return;
        }
        dialogoVictoriaMostrado = true;

        LogicaUsuarios lu = new LogicaUsuarios();
        lu.guardarScore(main.username, nivelActual, score);
        lu.marcarNivelPasado(main.username, nivelActual);
        
        // --- ÚNICO CAMBIO EN ESTE ARCHIVO ---
        lu.verificarYDesbloquearLogros(main.username);
        // --- FIN DEL CAMBIO ---

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 64;
        p.color = Color.valueOf("1E1E1E");
        BitmapFont titleFont = generator.generateFont(p);

        p.size = 40; // Tamaño legible para el mensaje
        BitmapFont messageFont = generator.generateFont(p);
        generator.dispose();

        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK,
                new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog dialogo = new Dialog("", windowStyle);

        Table wrapper = new Table();
        wrapper.pad(26);
        wrapper.defaults().pad(10);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.valueOf("1E1E1E"));
        Label titleLabel = new Label(gestorIdiomas.setTexto("game.dialogo_victoria_titulo"), titleStyle);
        wrapper.add(titleLabel).padBottom(25).row();

        int minutos = (int) tiempoDeJuego / 60;
        int segundos = (int) tiempoDeJuego % 60;
        String tiempoFormateado = String.format("%02d:%02d", minutos, segundos);
        String mensaje = gestorIdiomas.setTexto("game.dialogo_victoria_mensaje", score, juegoSokoban.getMovimientos(), juegoSokoban.getEmpujes(), tiempoFormateado);

        Label.LabelStyle messageStyle = new Label.LabelStyle(messageFont, Color.valueOf("1E1E1E"));
        Label messageLabel = new Label(mensaje, messageStyle);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);
        wrapper.add(messageLabel).width(750).padBottom(30).row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        TextButton reintentarBtn = new TextButton(gestorIdiomas.setTexto("game.dialogo_victoria_reintentar"), btnStyle);
        TextButton menuBtn = new TextButton(gestorIdiomas.setTexto("game.dialogo_victoria_menu"), btnStyle);

        Table buttonTable = new Table();
        buttonTable.defaults().width(320).height(60).pad(10);
        buttonTable.add(reintentarBtn);
        buttonTable.add(menuBtn);
        wrapper.add(buttonTable).row();

        reintentarBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialogo.hide();
                main.setScreen(new GameScreen(main, nivelActual));
            }
            
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
            
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialogo.hide();
                main.setScreen(new MenuScreen(main));
            }
            
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
            
        });

        dialogo.getContentTable().add(wrapper).prefWidth(900).prefHeight(520);
        dialogo.show(stage);
    }

    private void buildPauseMenu() {
        if (pausePanel != null) {
            pausePanel.remove();
        }

        pausePanel = new Table();
        pausePanel.setFillParent(true);
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.7f);
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
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        btnStyle.down = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        btnStyle.over = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        btnStyle.fontColor = Color.valueOf("1E1E1E");

        resumeButton = new TextButton("", btnStyle);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resume();
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        levelSelectButton = new TextButton("", btnStyle);
        levelSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resume();
                registrarPartida(false);
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LevelSelectScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        menuButton = new TextButton("", btnStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resume();
                registrarPartida(false);
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        container.add(resumeButton).width(250).height(50).pad(10).row();
        container.add(levelSelectButton).width(250).height(50).pad(10).row();
        container.add(menuButton).width(250).height(50).pad(10).row();

        pausePanel.add(container);
        stage.addActor(pausePanel);
        pausePanel.setVisible(false);
    }

    private void updatePauseMenuLanguage() {
        if (pauseTitle == null) {
            return;
        }
        pauseTitle.setText(gestorIdiomas.setTexto("pause.title"));
        resumeButton.setText(gestorIdiomas.setTexto("pause.resume"));
        levelSelectButton.setText(gestorIdiomas.setTexto("pause.level_select"));
        menuButton.setText(gestorIdiomas.setTexto("pause.main_menu"));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playGameMusic();
        transicionSuave.fadeIn(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        if (!isPaused) {
            if (!juegoSokoban.isJuegoGanado()) {
                tiempoDeJuego += delta;
                cantmoves.setText(gestorIdiomas.setTexto("game.movimientos") + juegoSokoban.getMovimientos());
                cantempujes.setText(gestorIdiomas.setTexto("game.empujes") + juegoSokoban.getEmpujes());
                timeLabel.setText(String.format(gestorIdiomas.setTexto("game.tiempo") + "%.0fs", tiempoDeJuego));
            }

            juegoSokoban.render(delta);

            if (juegoSokoban.isJuegoGanado() && !dialogoVictoriaMostrado) {

                score = scoreBase - (juegoSokoban.getMovimientos() * 5) - (int) (tiempoDeJuego * 2);
                if (score < 0) {
                    score = 0;
                }
                
                mostrarDialogoVictoria();
            }
        } else {
            juegoSokoban.renderizar();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        juegoSokoban.resize(width, height);
    }

    @Override
    public void hide() {
        if (!partidaRegistrada) {
            registrarPartida(juegoSokoban.isJuegoGanado());
        }
    }

    @Override
    public void pause() {
        isPaused = true;
        if (pausePanel != null) {
            updatePauseMenuLanguage();
            pausePanel.setVisible(true);
        }
    }

    @Override
    public void resume() {
        isPaused = false;
        if (pausePanel != null) {
            pausePanel.setVisible(false);
        }
    }

    @Override
    public void dispose() {
        if (!partidaRegistrada) {
            registrarPartida(juegoSokoban.isJuegoGanado());
        }
        juegoSokoban.dispose();
        stage.dispose();
        pixelFont.dispose();
        backgroundTexture.dispose();
    }
}