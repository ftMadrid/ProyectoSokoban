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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class LoginScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;
    private Texture backgroundTexture;
    private BitmapFont pixelFont;

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
        backgroundTexture = new Texture(Gdx.files.internal("background2.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.valueOf("F5F5DC");
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.magFilter = Texture.TextureFilter.Nearest;
        pixelFont = generator.generateFont(parameter);
        generator.dispose();

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Drawable tableBackground = skin.newDrawable("white", 0, 0, 0, 0.5f);
        table.setBackground(tableBackground);
        table.pad(20);

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, pixelFont.getColor());

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = new BitmapFont(); 
        textFieldStyle.fontColor = Color.BLACK;
        TextureRegionDrawable fieldBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/field 1.png"))));
        fieldBackground.setLeftWidth(35f); 
        textFieldStyle.background = fieldBackground;
        textFieldStyle.cursor = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/cursor 1.png")));
        
        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.font = pixelFont;
        checkBoxStyle.fontColor = pixelFont.getColor();
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/checkbox.png")));
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/chekbox no fill.png")));
        
        TextButton.TextButtonStyle labelButtonStyle = new TextButton.TextButtonStyle();
        labelButtonStyle.font = pixelFont;
        labelButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/users2.png"))));
        
        TextButton.TextButtonStyle actionButtonStyle = new TextButton.TextButtonStyle();
        actionButtonStyle.font = pixelFont;
        actionButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));

        Label title = new Label("Sokoban", labelStyle);
        title.setFontScale(1.5f);
        table.add(title).padBottom(40).colspan(2).row();

        // --- Usuario ---
        TextButton userLabelButton = new TextButton("USUARIO", labelButtonStyle);
        userLabelButton.setDisabled(true);
        table.add(userLabelButton).width(250).height(70).padRight(10);
        
        usernameField = new TextField("", textFieldStyle);
        usernameField.setMaxLength(15);
        table.add(usernameField).width(350).height(70).padBottom(15).row();

        // --- Contraseña ---
        TextButton passLabelButton = new TextButton("CONTRASENA", labelButtonStyle);
        passLabelButton.setDisabled(true);
        table.add(passLabelButton).width(250).height(70).padRight(10);

        passwordField = new TextField("", textFieldStyle);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMaxLength(20);
        table.add(passwordField).width(350).height(70).padBottom(15).row();

        showPasswordCheckBox = new CheckBox(" MOSTRAR CONTRASENA", checkBoxStyle);
        table.add(showPasswordCheckBox).colspan(2).left().pad(10, 80, 20, 0).row();

        Stack loginButtonStack = new Stack();
        TextButton loginButton = new TextButton("INICIAR SESION", actionButtonStyle);
        Image loginButtonOutline = new Image(new Texture(Gdx.files.internal("ui/outline botones.png")));
        loginButtonStack.add(loginButtonOutline);
        loginButtonStack.add(loginButton);
        table.add(loginButtonStack).colspan(2).size(300, 60).padTop(10).row();

        messageLabel = new Label("", labelStyle);
        table.add(messageLabel).colspan(2).padTop(10).row();

        Table registerLinkTable = new Table();
        Label noAccountLabel = new Label("No tienes cuenta?", labelStyle);
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
        pixelFont.dispose();
    }
}