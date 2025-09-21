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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class PreferenciasScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;
    private InputMultiplexer multiplexer;
    private BitmapFont pixelFont, titleFont, smallFont, sectionFont;
    private Texture backgroundTexture;
    private int keyUp, keyDown, keyLeft, keyRight;
    private Label keyUpLabel, keyDownLabel, keyLeftLabel, keyRightLabel, messageLabel;
    private TextButton waitingButton = null;
    private TextButton.TextButtonStyle originalButtonStyle, waitingButtonStyle;
    private SelectBox<String> displayModeSelectBox;
    private int displayMode;
    private Slider volumeSlider;

    public PreferenciasScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();
        backgroundTexture = new Texture(Gdx.files.internal("background3.png"));
        setupFonts();
        createUI();
        loadPreferences();
        setupInputProcessor();
    }

    private void setupFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        
        parameter.size = 24;
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);
        
        parameter.size = 70;
        titleFont = generator.generateFont(parameter);

        parameter.size = 30;
        parameter.color = Color.valueOf("3E3546"); 
        sectionFont = generator.generateFont(parameter);

        parameter.size = 14;
        parameter.color = Color.valueOf("F5F5DC");
        smallFont = generator.generateFont(parameter);
        
        generator.dispose();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        stage.addActor(mainTable);

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        originalButtonStyle = new TextButton.TextButtonStyle();
        originalButtonStyle.font = pixelFont;
        originalButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));
        waitingButtonStyle = new TextButton.TextButtonStyle(originalButtonStyle);
        waitingButtonStyle.font = smallFont;

        mainTable.add(new Label("OPCIONES", titleLabelStyle)).padBottom(20).row();
        
        Stack fieldStack = new Stack();
        Table contentTable = new Table();
        contentTable.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        contentTable.pad(30f);
        Image outlineImage = new Image(new Texture(Gdx.files.internal("ui/outline de field 2.png")));
        outlineImage.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        fieldStack.add(contentTable);
        fieldStack.add(outlineImage);
        mainTable.add(fieldStack).width(700).height(550).row();
        
        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        Label.LabelStyle sectionStyle = new Label.LabelStyle(sectionFont, Color.valueOf("3E3546"));

        contentTable.add(new Label("Volumen", sectionStyle)).colspan(3).padBottom(5).row();
        volumeSlider = new Slider(0, 1, 0.01f, false, skin);
        contentTable.add(volumeSlider).width(400).colspan(3).padBottom(25).row();

        contentTable.add(new Label("DISPLAY", sectionStyle)).colspan(3).padBottom(5).row();
        displayModeSelectBox = new SelectBox<>(skin);
        displayModeSelectBox.setItems("Fullscreen", "Windowed", "Mini");
        contentTable.add(displayModeSelectBox).width(400).colspan(3).padBottom(25).row();

        contentTable.add(new Label("Controles", sectionStyle)).colspan(3).padBottom(15).row();
        keyUpLabel = new Label("", labelStyle);
        keyDownLabel = new Label("", labelStyle);
        keyLeftLabel = new Label("", labelStyle);
        keyRightLabel = new Label("", labelStyle);
        createKeybindRow(contentTable, "Arriba", "up", keyUpLabel, labelStyle);
        createKeybindRow(contentTable, "Abajo", "down", keyDownLabel, labelStyle);
        createKeybindRow(contentTable, "Izquierda", "left", keyLeftLabel, labelStyle);
        createKeybindRow(contentTable, "Derecha", "right", keyRightLabel, labelStyle);
        
        messageLabel = new Label("Haz clic en 'Cambiar' y presiona una tecla", labelStyle);
        contentTable.add(messageLabel).colspan(3).padTop(15).row();

        TextButton saveButton = new TextButton("Guardar y Volver", originalButtonStyle);
        mainTable.add(saveButton).size(300, 50).padTop(20);

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.setVolume(((Slider) actor).getValue());
            }
        });

        displayModeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                displayMode = displayModeSelectBox.getSelectedIndex();
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                savePreferences();
                main.applyDisplayPreferences();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        });
    }

    private void createKeybindRow(Table table, String desc, String action, Label keyLabel, Label.LabelStyle style) {
        table.add(new Label(desc, style)).left().expandX().padLeft(50);
        table.add(keyLabel).width(120).center();
        
        TextButton changeButton = new TextButton("Cambiar", originalButtonStyle);
        changeButton.setName(action);
        
        changeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (waitingButton != null) {
                    waitingButton.setText("Cambiar");
                    waitingButton.setStyle(originalButtonStyle);
                }
                stage.setKeyboardFocus(null);
                waitingButton = (TextButton)event.getListenerActor();
                waitingButton.setText("Presiona una tecla...");
                waitingButton.setStyle(waitingButtonStyle);
                messageLabel.setText("Esperando tecla para: " + desc);
                messageLabel.setColor(Color.WHITE);
            }
        });
        table.add(changeButton).size(150, 40).padBottom(10).padRight(50).row();
    }
    
    private void setupInputProcessor() {
        multiplexer = new InputMultiplexer(stage, new InputAdapter() {
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
                    waitingButton.setStyle(originalButtonStyle);
                    waitingButton = null;
                }
                return true;
            }
        });
    }
    
    private void updateKeyLabels() {
        keyUpLabel.setText(Input.Keys.toString(keyUp));
        keyDownLabel.setText(Input.Keys.toString(keyDown));
        keyLeftLabel.setText(Input.Keys.toString(keyLeft));
        keyRightLabel.setText(Input.Keys.toString(keyRight));
    }
    
    private boolean isKeyAlreadyUsed(int newKey, String action) {
        if (!"up".equals(action) && newKey == keyUp) return true;
        if (!"down".equals(action) && newKey == keyDown) return true;
        if (!"left".equals(action) && newKey == keyLeft) return true;
        if (!"right".equals(action) && newKey == keyRight) return true;
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
        volumeSlider.setValue(main.getVolume());
        keyUp = prefs[4];
        keyDown = prefs[5];
        keyLeft = prefs[6];
        keyRight = prefs[7];
        displayMode = prefs[8];
        displayModeSelectBox.setSelectedIndex(displayMode);
        updateKeyLabels();
    }
    
    private void savePreferences() {
        int[] oldPrefs = userLogic.getPreferencias(main.username);
        userLogic.setPreferencias(main.username, (int)(main.getVolume()*100), (byte)oldPrefs[1], (byte)oldPrefs[2], oldPrefs[3] == 1, keyUp, keyDown, keyLeft, keyRight, displayMode);
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
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
    public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
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
        smallFont.dispose();
        sectionFont.dispose();
        backgroundTexture.dispose();
    }
}