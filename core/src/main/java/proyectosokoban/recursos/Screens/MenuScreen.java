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

    public MenuScreen(final Main main) {
        this.main = main;

        batch = new SpriteBatch();
        fondoTexture = new Texture("mainfondo.png");

        musicafondo = Gdx.audio.newMusic(Gdx.files.internal("main.mp3"));
        musicafondo.setLooping(true);
        musicafondo.play();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        crearInterfaz();
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
        TextButton botonPreferencias = new TextButton("PREFERENCIAS", botonStyle);

        mainTable.add(botonNiveles).size(270, 80).padBottom(20).row();
        mainTable.add(botonAmigos).size(270, 80).padBottom(20).row();
        mainTable.add(botonPreferencias).size(270, 80).padBottom(20).row();
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
        
        botonPreferencias.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new PreferenciasScreen(main));
                dispose();
            }
        });
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