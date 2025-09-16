/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosokoban.recursos.Screens;

import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LoginScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;

    private TextField usernameField;
    private TextField passwordField;
    private Label messageLabel;

    public LoginScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Sokoban - Ingresar", skin);
        title.setFontScale(2.0f);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Nombre de usuario");
        passwordField = new TextField("", skin);
        passwordField.setMessageText("Contrasena");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton loginButton = new TextButton("Iniciar Sesion", skin);
        TextButton registerButton = new TextButton("Registrarse", skin);
        messageLabel = new Label("", skin);

        table.add(title).padBottom(20).row();
        table.add(usernameField).width(300).padBottom(10).row();
        table.add(passwordField).width(300).padBottom(10).row();

        Table buttonTable = new Table();
        buttonTable.add(loginButton).size(140, 50).padRight(10);
        buttonTable.add(registerButton).size(140, 50);

        table.add(buttonTable).padBottom(10).row();
        table.add(messageLabel).padTop(10);

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (userLogic.login(usernameField.getText(), passwordField.getText())) {
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
                if (userLogic.CrearUsuario(usernameField.getText(), "DefaultName", passwordField.getText())) {
                    messageLabel.setText("Registro exitoso. Inicie sesion.");
                } else {
                    messageLabel.setText("El nombre de usuario ya existe o es invalido.");
                }
            }
        });
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}