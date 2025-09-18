/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class LoginScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;
    private Texture backgroundTexture;

    private TextField usernameField;
    private TextField passwordField;
    private Label messageLabel;
    private CheckBox showPasswordCheckBox;

    public LoginScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Drawable tableBackground = skin.newDrawable("white", 0, 0, 0, 0.5f);
        table.setBackground(tableBackground);
        table.pad(20);

        Label title = new Label("Sokoban - Ingresar", skin);
        title.setFontScale(2.0f);
        table.add(title).padBottom(40).colspan(2).row();

        table.add(new Label("Usuario:", skin)).left().padRight(10);
        usernameField = new TextField("", skin);
        table.add(usernameField).width(300).padBottom(10).row();

        table.add(new Label("Contrasena:", skin)).left().padRight(10);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        table.add(passwordField).width(300).padBottom(10).row();

        showPasswordCheckBox = new CheckBox(" Mostrar contrasena", skin);
        table.add(showPasswordCheckBox).colspan(2).left().padBottom(20).row();

        TextButton loginButton = new TextButton("Iniciar Sesion", skin);
        table.add(loginButton).colspan(2).size(300, 50).padTop(10).row();

        messageLabel = new Label("", skin);
        table.add(messageLabel).colspan(2).padTop(10).row();

        Table registerLinkTable = new Table();
        Label noAccountLabel = new Label("No tienes cuenta?", skin);
        registerLinkTable.add(noAccountLabel).padRight(5);

        TextButton registerButton = new TextButton("Registrate", skin, "toggle");
        registerButton.getLabel().setColor(Color.CYAN);
        registerLinkTable.add(registerButton);

        table.add(registerLinkTable).colspan(2).padTop(20);

        showPasswordCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                passwordField.setPasswordMode(!showPasswordCheckBox.isChecked());
            }
        });

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (userLogic.login(usernameField.getText(), passwordField.getText())) {
                    main.username = usernameField.getText();
                    main.setScreen(new MenuScreen(main));
                    dispose();
                } else {
                    messageLabel.setText("Usuario o contrasena incorrectos.");
                }
            }
        });

        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new RegisterScreen(main));
                dispose();
            }
        });
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
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
        backgroundTexture.dispose();
    }
}