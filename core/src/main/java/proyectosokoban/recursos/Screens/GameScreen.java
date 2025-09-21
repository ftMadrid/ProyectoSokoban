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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
    private BitmapFont pixelFont, sectionFont;

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

    public GameScreen(final Main main, int nivel) {
        this.main = main;
        this.nivelActual = nivel;
        this.juegoSokoban = new Sokoban(main, nivel, main.username);
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();

        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        loadControls();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);
        parameter.size = 28;
        parameter.color = Color.valueOf("1E1E1E");
        sectionFont = generator.generateFont(parameter);
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
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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
        
        String mensaje = gestorIdiomas.setTexto("game.dialogo_victoria_mensaje", score);
        Dialog dialogo = new Dialog(gestorIdiomas.setTexto("game.dialogo_victoria_titulo"), createDialogStyle()) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    transicionSuave.fadeOutAndChangeScreen(main, stage, new GameScreen(main, nivelActual));
                } else {
                    transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
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

        Label.LabelStyle labelStyle = new Label.LabelStyle(sectionFont, Color.valueOf("1E1E1E"));
        Label.LabelStyle smallLabelStyle = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));

        container.add(new Label(gestorIdiomas.setTexto("preferences.volumen"), labelStyle)).colspan(2).left().row();
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(
            new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 1.png"))),
            new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/cursor 2.png"))));
        Slider volumeSlider = new Slider(0, 100, 1, false, sliderStyle);
        volumeSlider.setValue(main.getVolume() * 100);
        final Label volumeValue = new Label(Integer.toString((int)volumeSlider.getValue()), labelStyle);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int volume = (int) ((Slider) actor).getValue();
                volumeValue.setText(Integer.toString(volume));
                main.setVolume(volume / 100f);
            }
        });
        container.add(volumeSlider).width(200).height(18);
        container.add(volumeValue).width(50).padLeft(10).row();
        
        container.add(new Label(gestorIdiomas.setTexto("preferences.display"), labelStyle)).left().padTop(15).row();
        TextButton.TextButtonStyle cycleBtnStyle = new TextButton.TextButtonStyle();
        cycleBtnStyle.font = pixelFont;
        cycleBtnStyle.fontColor = Color.valueOf("1E1E1E");
        cycleBtnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        
        final PreferenciasScreen.CycleButton displayButton = new PreferenciasScreen.CycleButton(new String[]{"Fullscreen", "Windowed", "Borderless"}, cycleBtnStyle);
        int displayMode = new LogicaUsuarios().getPreferencias(main.username)[8];
        displayButton.setIndex(displayMode);
        displayButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                main.applyDisplayMode(displayButton.getIndex());
            }
        });
        container.add(displayButton).width(180).height(38).left().row();

        container.add(new Label(gestorIdiomas.setTexto("preferences.controles"), labelStyle)).padTop(15).row();

        Table controlsTable = new Table();
        TextButton.TextButtonStyle keyBtnStyle = new TextButton.TextButtonStyle();
        keyBtnStyle.font = pixelFont;
        keyBtnStyle.fontColor = Color.valueOf("1E1E1E");
        keyBtnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/botonespeq.png")));

        controlsTable.add(new Label(gestorIdiomas.setTexto("preferences.arriba"), smallLabelStyle)).right().padRight(5);
        controlsTable.add(new TextButton(Input.Keys.toString(keyUp), keyBtnStyle)).width(60).height(30);
        controlsTable.add(new Label(gestorIdiomas.setTexto("preferences.abajo"), smallLabelStyle)).right().padLeft(10).padRight(5);
        controlsTable.add(new TextButton(Input.Keys.toString(keyDown), keyBtnStyle)).width(60).height(30).row();
        controlsTable.add(new Label(gestorIdiomas.setTexto("preferences.izquierda"), smallLabelStyle)).right().padRight(5);
        controlsTable.add(new TextButton(Input.Keys.toString(keyLeft), keyBtnStyle)).width(60).height(30);
        controlsTable.add(new Label(gestorIdiomas.setTexto("preferences.derecha"), smallLabelStyle)).right().padLeft(10).padRight(5);
        controlsTable.add(new TextButton(Input.Keys.toString(keyRight), keyBtnStyle)).width(60).height(30);
        container.add(controlsTable).left().row();

        Table buttonTable = new Table();
        buttonTable.padTop(20);

        TextButton resumeButton = new TextButton("RESUMIR", cycleBtnStyle);
        resumeButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                resume();
            }
        });
        
        TextButton menuButton = new TextButton("MENU PRINCIPAL", cycleBtnStyle);
        menuButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                resume();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        });
        
        buttonTable.add(resumeButton).width(180).height(40).padRight(10);
        buttonTable.add(menuButton).width(180).height(40);
        
        container.add(buttonTable).colspan(2).padTop(20).row();
        pausePanel.add(container);
        stage.addActor(pausePanel);
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playGameMusic();
        transicionSuave.fadeIn(stage);
    }

    @Override public void render(float delta) {
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

            juegoSokoban.actualizar(delta);
            
            if (juegoSokoban.isJuegoGanado()) {
                mostrarDialogoVictoria();
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        
        juegoSokoban.render(delta);

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
        buildPauseMenu();
    }

    @Override public void resume() {
        isPaused = false;
        if (pausePanel != null) {
            pausePanel.remove();
        }
    }

    @Override public void dispose() {
        juegoSokoban.dispose();
        stage.dispose();
        pixelFont.dispose();
        sectionFont.dispose();
        backgroundTexture.dispose();
    }
}