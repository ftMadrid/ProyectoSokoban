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

import java.util.List;

public class AmigosScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;

    private TextField usernameField;
    private Label messageLabel;
    private Label amigosLabel;

    public AmigosScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();

        createUI();
        updateFriendsList();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Gestionar Amigos", skin);
        title.setFontScale(2.0f);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Nombre de usuario del amigo");

        TextButton addButton = new TextButton("Agregar Amigo", skin);
        TextButton backButton = new TextButton("Volver al Menú", skin);

        messageLabel = new Label("", skin);
        amigosLabel = new Label("Amigos:", skin);

        table.add(title).padBottom(20).row();
        table.add(usernameField).width(300).padBottom(10).row();
        table.add(addButton).size(200, 50).padBottom(10).row();
        table.add(messageLabel).padTop(10).row();
        table.add(amigosLabel).padTop(20).row();
        table.add(backButton).size(200, 50).padTop(20);

        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (userLogic.agregarAmigo(main.username, usernameField.getText())) {
                    messageLabel.setText("Amigo agregado con éxito.");
                    updateFriendsList();
                } else {
                    messageLabel.setText("No se pudo agregar al amigo.");
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new MenuScreen(main));
                dispose();
            }
        });
    }

    private void updateFriendsList() {
        List<String> amigos = userLogic.listarAmigos(main.username);
        StringBuilder sb = new StringBuilder("Amigos:\n");
        if (amigos.isEmpty()) {
            sb.append("No tienes amigos agregados.");
        } else {
            for (String amigo : amigos) {
                sb.append("- ").append(amigo).append("\n");
            }
        }
        amigosLabel.setText(sb.toString());
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