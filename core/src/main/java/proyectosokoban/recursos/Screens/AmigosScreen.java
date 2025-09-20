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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.List;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class AmigosScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;
    private BitmapFont pixelFont;

    private TextField usernameField;
    private Label messageLabel;
    private Label amigosLabel;

    public AmigosScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();
        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.valueOf("F5F5DC");
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.magFilter = Texture.TextureFilter.Nearest;
        pixelFont = generator.generateFont(parameter);
        generator.dispose();

        createUI();
        updateFriendsList();
    }

    private void createUI() {
        Table table = new Table(skin);
        table.setBackground("default-pane");
        table.pad(20);
        table.setFillParent(true);
        stage.addActor(table);

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, pixelFont.getColor());

        Label title = new Label("Gestionar Amigos", labelStyle);
        title.setFontScale(1.5f);
        table.add(title).padBottom(40).colspan(2).row();
        
        TextButton.TextButtonStyle labelButtonStyle = new TextButton.TextButtonStyle();
        labelButtonStyle.font = pixelFont;
        labelButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/users2.png"))));

        // --- Campo de Texto ---
        TextButton userLabelButton = new TextButton("USUARIO", labelButtonStyle);
        userLabelButton.setDisabled(true);
        table.add(userLabelButton).width(250).height(70).padRight(10);
        
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = new BitmapFont();
        textFieldStyle.fontColor = Color.BLACK;
        TextureRegionDrawable fieldBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/field 1.png"))));
        fieldBackground.setLeftWidth(35f);
        textFieldStyle.background = fieldBackground;
        textFieldStyle.cursor = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/cursor 1.png")));
        
        usernameField = new TextField("", textFieldStyle);
        usernameField.setMessageText("Nombre de usuario del amigo");
        usernameField.setMaxLength(15);
        table.add(usernameField).width(350).height(70).padBottom(15).row();
        
        TextButton.TextButtonStyle actionButtonStyle = new TextButton.TextButtonStyle();
        actionButtonStyle.font = pixelFont;
        actionButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));

        // --- Botones con Contorno ---
        Stack addButtonStack = new Stack();
        TextButton addButton = new TextButton("Agregar Amigo", actionButtonStyle);
        Image addButtonOutline = new Image(new Texture(Gdx.files.internal("ui/outline botones.png")));
        addButtonStack.add(addButtonOutline);
        addButtonStack.add(addButton);
        table.add(addButtonStack).colspan(2).size(300, 60).padTop(20).row();

        messageLabel = new Label("", labelStyle);
        table.add(messageLabel).colspan(2).padTop(10).row();

        amigosLabel = new Label("Amigos:", labelStyle);
        table.add(amigosLabel).colspan(2).padTop(20).row();

        Stack backButtonStack = new Stack();
        TextButton backButton = new TextButton("Volver al Menu", actionButtonStyle);
        Image backButtonOutline = new Image(new Texture(Gdx.files.internal("ui/outline botones.png")));
        backButtonStack.add(backButtonOutline);
        backButtonStack.add(backButton);
        table.add(backButtonStack).colspan(2).size(300, 60).padTop(20);

        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String amigo = usernameField.getText();
                if (amigo.isEmpty()){
                    messageLabel.setText("El nombre de usuario no puede estar vacio.");
                    return;
                }
                if (userLogic.listarAmigos(main.username).contains(amigo)) {
                    messageLabel.setText("Ese usuario ya esta en tu lista de amigos.");
                    return;
                }
                if (userLogic.agregarAmigo(main.username, amigo)) {
                    messageLabel.setText("Amigo agregado con exito.");
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
        pixelFont.dispose();
    }
}