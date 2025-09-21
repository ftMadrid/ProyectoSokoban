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

public class LoginScreen implements Screen {

    private final Main main;
    private Stage stage;
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

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label(gestorIdiomas.setTexto("login.titulo"), titleStyle);
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

        table.add(new Label(gestorIdiomas.setTexto("login.contrasena"), labelStyle)).right().padRight(10).padTop(10);
        passwordField = new TextField("", textFieldStyle);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        table.add(passwordField).width(380).height(52).padTop(10).row();
        
        CheckBox.CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.font = pixelFont;
        checkBoxStyle.fontColor = pixelFont.getColor();
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/checkbox.png")));
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/chekbox no fill.png")));

        showPasswordCheckBox = new CheckBox(gestorIdiomas.setTexto("login.mostrar_contrasena"), checkBoxStyle);
        showPasswordCheckBox.getLabel().setStyle(labelStyle);
        showPasswordCheckBox.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                passwordField.setPasswordMode(!showPasswordCheckBox.isChecked());
            }
        });
        table.add(new Label("", labelStyle));
        table.add(showPasswordCheckBox).left().padTop(10).row();

        TextButton.TextButtonStyle actionStyle = new TextButton.TextButtonStyle();
        actionStyle.font = pixelFont;
        actionStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));

        final TextButton loginBtn = new TextButton(gestorIdiomas.setTexto("login.iniciar_sesion"), actionStyle);
        table.add(loginBtn).colspan(2).width(360).height(60).padTop(20).row();
        
        Table linkTable = new Table();
        Label noAccountLabel = new Label(gestorIdiomas.setTexto("login.no_tienes_cuenta"), labelStyle);
        linkTable.add(noAccountLabel).padRight(5);

        TextButton.TextButtonStyle linkStyle = new TextButton.TextButtonStyle();
        linkStyle.font = pixelFont;
        linkStyle.fontColor = Color.CYAN;
        TextButton registerButton = new TextButton(gestorIdiomas.setTexto("login.registrate"), linkStyle);
        linkTable.add(registerButton);
        table.add(linkTable).colspan(2).padTop(20).row();

        messageLabel = new Label("", labelStyle);
        table.add(messageLabel).colspan(2).padTop(12).row();

        TextButton exitButton = new TextButton(gestorIdiomas.setTexto("login.salir"), actionStyle);
        table.add(exitButton).colspan(2).width(360).height(60).padTop(10).row();
        
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        loginBtn.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y) {
                String u = usernameField.getText().trim();
                String p = passwordField.getText();
                if (userLogic.login(u, p)) {
                    main.username = u;
                    LogicaUsuarios.usuarioLogged = u;
                    gestorIdiomas.cargarPreferenciasUsuario(u);
                    main.loadAndApplyVolumePreference();
                    main.applyDisplayPreferences();
                    transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
                } else {
                    messageLabel.setColor(Color.RED);
                    messageLabel.setText(gestorIdiomas.setTexto("login.error"));
                }
            }
        });

        registerButton.addListener(new ClickListener(){
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new RegisterScreen(main));
            }
        });
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playLobbyMusic();
        transicionSuave.fadeIn(stage);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose(); 
        pixelFont.dispose(); 
        titleFont.dispose(); 
        backgroundTexture.dispose();
    }
}