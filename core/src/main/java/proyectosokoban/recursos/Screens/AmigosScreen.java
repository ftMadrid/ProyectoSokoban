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
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class AmigosScreen implements Screen {

    private final Main main;
    private Stage stage;
    private LogicaUsuarios userLogic;
    private GestorIdiomas gestorIdiomas;

    // assets
    private Texture backgroundTexture;
    private Texture btnTex;
    private Texture tfBgTex;
    private Texture cursorTex;

    // fuentes
    private BitmapFont pixelFont, titleFont;

    // UI refs
    private TextField friendUsernameTextField;
    private Table listContainer;

    public AmigosScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        userLogic = new LogicaUsuarios();
        gestorIdiomas = GestorIdiomas.obtenerInstancia();

        // cargar assets
        backgroundTexture = new Texture(Gdx.files.internal("background3.png"));
        btnTex = new Texture(Gdx.files.internal("ui/button1.png"));
        tfBgTex = new Texture(Gdx.files.internal("ui/txtfield.png"));
        cursorTex = new Texture(Gdx.files.internal("ui/cursor 1.png"));

        setupFonts();
        createUI();
        loadFriends();
    }

    private void setupFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size = 72; p.color = Color.valueOf("F5F5DC");
        titleFont = generator.generateFont(p);

        p.size = 26; p.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(p);

        generator.dispose();
    }

    private TextButton.TextButtonStyle makeButtonStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        TextureRegionDrawable dr = new TextureRegionDrawable(new TextureRegion(btnTex));
        style.up = dr;
        style.down = dr;
        style.over = dr;
        style.font = pixelFont;
        style.fontColor = Color.valueOf("1E1E1E"); // texto oscuro sobre botón claro
        return style;
    }

    private TextField.TextFieldStyle makeTextFieldStyle() {
        TextField.TextFieldStyle s = new TextField.TextFieldStyle();
        s.font = new BitmapFont(); // sistema para el contenido del textfield (negro)
        s.fontColor = Color.BLACK;
        s.background = new TextureRegionDrawable(new TextureRegion(tfBgTex));
        s.cursor = new TextureRegionDrawable(new TextureRegion(cursorTex));
        return s;
    }

    private Label.LabelStyle makeLabelStyle() {
        return new Label.LabelStyle(pixelFont, Color.valueOf("F5F5DC"));
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.pad(22, 26, 26, 26); // top,left,bottom,right
        stage.addActor(root);

        // ===== TÍTULO =====
        Label title = new Label("SOKOMINE", new Label.LabelStyle(titleFont, Color.WHITE));
        root.add(title).expandX().center().padBottom(16).row();

        // ===== Fila superior alineada (TextField + Add + Back) =====
        TextField.TextFieldStyle tfStyle = makeTextFieldStyle();
        friendUsernameTextField = new TextField("", tfStyle);
        friendUsernameTextField.setMessageText(gestorIdiomas.setTexto("amigos.username_message"));

        TextButton.TextButtonStyle btnStyle = makeButtonStyle();
        TextButton addBtn = new TextButton(gestorIdiomas.setTexto("amigos.agregar"), btnStyle);
        addBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                String friend = friendUsernameTextField.getText().trim();
                if (friend.isEmpty()) return;
                if (userLogic.agregarAmigo(main.username, friend)) {
                    friendUsernameTextField.setText("");
                    loadFriends();
                }
            }
        });

        TextButton backBtn = new TextButton(gestorIdiomas.setTexto("amigos.volver_menu"), btnStyle);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        });

        Table topRow = new Table();
        topRow.defaults().space(12).height(46);
        topRow.add(friendUsernameTextField).width(380);
        topRow.add(addBtn).width(180);
        topRow.add(backBtn).width(200);
        root.add(topRow).expandX().fillX().padBottom(14).row();

        // ===== Lista con Scroll más pequeño (≈60% alto) =====
        listContainer = new Table();
        listContainer.top().defaults().pad(6);

        // ScrollPane sin Skin: usamos el constructor por defecto y estilos vacíos
        ScrollPane scroll = new ScrollPane(listContainer);
        scroll.setFadeScrollBars(false);

        // foreground simple: usamos una Tabla sin fondo para no depender de skins
        Table fg = new Table();
        fg.pad(12);
        fg.add(scroll).expand().fill().row();

        float desiredHeight = Math.max(280, Gdx.graphics.getHeight() * 0.60f);
        root.add(fg).expandX().fillX().height(desiredHeight).padTop(6);
    }

    private void loadFriends() {
        listContainer.clear();
        List<String> amigos = userLogic.listarAmigos(main.username);
        if (amigos == null || amigos.isEmpty()) {
            listContainer.add(new Label(gestorIdiomas.setTexto("amigos.no_amigos"), makeLabelStyle()))
                         .padTop(4).row();
            return;
        }
        for (String amigo : amigos) {
            listContainer.add(makeFriendRow(amigo)).expandX().fillX().row();
        }
    }

    private Table makeFriendRow(String username) {
        Table row = new Table();
        row.pad(10).defaults().space(6);

        Label name = new Label(username, makeLabelStyle());
        row.add(name).left().expandX();
        // puedes añadir botones de acción aquí reutilizando btnStyle si los necesitas

        return row;
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        transicionSuave.fadeIn(stage);
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        // fondo estirable
        stage.getBatch().draw(backgroundTexture, 0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() { }

    @Override public void resume() { }

    @Override public void hide() { }

    @Override public void dispose() {
        stage.dispose();
        titleFont.dispose();
        pixelFont.dispose();
        backgroundTexture.dispose();
        btnTex.dispose();
        tfBgTex.dispose();
        cursorTex.dispose();
    }
}
