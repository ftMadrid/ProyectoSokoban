package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.SelectorNiveles.MapaSelector;
import proyectosokoban.recursos.SelectorNiveles.SelectorNiveles;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class LevelSelectScreen implements Screen {

    final Main main;
    private Stage stage;
    private GestorIdiomas gestorIdiomas;

    private MapaActor mapaActor;
    private int keyUp, keyDown, keyLeft, keyRight;
    private boolean active = true;
    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont messageFont;
    private Texture buttonTexture;
    private Texture backgroundTexture;

    private Table pausePanel;
    private boolean isPaused = false;

    private Label pauseTitle;
    private TextButton resumeButton, backToMenuButton;

    // nuevos elementos para titulo y mensaje
    private Label titleLabel;
    private Label enterLevelMessage;
    private Table messagePanel;

    public LevelSelectScreen(final Main main) {
        this.main = main;
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();
        this.backgroundTexture = new Texture(Gdx.files.internal("background3.png"));
        cargarControles();
        inicializar();
    }

    private void cargarControles() {
        this.keyUp = main.keyUp;
        this.keyDown = main.keyDown;
        this.keyLeft = main.keyLeft;
        this.keyRight = main.keyRight;
    }

    private void inicializar() {
        stage = new Stage(new ScreenViewport());
        mapaActor = new MapaActor(main.username);
        stage.addActor(mapaActor);

        buttonTexture = new Texture(Gdx.files.internal("ui/button1.png"));
        inicializarFuentes();
        inicializarUI();
    }

    private void inicializarFuentes() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 24;
        p.color = Color.valueOf("1E1E1E");
        font = generator.generateFont(p);

        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = 40;
        titleParam.color = Color.WHITE;
        titleParam.borderColor = Color.BLACK;
        titleParam.borderWidth = 2;
        titleParam.magFilter = Texture.TextureFilter.Nearest;
        titleParam.minFilter = Texture.TextureFilter.Nearest;
        titleFont = generator.generateFont(titleParam);

        FreeTypeFontGenerator.FreeTypeFontParameter messageParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        messageParam.size = 36;
        messageParam.color = Color.YELLOW;
        messageParam.borderColor = Color.BLACK;
        messageParam.borderWidth = 2;
        messageParam.shadowColor = Color.BLACK;
        messageParam.shadowOffsetX = 1;
        messageParam.shadowOffsetY = 1;
        messageFont = generator.generateFont(messageParam);

        generator.dispose();
    }

    private void inicializarUI() {
        Table tablaPrincipal = new Table();
        tablaPrincipal.setFillParent(true);
        stage.addActor(tablaPrincipal);

        Table panelSuperior = new Table();
        panelSuperior.setFillParent(true);
        panelSuperior.top();

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        titleLabel = new Label(gestorIdiomas.setTexto("level_selector.title"), titleStyle);

        panelSuperior.add(titleLabel).expandX().center().pad(20).top().row();

        stage.addActor(panelSuperior);
        tablaPrincipal.add(mapaActor).expand().fill();

        createMessagePanel();

        buildPauseMenu();
    }

    private void createMessagePanel() {
        messagePanel = new Table();
        messagePanel.setFillParent(true);
        messagePanel.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);

        Label.LabelStyle messageStyle = new Label.LabelStyle(messageFont, Color.YELLOW);
        enterLevelMessage = new Label(gestorIdiomas.setTexto("level_selector.enter"), messageStyle);

        messagePanel.add(enterLevelMessage).center();
        stage.addActor(messagePanel);
        messagePanel.setVisible(false);
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

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.valueOf("1E1E1E"));
        pauseTitle = new Label("", labelStyle);
        container.add(pauseTitle).colspan(2).center().padBottom(20).row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(buttonTexture);
        btnStyle.font = font;

        resumeButton = new TextButton("", btnStyle);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resume();
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

        backToMenuButton = new TextButton("", btnStyle);
        backToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!active) {
                    return;
                }
                active = false;
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

        container.add(resumeButton).width(240).height(50).pad(10).row();
        container.add(backToMenuButton).width(240).height(50).pad(10).row();

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
        backToMenuButton.setText(gestorIdiomas.setTexto("pause.main_menu"));
    }

    public void showEnterLevelMessage(boolean show) {
        if (messagePanel != null) {
            messagePanel.setVisible(show && !isPaused);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isPaused) {
                resume();
            } else {
                pause();
            }
        }

        if (!isPaused) {
            mapaActor.handleInput(delta);
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        mapaActor.resize(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playSelectorMusic();
        transicionSuave.fadeIn(stage);
        active = true;
    }

    @Override
    public void hide() {
        active = false;
    }

    @Override
    public void pause() {
        isPaused = true;
        if (pausePanel != null) {
            updatePauseMenuLanguage();
            pausePanel.setVisible(true);
        }
        // ocultar mensaje cuando esta pausado (sisisis)
        showEnterLevelMessage(false);
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
        if (stage != null) {
            stage.dispose();
        }
        if (mapaActor != null) {
            mapaActor.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (messageFont != null) {
            messageFont.dispose();
        }
        if (buttonTexture != null) {
            buttonTexture.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    class MapaActor extends Actor {

        private OrthographicCamera camera;
        private Viewport viewport;
        private SpriteBatch batch;
        private MapaSelector mapa;
        private SelectorNiveles selector;
        private final int TILE = 64;
        private float tiempoDesdeUltimoMovimiento = 0f;
        private final float delayMovimiento = 0.2f;

        public MapaActor(String username) {
            batch = new SpriteBatch();
            mapa = new MapaSelector(TILE, username);
            selector = new SelectorNiveles(7, 3, TILE, mapa);

            float worldWidth = mapa.getColumnas() * TILE;
            float worldHeight = mapa.getFilas() * TILE;

            camera = new OrthographicCamera();
            viewport = new StretchViewport(worldWidth, worldHeight, camera);
        }

        public void resize(int width, int height) {
            viewport.update(width, height, true);
        }

        public void handleInput(float delta) {
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
                    if (nivelSeleccionado > 0 && mapa.isNivelDesbloqueado(nivelSeleccionado)) {
                        active = false;
                        transicionSuave.fadeOutAndChangeScreen(main, stage, new GameScreen(main, nivelSeleccionado));
                    }
                }
                if (seMovio) {
                    tiempoDesdeUltimoMovimiento = 0f;
                }
            }

            // Verificar si el personaje está sobre un nivel desbloqueado
            checkIfOnLevel();
        }

        private void checkIfOnLevel() {
            int nivelSeleccionado = selector.getNivelSeleccionado();
            boolean mostrarMensaje = (nivelSeleccionado > 0 && mapa.isNivelDesbloqueado(nivelSeleccionado));
            showEnterLevelMessage(mostrarMensaje);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (active) {
                selector.actualizar(delta);
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.end();

            viewport.apply(true);
            this.batch.setProjectionMatrix(camera.combined);

            this.batch.begin();
            mapa.render(this.batch);
            selector.render(this.batch);
            this.batch.end();

            batch.begin();
        }

        public void dispose() {
            if (batch != null) {
                batch.dispose();
            }
            if (mapa != null) {
                mapa.dispose();
            }
            if (selector != null) {
                selector.dispose();
            }
        }
    }
}
