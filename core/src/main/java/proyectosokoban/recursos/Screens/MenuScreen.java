package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class MenuScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private BitmapFont pixelFont;
    private BitmapFont titleFont;
    private Texture backgroundTexture;

    public MenuScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("background3.png"));
        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);
        
        parameter.size = 90;
        titleFont = generator.generateFont(parameter);
        
        generator.dispose();
        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.valueOf("F5F5DC"));
        Label title = new Label("Sokoban", titleStyle);
        table.add(title).padBottom(50).row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = pixelFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));

        TextButton playButton = new TextButton("Jugar", buttonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LevelSelectScreen(main));
            }
        });
        table.add(playButton).size(300, 60).padBottom(20).row();

        TextButton friendsButton = new TextButton("Amigos", buttonStyle);
        friendsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new AmigosScreen(main));
            }
        });
        table.add(friendsButton).size(300, 60).padBottom(20).row();
        
        TextButton preferencesButton = new TextButton("Preferencias", buttonStyle);
        preferencesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new PreferenciasScreen(main));
            }
        });
        table.add(preferencesButton).size(300, 60).padBottom(20).row();

        TextButton exitButton = new TextButton("Cerrar Sesion", buttonStyle);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.username = null;
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LoginScreen(main));
            }
        });
        table.add(exitButton).size(300, 60).padBottom(20).row();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playMenuMusic();
        transicionSuave.fadeIn(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        pixelFont.dispose();
        titleFont.dispose();
        backgroundTexture.dispose();
    }
}