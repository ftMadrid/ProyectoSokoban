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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

import java.util.List;

public class AmigosScreen implements Screen {

    private final Main main;
    private Stage stage;
    private LogicaUsuarios userLogic;
    private GestorIdiomas gestorIdiomas;
    private BitmapFont titleFont, pixelFont;
    private Texture backgroundTexture;
    private List<String> friends;
    private TextField friendUsernameTextField;
    private Label friendsListLabel;

    public AmigosScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        userLogic = new LogicaUsuarios();
        gestorIdiomas = GestorIdiomas.obtenerInstancia();
        backgroundTexture = new Texture(Gdx.files.internal("background3.png"));
        setupFonts();
        createUI();
        loadFriends();
    }

    private void setupFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 70;
        parameter.color = Color.valueOf("F5F5DC");
        titleFont = generator.generateFont(parameter);
        parameter.size = 24;
        pixelFont = generator.generateFont(parameter);
        generator.dispose();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        stage.addActor(mainTable);

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label titleLabel = new Label(gestorIdiomas.setTexto("amigos.titulo"), titleLabelStyle);
        mainTable.add(titleLabel).padBottom(30).row();

        Stack fieldStack = new Stack();
        Table contentTable = new Table();
        contentTable.padTop(35).padLeft(35).padRight(35).padBottom(25);
        contentTable.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        Image outlineImage = new Image(new Texture(Gdx.files.internal("ui/outline de field 2.png")));
        outlineImage.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);

        fieldStack.add(contentTable);
        fieldStack.add(outlineImage);

        mainTable.add(fieldStack).width(750).height(500).row();

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        friendsListLabel = new Label("", labelStyle);
        friendsListLabel.setWrap(true);
        friendsListLabel.setAlignment(Align.topLeft);

        ScrollPane scrollPane = new ScrollPane(friendsListLabel, new Skin(Gdx.files.internal("uiskin.json")));
        scrollPane.setFadeScrollBars(false);
        contentTable.add(scrollPane).expand().fill().pad(15).padBottom(20).row();

        Table addFriendTable = new Table();
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = pixelFont;
        textFieldStyle.fontColor = Color.BLACK;
        TextureRegionDrawable fieldBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/txtfield.png"))));
        fieldBackground.setLeftWidth(25f);
        fieldBackground.setRightWidth(25f);
        textFieldStyle.background = fieldBackground;
        textFieldStyle.messageFont = pixelFont;
        textFieldStyle.messageFontColor = new Color(0, 0, 0, 0.5f);

        friendUsernameTextField = new TextField("", textFieldStyle);
        friendUsernameTextField.setMessageText(gestorIdiomas.setTexto("amigos.username_message"));

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = pixelFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));
        TextButton addFriendButton = new TextButton(gestorIdiomas.setTexto("amigos.agregar"), buttonStyle);

        addFriendTable.add(friendUsernameTextField).width(350).height(50).padRight(15);
        addFriendTable.add(addFriendButton).size(220, 50);
        contentTable.add(addFriendTable).padTop(10).padBottom(10); 

        TextButton backButton = new TextButton(gestorIdiomas.setTexto("back.button"), buttonStyle);
        mainTable.add(backButton).size(350, 60).padTop(30).row();

        addFriendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String friendUsername = friendUsernameTextField.getText();
                if (userLogic.agregarAmigo(main.username, friendUsername)) {
                    loadFriends();
                    friendUsernameTextField.setText("");
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        });
    }

    private void loadFriends() {
        friends = userLogic.listarAmigos(main.username);
        if (friends.isEmpty()) {
            friendsListLabel.setText(gestorIdiomas.setTexto("amigos.no_amigos"));
        } else {
            StringBuilder sb = new StringBuilder();
            for (String friend : friends) {
                sb.append(" • ").append(friend).append("\n\n");
            }
            friendsListLabel.setText(sb.toString());
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        transicionSuave.fadeIn(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
        titleFont.dispose();
        pixelFont.dispose();
        backgroundTexture.dispose();
    }
}