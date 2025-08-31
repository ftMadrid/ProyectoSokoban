package proyectosokoban.recursos.Screens;

import proyectosokoban.recursos.Screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;

public class MenuScreen implements Screen {

    final Main main;
    private Stage stage;
    private Skin skin;

    public MenuScreen(final Main main) {
        this.main = main;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        crearInterfaz();
    }

    private void crearInterfaz() {

        BitmapFont fontGrande = new BitmapFont();
        fontGrande.getData().setScale(3.0f);

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = fontGrande;

        Label titulo = new Label("SOKOBAN", style);

        TextButton botonJugar = new TextButton("JUGAR", skin);
        botonJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
                main.setScreen(new GameScreen(main));
                dispose();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
            }

        });

        titulo.setPosition(400, 400);

        botonJugar.setBounds(420, 300, 150, 50);

        stage.addActor(titulo);
        stage.addActor(botonJugar);

    }

    @Override
    public void render(float delta) {
        
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        main.batch.begin();
        main.batch.end();
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
        stage.dispose();
        skin.dispose();
    }
}
