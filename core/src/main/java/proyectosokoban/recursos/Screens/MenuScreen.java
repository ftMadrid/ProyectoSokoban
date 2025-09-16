package proyectosokoban.recursos.Screens;

import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {

    final Main main;
    private Stage stage;
    private Skin skin;
    private Music musicafondo;
    private SpriteBatch batch;
    private Texture fondoTexture;
    
    private Table preferencesTable;
    private Slider volumenSlider;
    private TextButton muteButton;
    private LogicaUsuarios userLogic;
    private float lastVolume;
    private boolean isMuted;

    public MenuScreen(final Main main) {
        this.main = main;
        this.userLogic = new LogicaUsuarios();

        batch = new SpriteBatch();
        fondoTexture = new Texture("mainfondo.png");

        musicafondo = Gdx.audio.newMusic(Gdx.files.internal("main.mp3"));
        musicafondo.setLooping(true);
        
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        crearInterfaz();
        cargarPreferencias();
        musicafondo.play();
    }

    private void crearInterfaz() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
        
        Texture botonTexture = new Texture(Gdx.files.internal("boton.png"));
        Drawable drawableBoton = new TextureRegionDrawable(new TextureRegion(botonTexture));

        TextButton.TextButtonStyle botonStyle = new TextButton.TextButtonStyle();
        botonStyle.up = drawableBoton;
        botonStyle.down = drawableBoton;
        botonStyle.font = skin.getFont("default-font");

        TextButton botonNiveles = new TextButton("SELECCIONAR NIVEL", botonStyle);
        TextButton botonAmigos = new TextButton("AMIGOS", botonStyle);
        TextButton botonSalir = new TextButton("SALIR", botonStyle);

        mainTable.add(botonNiveles).size(270, 80).padBottom(20).row();
        mainTable.add(botonAmigos).size(270, 80).padBottom(20).row();
        mainTable.add(botonSalir).size(270, 80).padBottom(20).row();

        botonNiveles.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new LevelSelectScreen(main));
                dispose();
            }
        });
        
        botonAmigos.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new AmigosScreen(main));
                dispose();
            }
        });

        botonSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        
        // Controles de volumen en un panel que se muestra/oculta
        preferencesTable = new Table(skin);
        preferencesTable.setBackground("default-pane");
        preferencesTable.pad(10);
        preferencesTable.setVisible(false);

        preferencesTable.add(new Label("Volumen:", skin)).padRight(10);
        volumenSlider = new Slider(0, 100, 1, false, skin);
        preferencesTable.add(volumenSlider).width(200);

        muteButton = new TextButton("Mute: OFF", skin);
        preferencesTable.add(muteButton).padLeft(20);

        preferencesTable.setPosition(stage.getWidth() - preferencesTable.getWidth() - 20, stage.getHeight() - preferencesTable.getHeight() - 20);
        stage.addActor(preferencesTable);

        TextButton preferencesButton = new TextButton("Preferencias", skin);
        preferencesButton.setPosition(stage.getWidth() - preferencesButton.getWidth() - 10, stage.getHeight() - preferencesButton.getHeight() - 10);
        preferencesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                preferencesTable.setVisible(!preferencesTable.isVisible());
            }
        });
        stage.addActor(preferencesButton);

        volumenSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volumen = volumenSlider.getValue() / 100f;
                musicafondo.setVolume(volumen);
                if (volumen > 0) {
                    isMuted = false;
                    muteButton.setText("Mute: OFF");
                } else {
                    isMuted = true;
                    muteButton.setText("Mute: ON");
                }
                guardarPreferencias();
            }
        });

        muteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isMuted = !isMuted;
                if (isMuted) {
                    lastVolume = musicafondo.getVolume();
                    musicafondo.setVolume(0);
                    muteButton.setText("Mute: ON");
                    volumenSlider.setValue(0);
                } else {
                    if (lastVolume == 0) lastVolume = 0.8f;
                    musicafondo.setVolume(lastVolume);
                    muteButton.setText("Mute: OFF");
                    volumenSlider.setValue((int)(lastVolume * 100));
                }
                guardarPreferencias();
            }
        });
    }

    private void cargarPreferencias() {
        if (main.username != null) {
            int[] prefs = userLogic.getPreferencias(main.username);
            int volumen = prefs[0];
            boolean mute = prefs[3] == 1;
            
            lastVolume = volumen / 100f;
            isMuted = mute;

            musicafondo.setVolume(mute ? 0 : lastVolume);
            volumenSlider.setValue(mute ? 0 : volumen);
            muteButton.setText(mute ? "Mute: ON" : "Mute: OFF");
        } else {
            lastVolume = 0.8f;
            isMuted = false;
            volumenSlider.setValue(80);
            musicafondo.setVolume(lastVolume);
            muteButton.setText("Mute: OFF");
        }
    }

    private void guardarPreferencias() {
        if (main.username != null) {
            int volumen = (int) volumenSlider.getValue();
            userLogic.setPreferencias(main.username, volumen, (byte) 0, (byte) 0, isMuted);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(fondoTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

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
        batch.dispose();
        fondoTexture.dispose();
        stage.dispose();
        skin.dispose();
        musicafondo.dispose();
    }
}