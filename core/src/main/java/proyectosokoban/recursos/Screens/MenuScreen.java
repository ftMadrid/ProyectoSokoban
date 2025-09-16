package proyectosokoban.recursos.Screens;

import proyectosokoban.recursos.Main;
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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
        musicafondo.setVolume(0.8f);
        musicafondo.play();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        crearInterfaz();
    }

    private void crearInterfaz() {
        Texture botonTexture = new Texture(Gdx.files.internal("boton.png"));
        Drawable drawableBoton = new TextureRegionDrawable(new TextureRegion(botonTexture));

        TextButton.TextButtonStyle botonStyle = new TextButton.TextButtonStyle();
        botonStyle.up = drawableBoton;
        botonStyle.down = drawableBoton;
        botonStyle.font = skin.getFont("default-font");

        TextButton botonNiveles = new TextButton("SELECCIONAR NIVEL", botonStyle);
        botonNiveles.setBounds(310, 250, 270, 80);
        botonNiveles.setTransform(true);
        botonNiveles.setOrigin(botonNiveles.getWidth() / 2f, botonNiveles.getHeight() / 2f);

        botonNiveles.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                botonNiveles.addAction(
                        Actions.sequence(
                                Actions.scaleTo(0.9f, 0.9f, 0.1f),
                                Actions.scaleTo(1f, 1f, 0.1f),
                                Actions.run(() -> {
                                    Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
                                    main.setScreen(new LevelSelectScreen(main));
                                    dispose();
                                })
                        )
                );
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Hand);
                botonNiveles.addAction(Actions.scaleTo(1.1f, 1.1f, 0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
                botonNiveles.addAction(Actions.scaleTo(1f, 1f, 0.2f));
            }
        });
        stage.addActor(botonNiveles);
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