/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class RegisterScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;

    private TextField usernameField;
    private TextField fullnameField;
    private TextField passwordField;
    private Label messageLabel;

    public RegisterScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();

        createUI();
    }

    private void createUI() {
        Table table = new Table(skin);
        table.setBackground("default-pane");
        table.pad(20);
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Registro de Usuario", skin);
        title.setFontScale(2.0f);
        table.add(title).padBottom(40).colspan(2).row();

        table.add(new Label("Usuario:", skin)).padRight(10);
        usernameField = new TextField("", skin);
        table.add(usernameField).width(300).padBottom(10).row();

        table.add(new Label("Nombre Completo:", skin)).padRight(10);
        fullnameField = new TextField("", skin);
        table.add(fullnameField).width(300).padBottom(10).row();

        table.add(new Label("Contraseña:", skin)).padRight(10);
        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        table.add(passwordField).width(300).padBottom(10).row();

        TextButton registerButton = new TextButton("Registrarse", skin);
        table.add(registerButton).colspan(2).size(300, 50).padTop(20).row();

        messageLabel = new Label("", skin);
        table.add(messageLabel).colspan(2).padTop(10).row();

        Table loginLinkTable = new Table();
        Label alreadyAccountLabel = new Label("Ya tienes usuario?", skin);
        loginLinkTable.add(alreadyAccountLabel).padRight(5);

        TextButton loginButton = new TextButton("Login", skin, "toggle");
        loginButton.getLabel().setColor(Color.CYAN);
        loginLinkTable.add(loginButton);

        table.add(loginLinkTable).colspan(2).padTop(20);


        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (userLogic.CrearUsuario(usernameField.getText(), fullnameField.getText(), passwordField.getText())) {
                    messageLabel.setText("Registro exitoso. Ahora puedes iniciar sesion.");
                } else {
                    messageLabel.setText("El nombre de usuario ya existe o es invalido.");
                }
            }
        });

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new LoginScreen(main));
                dispose();
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