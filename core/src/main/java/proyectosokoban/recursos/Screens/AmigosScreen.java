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
import java.util.List;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class AmigosScreen implements Screen {

    private final Main main;
    private Stage stage;
    private LogicaUsuarios userLogic;
    private BitmapFont pixelFont;
    private BitmapFont titleFont;
    private Texture backgroundTexture;
    private TextField usernameField;
    private Label messageLabel;
    private Label amigosLabel;

    public AmigosScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        userLogic = new LogicaUsuarios();
        backgroundTexture = new Texture(Gdx.files.internal("background3.png"));
        
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);

        parameter.size = 70;
        titleFont = generator.generateFont(parameter);
        generator.dispose();

        createUI();
        updateFriendsList();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        stage.addActor(mainTable);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        mainTable.add(new Label("Gestionar Amigos", titleStyle)).expandX().top().padTop(50).padBottom(50).row();
        
        Stack fieldStack = new Stack();
        Table contentTable = new Table();
        contentTable.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        contentTable.pad(20f);
        Image outlineImage = new Image(new Texture(Gdx.files.internal("ui/outline de field 2.png")));
        outlineImage.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        fieldStack.add(contentTable);
        fieldStack.add(outlineImage);
        
        mainTable.add(fieldStack).width(800).height(450).row();
        
        Table inputTable = new Table();
        contentTable.add(inputTable).pad(20).top().row();
        
        TextButton.TextButtonStyle labelButtonStyle = new TextButton.TextButtonStyle();
        labelButtonStyle.font = pixelFont;
        labelButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/users1.png"))));
        TextButton userLabelButton = new TextButton("USUARIO", labelButtonStyle);
        userLabelButton.setDisabled(true);
        inputTable.add(userLabelButton).width(200).height(60).padRight(15);
        
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = new BitmapFont();
        textFieldStyle.fontColor = Color.BLACK;
        TextureRegionDrawable fieldBackground = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/txtfield.png"))));
        fieldBackground.setLeftWidth(15f);
        fieldBackground.setRightWidth(15f);
        textFieldStyle.background = fieldBackground;
        textFieldStyle.messageFont = pixelFont;
        textFieldStyle.messageFontColor = Color.GRAY;
        usernameField = new TextField("", textFieldStyle);
        usernameField.setMessageText(" Nombre de usuario");
        usernameField.setMaxLength(15);
        inputTable.add(usernameField).width(350).height(60);
        
        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        messageLabel = new Label("", labelStyle);
        contentTable.add(messageLabel).padTop(10).row();
        
        amigosLabel = new Label("Amigos:", labelStyle);
        amigosLabel.setAlignment(Align.topLeft);
        amigosLabel.setWrap(true);
        
        ScrollPane scrollPane = new ScrollPane(amigosLabel, new Skin(Gdx.files.internal("uiskin.json")));
        contentTable.add(scrollPane).expand().fill().pad(20).row();
        
        TextButton.TextButtonStyle actionButtonStyle = new TextButton.TextButtonStyle();
        actionButtonStyle.font = pixelFont;
        actionButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));

        Stack addButtonStack = new Stack();
        TextButton addButton = new TextButton("Agregar Amigo", actionButtonStyle);
        Image addButtonOutline = new Image(new Texture(Gdx.files.internal("ui/outline botones.png")));
        addButtonStack.add(addButtonOutline);
        addButtonStack.add(addButton);
        mainTable.add(addButtonStack).size(300, 60).padTop(30).row();

        Stack backButtonStack = new Stack();
        TextButton backButton = new TextButton("Volver al Menu", actionButtonStyle);
        Image backButtonOutline = new Image(new Texture(Gdx.files.internal("ui/outline botones.png")));
        backButtonStack.add(backButtonOutline);
        backButtonStack.add(backButton);
        mainTable.add(backButtonStack).size(300, 60).padTop(20);

        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String amigo = usernameField.getText();
                if (amigo.isEmpty()){
                    messageLabel.setText("El nombre de usuario no puede estar vacio.");
                    return;
                }
                if (userLogic.listarAmigos(main.username).contains(amigo)) {
                    messageLabel.setText("Ya tienes a este usuario como amigo.");
                    return;
                }
                if (userLogic.agregarAmigo(main.username, amigo)) {
                    messageLabel.setText("Amigo agregado con exito.");
                    updateFriendsList();
                } else {
                    messageLabel.setText("No se pudo agregar. Revisa el nombre.");
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

    private void updateFriendsList() {
        List<String> amigos = userLogic.listarAmigos(main.username);
        StringBuilder sb = new StringBuilder("Amigos:\n\n");
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