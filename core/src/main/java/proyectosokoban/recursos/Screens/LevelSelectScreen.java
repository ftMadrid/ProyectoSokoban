/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class LevelSelectScreen implements Screen {

    final Main main;
    private Stage stage;
    private Skin skin;
    private Label levelLabel;
    private int currentLevel = 1;
    private final int MAX_LEVEL = 7;
    private int ultimoNivelDesbloqueado;
    private BitmapFont pixelFont;

    public LevelSelectScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        LogicaUsuarios lu = new LogicaUsuarios();
        ultimoNivelDesbloqueado = lu.ultimoNivelDesbloqueado(main.username);
        if (ultimoNivelDesbloqueado == 0) ultimoNivelDesbloqueado = 1;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32; 
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);
        generator.dispose();

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, pixelFont.getColor());
        levelLabel = new Label("Nivel " + currentLevel, labelStyle);
        levelLabel.setFontScale(1.5f);
        
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = pixelFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));


        TextButton leftButton = new TextButton("<", skin);
        leftButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLevel > 1) {
                    currentLevel--;
                    updateLevelLabel();
                }
            }
        });

        TextButton rightButton = new TextButton(">", skin);
        rightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLevel < MAX_LEVEL && currentLevel < ultimoNivelDesbloqueado) {
                    currentLevel++;
                    updateLevelLabel();
                }
            }
        });

        TextButton startButton = new TextButton("Iniciar Juego", buttonStyle);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentLevel <= ultimoNivelDesbloqueado) {
                    main.setScreen(new GameScreen(main, currentLevel));
                    dispose();
                }
            }
        });

        TextButton backButton = new TextButton("Volver al Menu", buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new MenuScreen(main));
                dispose();
            }
        });

        table.add(leftButton).size(100, 50).pad(10);
        table.add(levelLabel).pad(10);
        table.add(rightButton).size(100, 50).pad(10).row();
        table.add(startButton).colspan(3).size(200, 60).pad(20).row();
        table.add(backButton).colspan(3).size(200, 60).pad(20);
    }

    private void updateLevelLabel() {
        levelLabel.setText("Nivel " + currentLevel);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && currentLevel > 1) {
            currentLevel--;
            updateLevelLabel();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && currentLevel < MAX_LEVEL && currentLevel < ultimoNivelDesbloqueado) {
            currentLevel++;
            updateLevelLabel();
        }
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
    }
}