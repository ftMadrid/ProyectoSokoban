package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;

public class MenuScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;

    public MenuScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label title = new Label("Sokoban", skin);
        title.setFontScale(3.0f);
        table.add(title).padBottom(50).row();

        // Boton para Jugar
        TextButton playButton = new TextButton("Jugar", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new LevelSelectScreen(main));
                dispose();
            }
        });
        table.add(playButton).size(300, 60).padBottom(20).row();

        // Boton para Amigos
        TextButton friendsButton = new TextButton("Amigos", skin);
        friendsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new AmigosScreen(main));
                dispose();
            }
        });
        table.add(friendsButton).size(300, 60).padBottom(20).row();
        
        // Boton para Preferencias
        TextButton preferencesButton = new TextButton("Preferencias", skin);
        preferencesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new PreferenciasScreen(main));
                dispose();
            }
        });
        table.add(preferencesButton).size(300, 60).padBottom(20).row();

        // Boton para Salir
        TextButton exitButton = new TextButton("Cerrar Sesion", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.username = null; // Limpiar el usuario logueado
                main.setScreen(new LoginScreen(main));
                dispose();
            }
        });
        table.add(exitButton).size(300, 60).padBottom(20).row();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // Inicia la musica del menu cada vez que se muestra esta pantalla
        main.playMenuMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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
    public void hide() {
        // La musica se detendra cuando la nueva pantalla inicie la suya
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}