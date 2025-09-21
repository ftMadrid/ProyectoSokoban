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
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class LoginScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;
    private GestorIdiomas gestorIdiomas;
    private Texture backgroundTexture;
    private BitmapFont pixelFont;
    private BitmapFont titleFont;
    private TextField usernameField;
    private TextField passwordField;
    private Label messageLabel;
    private CheckBox showPasswordCheckBox;

    public LoginScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();
        gestorIdiomas = GestorIdiomas.obtenerInstancia();
        backgroundTexture = new Texture(Gdx.files.internal("background2.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);

        parameter.size = 150;
        titleFont = generator.generateFont(parameter);
        generator.dispose();

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        
        TextButton.TextButtonStyle exitButtonStyle = new TextButton.TextButtonStyle();
        exitButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/botonespeq.png"))));
        exitButtonStyle.font = pixelFont;
        
        TextButton exitButton = new TextButton("X", exitButtonStyle);
        exitButton.getLabel().setFontScale(1.5f);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        
        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top().right();
        topTable.add(exitButton).size(60, 60).pad(10);
        stage.addActor(topTable);

        table.center();
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label(gestorIdiomas.setTexto("login.titulo"), titleStyle);
        table.add(title).padBottom(50).colspan(2).row();
        
        Drawable tableBackground = skin.newDrawable("white", 0, 0, 0, 0.5f);
        table.setBackground(tableBackground);
        table.pad(20);

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, pixelFont.getColor());
        
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = new BitmapFont(); 
        textFieldStyle.fontColor = Color.BLACK;
        TextureRegionDrawable fieldBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/txtfield.png"))));
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

        TextButton userLabelButton = new TextButton(gestorIdiomas.setTexto("login.usuario"), labelButtonStyle);
        userLabelButton.setDisabled(true);
        table.add(userLabelButton).width(250).height(70).padRight(10);
        
        usernameField = new TextField("", textFieldStyle);
        usernameField.setMaxLength(15);
        table.add(usernameField).width(350).height(70).padBottom(15).row();

        TextButton passLabelButton = new TextButton(gestorIdiomas.setTexto("login.contrasena"), labelButtonStyle);
        passLabelButton.setDisabled(true);
        table.add(passLabelButton).width(250).height(70).padRight(10);

        passwordField = new TextField("", textFieldStyle);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMaxLength(20);
        table.add(passwordField).width(350).height(70).padBottom(15).row();

        showPasswordCheckBox = new CheckBox(gestorIdiomas.setTexto("login.mostrar_contrasena"), checkBoxStyle);
        table.add(showPasswordCheckBox).colspan(2).left().pad(10, 80, 20, 0).row();

        TextButton loginButton = new TextButton(gestorIdiomas.setTexto("login.iniciar_sesion"), actionButtonStyle);
        table.add(loginButton).colspan(2).size(300, 60).padTop(10).row();

        messageLabel = new Label("", labelStyle);
        table.add(messageLabel).colspan(2).padTop(10).row();

        Table registerLinkTable = new Table();
        Label noAccountLabel = new Label(gestorIdiomas.setTexto("login.no_tienes_cuenta"), labelStyle);
        registerLinkTable.add(noAccountLabel).padRight(5);

        TextButton.TextButtonStyle linkStyle = new TextButton.TextButtonStyle(skin.get("toggle", TextButton.TextButtonStyle.class));
        linkStyle.font = pixelFont;
        linkStyle.fontColor = Color.CYAN;
        TextButton registerButton = new TextButton(gestorIdiomas.setTexto("login.registrate"), linkStyle);
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
                    main.applyDisplayPreferences();
                    transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
                } else {
                    messageLabel.setText(gestorIdiomas.setTexto("login.error"));
                }
            }
        });

        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new RegisterScreen(main));
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playLobbyMusic();
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