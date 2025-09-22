package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
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
    private BitmapFont pixelFont, titleFont;
    private Texture backgroundTexture, btnTex, tfBgTex, cursorTex, panelBgTex;
    private Table listContainer;
    private TextField friendUsernameTextField;
    private Label messageLabel;

    public AmigosScreen(final Main main) {
        this.main = main;
        this.stage = new Stage(new ScreenViewport());
        this.userLogic = new LogicaUsuarios();
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();

        backgroundTexture = new Texture(Gdx.files.internal("background3.png"));
        btnTex = new Texture(Gdx.files.internal("ui/button1.png"));
        tfBgTex = new Texture(Gdx.files.internal("ui/txtfield.png"));
        cursorTex = new Texture(Gdx.files.internal("ui/cursor 1.png"));
        panelBgTex = new Texture(Gdx.files.internal("ui/field 2.png"));

        setupFonts();
        createUI();
        loadFriends();
    }

    private void setupFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size = 72;
        p.color = Color.valueOf("F5F5DC");
        titleFont = generator.generateFont(p);

        p.size = 26;
        p.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(p);

        generator.dispose();
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.pad(22, 26, 26, 26);
        stage.addActor(root);

        Label title = new Label(gestorIdiomas.setTexto("amigos.titulo"), new Label.LabelStyle(titleFont, Color.WHITE));
        root.add(title).expandX().center().padBottom(16).row();

        messageLabel = new Label("", new Label.LabelStyle(pixelFont, Color.WHITE));
        root.add(messageLabel).padBottom(10).row();

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle();
        tfStyle.font = pixelFont;
        tfStyle.fontColor = Color.BLACK;
        TextureRegionDrawable tfBackground = new TextureRegionDrawable(new TextureRegion(tfBgTex));
        tfStyle.background = tfBackground;
        tfStyle.cursor = new TextureRegionDrawable(new TextureRegion(cursorTex));
        friendUsernameTextField = new TextField("", tfStyle);
        friendUsernameTextField.setAlignment(Align.center);
        friendUsernameTextField.setMessageText(gestorIdiomas.setTexto("amigos.username_message"));

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(new TextureRegion(btnTex));
        btnStyle.down = new TextureRegionDrawable(new TextureRegion(btnTex)); // opcional, misma textura
        btnStyle.over = new TextureRegionDrawable(new TextureRegion(btnTex)); // hover (usamos la opacidad más abajo)
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");

        TextButton addBtn = new TextButton(gestorIdiomas.setTexto("amigos.agregar"), btnStyle);

        addBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addFriend(friendUsernameTextField.getText());
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                addBtn.addAction(
                        Actions.sequence(
                                Actions.scaleTo(0.9f, 0.9f, 0.05f),
                                Actions.scaleTo(1f, 1f, 0.05f)
                        )
                );
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        TextButton backBtn = new TextButton(gestorIdiomas.setTexto("amigos.volver_menu"), btnStyle);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                backBtn.addAction(
                        Actions.sequence(
                                Actions.scaleTo(0.9f, 0.9f, 0.05f),
                                Actions.scaleTo(1f, 1f, 0.05f)
                        )
                );
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        Table topRow = new Table();
        topRow.defaults().space(12).height(46);
        topRow.add(friendUsernameTextField).width(380);
        topRow.add(addBtn).width(180);
        root.add(topRow).expandX().fillX().padBottom(14).row();

        listContainer = new Table();
        listContainer.top().defaults().pad(6);

        ScrollPane scroll = new ScrollPane(listContainer);
        scroll.setFadeScrollBars(false);

        Table foreground = new Table();
        foreground.setBackground(new TextureRegionDrawable(new TextureRegion(panelBgTex)));
        foreground.pad(12);
        foreground.add(scroll).expand().fill();

        float desiredHeight = Math.max(280, Gdx.graphics.getHeight() * 0.50f);
        root.add(foreground).expand().fillX().height(desiredHeight).padTop(6).row();

        root.add(backBtn).width(300).height(50).padTop(20);
        
        addBtn.setTransform(true);
        addBtn.setOrigin(Align.center);
        addBtn.setScale(1f);
        addBtn.getColor().a = 1f;
        
        backBtn.setTransform(true);
        backBtn.setOrigin(Align.center);
        backBtn.setScale(1f);
        backBtn.getColor().a = 1f;
        
    }

    private void loadFriends() {
        listContainer.clear();
        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        List<String> amigos = userLogic.listarAmigos(main.username);

        if (amigos == null || amigos.isEmpty()) {
            listContainer.add(new Label(gestorIdiomas.setTexto("amigos.no_amigos"), labelStyle)).padTop(4).row();
            return;
        }
        for (String amigo : amigos) {
            listContainer.add(new Label(amigo, labelStyle)).expandX().fillX().left().padLeft(20).row();
        }
    }

    private void addFriend(String friendName) {
        if (friendName == null || friendName.trim().isEmpty()) {
            messageLabel.setText(gestorIdiomas.setTexto("amigos.error_vacio"));
            messageLabel.setColor(Color.RED);
            return;
        }
        if (userLogic.agregarAmigo(main.username, friendName.trim())) {
            messageLabel.setText(gestorIdiomas.setTexto("amigos.exito"));
            messageLabel.setColor(Color.GREEN);
            friendUsernameTextField.setText("");
            loadFriends();
        } else {
            messageLabel.setText(gestorIdiomas.setTexto("amigos.error"));
            messageLabel.setColor(Color.RED);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
        titleFont.dispose();
        pixelFont.dispose();
        backgroundTexture.dispose();
        btnTex.dispose();
        tfBgTex.dispose();
        cursorTex.dispose();
        panelBgTex.dispose();
    }
}
