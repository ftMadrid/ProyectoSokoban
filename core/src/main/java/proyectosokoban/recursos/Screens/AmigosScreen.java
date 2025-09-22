// Ruta: core/src/main/java/proyectosokoban/recursos/Screens/AmigosScreen.java
package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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
    private BitmapFont pixelFont, titleFont, levelFont;
    private Texture backgroundTexture, btnTex, tfBgTex, cursorTex, panelBgTex;
    private Table listContainer;
    private TextField friendUsernameTextField;
    private Label messageLabel;
    private Table panelTable;

    public AmigosScreen(final Main main) {
        this.main = main;
        this.stage = new Stage(new ScreenViewport());
        this.userLogic = new LogicaUsuarios();
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();

        backgroundTexture = new Texture(Gdx.files.internal("background2.png"));
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

        p.size = 48;
        p.color = Color.valueOf("1E1E1E");
        titleFont = generator.generateFont(p);

        p.size = 24;
        p.color = Color.valueOf("1E1E1E");
        pixelFont = generator.generateFont(p);

        p.size = 22;
        p.color = Color.valueOf("2E8B57");
        levelFont = generator.generateFont(p);

        generator.dispose();
    }

    private TextureRegionDrawable solid(float r, float g, float b, float a) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(r, g, b, a);
        pm.fill();
        TextureRegionDrawable dr = new TextureRegionDrawable(new TextureRegion(new Texture(pm)));
        pm.dispose();
        return dr;
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        panelTable = new Table();
        panelTable.setBackground(new TextureRegionDrawable(new TextureRegion(panelBgTex)));
        panelTable.pad(20, 40, 20, 40);

        float panelWidth = Gdx.graphics.getWidth() * 0.9f;
        float panelHeight = Gdx.graphics.getHeight() * 0.9f;

        root.add(panelTable).width(panelWidth).height(panelHeight);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.valueOf("1E1E1E"));
        Label title = new Label(gestorIdiomas.setTexto("amigos.titulo"), titleStyle);
        panelTable.add(title).expandX().center().padTop(60).padBottom(15).row();

        Label.LabelStyle messageStyle = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        messageLabel = new Label("", messageStyle);
        panelTable.add(messageLabel).height(30).padBottom(10).row();

        Table topRow = new Table();
        topRow.defaults().space(12).height(46);

        TextField.TextFieldStyle tfStyle = new TextField.TextFieldStyle(pixelFont, Color.BLACK,
                new TextureRegionDrawable(new TextureRegion(cursorTex)), null,
                new TextureRegionDrawable(new TextureRegion(tfBgTex)));

        friendUsernameTextField = new TextField("", tfStyle);
        friendUsernameTextField.setAlignment(Align.center);
        friendUsernameTextField.setMessageText(gestorIdiomas.setTexto("amigos.username_message"));
        topRow.add(friendUsernameTextField).width(300);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = new TextureRegionDrawable(new TextureRegion(btnTex));
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");

        TextButton addBtn = new TextButton(gestorIdiomas.setTexto("amigos.agregar"), btnStyle);
        topRow.add(addBtn).width(150);

        panelTable.add(topRow).expandX().center().padBottom(15).row();

        listContainer = new Table();
        listContainer.top();

        ScrollPane scroll = new ScrollPane(listContainer, new ScrollPane.ScrollPaneStyle());
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);

        panelTable.add(scroll).expand().fill().padBottom(15).row();

        TextButton backBtn = new TextButton(gestorIdiomas.setTexto("amigos.volver_menu"), btnStyle);
        panelTable.add(backBtn).width(200).height(50).center().padTop(10);

        addBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addFriend(friendUsernameTextField.getText());
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                addBtn.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                backBtn.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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
        
        addBtn.setTransform(true);
        addBtn.setOrigin(Align.center);
        
        backBtn.setTransform(true);
        backBtn.setOrigin(Align.center);
        
    }

    private void loadFriends() {
        listContainer.clear();
        Label.LabelStyle nameStyle = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        Label.LabelStyle levelStyle = new Label.LabelStyle(levelFont, Color.valueOf("2E8B57"));

        List<String> amigos = userLogic.listarAmigos(main.username);

        if (amigos == null || amigos.isEmpty()) {
            Label emptyLabel = new Label(gestorIdiomas.setTexto("amigos.no_amigos"), nameStyle);
            emptyLabel.setAlignment(Align.center);
            listContainer.add(emptyLabel).expandX().center().pad(20).row();
            return;
        }

        float availableWidth = panelTable.getWidth() - 80;
        if (availableWidth <= 0) {
            availableWidth = Gdx.graphics.getWidth() * 0.9f - 80;
        }
        float avatarWidth = availableWidth * 0.15f;
        float nameWidth = availableWidth * 0.60f;
        float levelWidth = availableWidth * 0.25f;

        Drawable rowBackground = solid(0, 0, 0, 0.08f);

        int index = 0;
        for (String amigo : amigos) {
            Table friendRow = new Table();

            if (index % 2 == 0) {
                friendRow.setBackground(rowBackground);
            }

            String avatarPath = userLogic.getAvatar(amigo);
            Image avatar = new Image(new Texture(Gdx.files.internal(avatarPath)));
            Table avatarCell = new Table();
            avatarCell.add(avatar).size(40, 40);
            friendRow.add(avatarCell).width(avatarWidth).center();

            Label nameLabel = new Label(amigo, nameStyle);
            nameLabel.setAlignment(Align.center);
            friendRow.add(nameLabel).width(nameWidth).center();

            int nivel = userLogic.ultimoNivelDesbloqueado(amigo) + 1;
            Label levelLabel = new Label("Lvl " + nivel, levelStyle);
            levelLabel.setAlignment(Align.center);
            friendRow.add(levelLabel).width(levelWidth).center();

            listContainer.add(friendRow).width(availableWidth).center().padTop(8).padBottom(8).row();
            index++;
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
        createUI();
        loadFriends();
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
        levelFont.dispose();
        backgroundTexture.dispose();
        btnTex.dispose();
        tfBgTex.dispose();
        cursorTex.dispose();
        panelBgTex.dispose();
    }
}
