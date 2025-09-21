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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class RegisterScreen implements Screen {

    private final Main main;
    private Stage stage;
    private LogicaUsuarios userLogic;
    private GestorIdiomas gestorIdiomas;
    private Texture backgroundTexture;
    private BitmapFont pixelFont;
    private BitmapFont titleFont;
    private TextField usernameField;
    private TextField fullnameField;
    private TextField passwordField;
    private Label messageLabel;
    private CheckBox showPasswordCheckBox;

    public RegisterScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        userLogic = new LogicaUsuarios();
        gestorIdiomas = GestorIdiomas.obtenerInstancia();
        backgroundTexture = new Texture(Gdx.files.internal("background2.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);

        parameter.size = 84;
        titleFont = generator.generateFont(parameter);
        generator.dispose();

        createUI();
    }
    
    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[A-Za-z].*") && password.matches(".*\\d.*") && password.matches(".*[^A-Za-z0-9].*");
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label(gestorIdiomas.setTexto("register.titulo"), titleStyle);
        table.add(title).padBottom(50).colspan(2).row();

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, pixelFont.getColor());

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = new BitmapFont();
        textFieldStyle.font.getData().setScale(1.5f);
        textFieldStyle.fontColor = Color.BLACK;
        TextureRegionDrawable fieldBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/txtfield.png"))));
        fieldBackground.setLeftWidth(35f);
        textFieldStyle.background = fieldBackground;
        textFieldStyle.cursor = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/cursor 1.png")));

        table.add(new Label(gestorIdiomas.setTexto("login.usuario"), labelStyle)).right().padRight(10);
        usernameField = new TextField("", textFieldStyle);
        usernameField.setMaxLength(15);
        table.add(usernameField).width(380).height(52).row();
        
        table.add(new Label(gestorIdiomas.setTexto("register.nombre"), labelStyle)).right().padRight(10).padTop(10);
        fullnameField = new TextField("", textFieldStyle);
        fullnameField.setMaxLength(25);
        table.add(fullnameField).width(380).height(52).padTop(10).row();

        table.add(new Label(gestorIdiomas.setTexto("login.contrasena"), labelStyle)).right().padRight(10).padTop(10);
        passwordField = new TextField("", textFieldStyle);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMaxLength(20);
        table.add(passwordField).width(380).height(52).padTop(10).row();
        
        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.font = pixelFont;
        checkBoxStyle.fontColor = pixelFont.getColor();
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/checkbox.png")));
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/chekbox no fill.png")));

        showPasswordCheckBox = new CheckBox(gestorIdiomas.setTexto("login.mostrar_contrasena"), checkBoxStyle);
        showPasswordCheckBox.getLabel().setStyle(labelStyle);
        table.add(new Label("", labelStyle));
        table.add(showPasswordCheckBox).left().padTop(10).row();
        
        TextButton.TextButtonStyle actionButtonStyle = new TextButton.TextButtonStyle();
        actionButtonStyle.font = pixelFont;
        actionButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));

        TextButton registerButton = new TextButton(gestorIdiomas.setTexto("register.registrarse"), actionButtonStyle);
        table.add(registerButton).colspan(2).width(360).height(60).padTop(20).row();

        Table loginLinkTable = new Table();
        Label alreadyAccountLabel = new Label(gestorIdiomas.setTexto("register.ya_tienes_cuenta"), labelStyle);
        loginLinkTable.add(alreadyAccountLabel).padRight(5);

        TextButton.TextButtonStyle linkStyle = new TextButton.TextButtonStyle();
        linkStyle.font = pixelFont;
        linkStyle.fontColor = Color.CYAN;
        TextButton loginButton = new TextButton(gestorIdiomas.setTexto("register.login"), linkStyle);
        loginLinkTable.add(loginButton);
        table.add(loginLinkTable).colspan(2).padTop(20).row();

        messageLabel = new Label("", labelStyle);
        messageLabel.setWrap(true);
        table.add(messageLabel).colspan(2).width(450).padTop(10).center().row();

        showPasswordCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                passwordField.setPasswordMode(!showPasswordCheckBox.isChecked());
            }
        });

        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String password = passwordField.getText();
                if (!isPasswordValid(password)) {
                    messageLabel.setText(gestorIdiomas.setTexto("register.error_password"));
                    return;
                }
                if (userLogic.CrearUsuario(usernameField.getText(), fullnameField.getText(), password)) {
                    transicionSuave.fadeOutAndChangeScreen(main, stage, new LoginScreen(main));
                } else {
                    messageLabel.setText(gestorIdiomas.setTexto("register.error_username"));
                }
            }
        });

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LoginScreen(main));
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
        pixelFont.dispose();
        titleFont.dispose();
        backgroundTexture.dispose();
    }
}