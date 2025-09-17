package proyectosokoban.recursos.Screens;

import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private SpriteBatch batch;
    private Texture fondoTexture;

    public MenuScreen(final Main main) {
        this.main = main;

        batch = new SpriteBatch();
        fondoTexture = new Texture("mainfondo.png");

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        crearInterfaz();
        main.musicafondo.play();
    }

    private void crearInterfaz() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        TextButton botonNiveles = new TextButton("SELECCIONAR NIVEL", skin);
        TextButton botonAmigos = new TextButton("AMIGOS", skin);
        TextButton botonSalir = new TextButton("SALIR", skin);
        TextButton botonPreferencias = new TextButton("PREFERENCIAS", skin);

        float buttonWidth = 270;
        float buttonHeight = 80;
        float padding = 20;

        botonNiveles.setSize(buttonWidth, buttonHeight);
        botonAmigos.setSize(buttonWidth, buttonHeight);
        botonPreferencias.setSize(buttonWidth, buttonHeight);
        botonSalir.setSize(buttonWidth, buttonHeight);

        float totalHeight = (buttonHeight + padding) * 4 - padding;
        float startY = (screenHeight - totalHeight) / 2 + (buttonHeight + padding) * 3;
        float centerX = (screenWidth - buttonWidth) / 2;

        botonNiveles.setPosition(centerX, startY);
        botonAmigos.setPosition(centerX, startY - (buttonHeight + padding));
        botonPreferencias.setPosition(centerX, startY - 2 * (buttonHeight + padding));
        botonSalir.setPosition(centerX, startY - 3 * (buttonHeight + padding));

        stage.addActor(botonNiveles);
        stage.addActor(botonAmigos);
        stage.addActor(botonPreferencias);
        stage.addActor(botonSalir);

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
    }
}