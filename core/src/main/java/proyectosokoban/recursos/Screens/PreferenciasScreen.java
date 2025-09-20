package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class PreferenciasScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;
    private InputMultiplexer multiplexer;
    private BitmapFont pixelFont;
    private BitmapFont titleFont;

    private int keyUp = Input.Keys.UP;
    private int keyDown = Input.Keys.DOWN;
    private int keyLeft = Input.Keys.LEFT;
    private int keyRight = Input.Keys.RIGHT;

    private Label keyUpLabel, keyDownLabel, keyLeftLabel, keyRightLabel;
    private Label messageLabel;
    private TextButton waitingButton = null;

    public PreferenciasScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        
        // Fuente normal para el contenido
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.valueOf("F5F5DC");
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.magFilter = Texture.TextureFilter.Nearest;
        pixelFont = generator.generateFont(parameter);

        // Fuente más grande para el título
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = 48;
        titleParameter.color = Color.valueOf("F5F5DC");
        titleParameter.minFilter = Texture.TextureFilter.Nearest;
        titleParameter.magFilter = Texture.TextureFilter.Nearest;
        titleFont = generator.generateFont(titleParameter);
        
        generator.dispose();

        createUI();
        loadPreferences();

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (waitingButton != null) {
                    if (isKeyAlreadyUsed(keycode, waitingButton.getName())) {
                        messageLabel.setText("Esa tecla ya esta en uso. Elige otra.");
                        messageLabel.setColor(Color.RED);
                    } else {
                        assignKey(waitingButton.getName(), keycode);
                        updateKeyLabels();
                        messageLabel.setText("Control actualizado.");
                        messageLabel.setColor(Color.GREEN);
                    }
                    waitingButton.setText("Cambiar");
                    waitingButton = null;
                }
                return true;
            }
        });
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        stage.addActor(mainTable);

        // Estilos
        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, pixelFont.getColor());
        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(titleFont, titleFont.getColor());
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = pixelFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));
        
        mainTable.add(new Label("OPTIONS", titleLabelStyle)).padBottom(20).row();

        Stack fieldStack = new Stack();
        
        Table contentTable = new Table();
        contentTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/field 2.png")))));
        contentTable.pad(30f);

        Image outlineImage = new Image(new Texture(Gdx.files.internal("ui/outline de field 2.png")));
        outlineImage.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled); // Para que no bloquee los clics
        
        fieldStack.add(contentTable);
        fieldStack.add(outlineImage);
        
        mainTable.add(fieldStack).width(700).height(550).row();

        
        // Audio
        Texture audioTexture = new Texture(Gdx.files.internal("ui/audio.png"));
        Image audioImage = new Image(audioTexture);
        contentTable.add(audioImage).size(180, 40).colspan(3).padTop(10).padBottom(10).row();

        Slider volumeSlider = new Slider(0, 1, 0.01f, false, skin);
        volumeSlider.setValue(main.getVolume());
        contentTable.add(volumeSlider).width(400).colspan(3).padBottom(5).row();
        contentTable.add(new Label("Volumen", labelStyle)).colspan(3).padBottom(30).row();

        // Controles
        Texture controlsTexture = new Texture(Gdx.files.internal("ui/controls.png"));
        Image controlsImage = new Image(controlsTexture);
        contentTable.add(controlsImage).size(180, 40).colspan(3).padBottom(20).row();
        
        keyUpLabel = new Label("", labelStyle);
        keyDownLabel = new Label("", labelStyle);
        keyLeftLabel = new Label("", labelStyle);
        keyRightLabel = new Label("", labelStyle);

        createKeybindRow(contentTable, "Mover Arriba", "up", keyUpLabel, buttonStyle, labelStyle);
        createKeybindRow(contentTable, "Mover Abajo", "down", keyDownLabel, buttonStyle, labelStyle);
        createKeybindRow(contentTable, "Mover Izquierda", "left", keyLeftLabel, buttonStyle, labelStyle);
        createKeybindRow(contentTable, "Mover Derecha", "right", keyRightLabel, buttonStyle, labelStyle);
        
        contentTable.add().height(20).colspan(3).row(); 
        
        messageLabel = new Label("Haz clic en 'Cambiar' y presiona la nueva tecla", labelStyle);
        contentTable.add(messageLabel).colspan(3).padTop(10).row();

        Stack saveButtonStack = new Stack();
        TextButton saveButton = new TextButton("Guardar y Volver", buttonStyle);
        Image saveButtonOutline = new Image(new Texture(Gdx.files.internal("ui/outline botones.png")));
        saveButtonStack.add(saveButtonOutline);
        saveButtonStack.add(saveButton);
        mainTable.add(saveButtonStack).size(300, 50).padTop(20);

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.setVolume(((Slider) actor).getValue());
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                savePreferences();
                main.setScreen(new MenuScreen(main));
                dispose();
            }
        });
    }

    private void createKeybindRow(Table table, String description, String action, Label keyLabel, TextButton.TextButtonStyle buttonStyle, Label.LabelStyle labelStyle) {
        table.add(new Label(description, labelStyle)).left().expandX().padLeft(40);
        table.add(keyLabel).width(80).center();
        
        Stack buttonStack = new Stack();
        TextButton changeButton = new TextButton("Cambiar", buttonStyle);
        changeButton.setName(action);
        Image buttonOutline = new Image(new Texture(Gdx.files.internal("ui/outline botones.png")));
        
        buttonStack.add(buttonOutline);
        buttonStack.add(changeButton);

        changeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (waitingButton != null) {
                    waitingButton.setText("Cambiar");
                }
                waitingButton = (TextButton)event.getListenerActor();
                waitingButton.setText("Presiona una tecla...");
                messageLabel.setText("Esperando tecla para: " + description);
                messageLabel.setColor(Color.WHITE);
            }
        });
        table.add(buttonStack).size(150, 40).padBottom(10).padRight(40).row();
    }
    
    private void updateKeyLabels() {
        keyUpLabel.setText(Input.Keys.toString(keyUp));
        keyDownLabel.setText(Input.Keys.toString(keyDown));
        keyLeftLabel.setText(Input.Keys.toString(keyLeft));
        keyRightLabel.setText(Input.Keys.toString(keyRight));
    }
    
    private boolean isKeyAlreadyUsed(int newKeycode, String currentAction) {
        if (!currentAction.equals("up") && newKeycode == keyUp) return true;
        if (!currentAction.equals("down") && newKeycode == keyDown) return true;
        if (!currentAction.equals("left") && newKeycode == keyLeft) return true;
        if (!currentAction.equals("right") && newKeycode == keyRight) return true;
        return false;
    }

    private void assignKey(String action, int keycode) {
        switch (action) {
            case "up": keyUp = keycode; break;
            case "down": keyDown = keycode; break;
            case "left": keyLeft = keycode; break;
            case "right": keyRight = keycode; break;
        }
    }

    private void loadPreferences() {
        int[] prefs = userLogic.getPreferencias(main.username);
        main.setVolume(prefs[0] / 100f);
        keyUp = prefs[4];
        keyDown = prefs[5];
        keyLeft = prefs[6];
        keyRight = prefs[7];
        updateKeyLabels();
    }
    
    private void savePreferences() {
        int[] oldPrefs = userLogic.getPreferencias(main.username);
        userLogic.setPreferencias(
            main.username,
            (int)(main.getVolume() * 100),
            (byte)oldPrefs[1],
            (byte)oldPrefs[2],
            oldPrefs[3] == 1,
            keyUp, keyDown, keyLeft, keyRight
        );
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
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
    }
}