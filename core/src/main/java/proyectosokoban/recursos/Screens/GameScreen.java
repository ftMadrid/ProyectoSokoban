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
    private Skin skin;

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

    // flag para evitar dobles escrituras en historial
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

        skin = new Skin(Gdx.files.internal("uiskin.json"));
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

        // score no se mostrará durante el juego: lo dejamos vacío y oculto visualmente.
        scoreLabel = new Label("", labelStyle);
        scoreLabel.setVisible(false);

        timeLabel = new Label(gestorIdiomas.setTexto("game.tiempo") + "0s", labelStyle);

        panel.add(cantmoves).expandX().left().padLeft(20);
        panel.add(cantempujes).expandX().left().padLeft(20);
        // dejamos el hueco del score para no romper el layout, pero invisible
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

    // Guardado en historial. Si no hubo victoria, score = 0.
    private void registrarPartida(boolean exitoFinal) {
        if (partidaRegistrada || main.username == null) return;

        LogicaUsuarios lu = new LogicaUsuarios();
        int intentos = juegoSokoban.getEmpujes(); // tu métrica
        long duracionMs = (long) (tiempoDeJuego * 1000L);

        int puntajeFinal = exitoFinal
                ? Math.max(0, score)  // score ya se calcula justo al ganar
                : 0;                   // si no terminaste, no hay score

        lu.registrarPartida(main.username, nivelActual, puntajeFinal, intentos, duracionMs, exitoFinal);
        partidaRegistrada = true;
    }

    private void mostrarDialogoVictoria() {
        int minutos = (int) tiempoDeJuego / 60;
        int segundos = (int) tiempoDeJuego % 60;
        String tiempoFormateado = String.format("%02d:%02d", minutos, segundos);
        String mensaje = gestorIdiomas.setTexto("game.dialogo_victoria_mensaje", score, juegoSokoban.getMovimientos(), juegoSokoban.getEmpujes(), tiempoFormateado);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = pixelFont;
        titleStyle.fontColor = Color.GREEN;

        // Crear un estilo personalizado para el diálogo con fondo más ancho
        Window.WindowStyle dialogStyle = new Window.WindowStyle();
        dialogStyle.titleFont = pixelFont;
        dialogStyle.titleFontColor = Color.WHITE;

        // Crear fondo para el diálogo
        Pixmap dialogBg = new Pixmap(500, 300, Pixmap.Format.RGBA8888);
        dialogBg.setColor(0.1f, 0.1f, 0.1f, 0.95f); // Fondo oscuro semitransparente
        dialogBg.fill();
        dialogStyle.background = new TextureRegionDrawable(new TextureRegion(new Texture(dialogBg)));
        dialogBg.dispose();

        Dialog dialogo = new Dialog("", dialogStyle) {
            @Override
            protected void result(Object obj) {
                if (obj != null) {
                    boolean reintentar = (Boolean) obj;
                    if (reintentar) {
                        transicionSuave.fadeOutAndChangeScreen(main, stage, new GameScreen(main, nivelActual));
                    } else {
                        transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
                    }
                }
            }
        };

        // Configurar padding y espaciado para hacerlo más amplio
        dialogo.getContentTable().defaults().pad(15);
        dialogo.getButtonTable().defaults().pad(15);

        Label titleLabel = new Label(gestorIdiomas.setTexto("game.dialogo_victoria_titulo"), titleStyle);
        titleLabel.setFontScale(2.0f); // Título un poco más grande
        dialogo.getContentTable().add(titleLabel).colspan(2).center().padBottom(20);
        dialogo.getContentTable().row();

        // Crear estilo para el mensaje con wrap para texto largo
        Label.LabelStyle messageStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        Label messageLabel = new Label(mensaje, messageStyle);
        messageLabel.setFontScale(1.4f);
        messageLabel.setAlignment(Align.center);
        messageLabel.setWrap(true); // Permitir que el texto se ajuste

        // Hacer el mensaje más ancho
        dialogo.getContentTable().add(messageLabel).colspan(2).width(600).center().padBottom(20);
        dialogo.getContentTable().row();

        TextButton reintentarBtn = new TextButton(gestorIdiomas.setTexto("game.dialogo_victoria_reintentar"), skin);
        TextButton menuBtn = new TextButton(gestorIdiomas.setTexto("game.dialogo_victoria_menu"), skin);

        // Hacer botones más grandes
        reintentarBtn.getLabel().setFontScale(1.5f);
        menuBtn.getLabel().setFontScale(1.5f);

        reintentarBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialogo.hide();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new GameScreen(main, nivelActual));
            }
        });

        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialogo.hide();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        });

        // Añadir más espacio entre botones
        dialogo.getButtonTable().defaults().pad(15).minWidth(150).minHeight(50);
        dialogo.getButtonTable().add(reintentarBtn).padRight(30);
        dialogo.getButtonTable().add(menuBtn).padLeft(30);

        dialogo.show(stage);

        // Hacer el diálogo más ancho y alto
        dialogo.setSize(700, 350); // Aumentado el ancho y alto
        dialogo.setPosition(
                (Gdx.graphics.getWidth() - dialogo.getWidth()) / 2,
                (Gdx.graphics.getHeight() - dialogo.getHeight()) / 2
        );

        stage.setKeyboardFocus(dialogo);
        stage.setScrollFocus(dialogo);
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
        btnStyle.fontColor = Color.valueOf("1E1E1E");
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        resumeButton = new TextButton("", btnStyle);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resume();
            }
        });

        levelSelectButton = new TextButton("", btnStyle);
        levelSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resume();
                registrarPartida(false); // salida sin terminar
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LevelSelectScreen(main));
            }
        });

        menuButton = new TextButton("", btnStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resume();
                registrarPartida(false); // salida sin terminar
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

<<<<<<< HEAD
    private void updatePauseMenuLanguage(){
        if(pauseTitle == null) return;
=======
    private void updatePauseMenuLanguage() {
        if (pauseTitle == null) {
            return;
        }
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
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
                // durante el juego: solo tiempo y movimientos
                tiempoDeJuego += delta;
<<<<<<< HEAD
=======
                score = scoreBase - (juegoSokoban.getMovimientos() * 5) - (int) (tiempoDeJuego * 2);
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
                cantmoves.setText(gestorIdiomas.setTexto("game.movimientos") + juegoSokoban.getMovimientos());
                cantempujes.setText(gestorIdiomas.setTexto("game.empujes") + juegoSokoban.getEmpujes());
                timeLabel.setText(String.format(gestorIdiomas.setTexto("game.tiempo") + "%.0fs", tiempoDeJuego));
                // score NO se actualiza ni se muestra
            }

            juegoSokoban.render(delta);

<<<<<<< HEAD
            if (juegoSokoban.isJuegoGanado()) {
                // calcula el score SOLO cuando ganas, antes del diálogo
                score = scoreBase - (juegoSokoban.getMovimientos() * 5) - (int)(tiempoDeJuego * 2);
                if (score < 0) score = 0;
                // para el historial en hide/dispose, el flag usará este score
=======
            if (juegoSokoban.isJuegoGanado() && !dialogoVictoriaMostrado) {
                dialogoVictoriaMostrado = true;

                LogicaUsuarios lu = new LogicaUsuarios();
                lu.guardarScore(main.username, nivelActual, Math.max(0, score));
                lu.marcarNivelPasado(main.username, nivelActual);
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
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

<<<<<<< HEAD
    @Override public void hide() {
        // Si se abandona la pantalla por otra vía, registra según estado
        if (!partidaRegistrada) {
            registrarPartida(juegoSokoban.isJuegoGanado());
        }
=======
    @Override
    public void hide() {
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
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

<<<<<<< HEAD
    @Override public void dispose() {
        if (!partidaRegistrada) {
            registrarPartida(juegoSokoban.isJuegoGanado());
        }
=======
    @Override
    public void dispose() {
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
        juegoSokoban.dispose();
        stage.dispose();
        pixelFont.dispose();
        backgroundTexture.dispose();
    }
}
