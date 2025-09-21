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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class PreferenciasScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;
    private GestorIdiomas gestorIdiomas;
    private InputMultiplexer multiplexer;
    private BitmapFont pixelFont, titleFont, smallFont, sectionFont;
    private Texture backgroundTexture;
    private int keyUp, keyDown, keyLeft, keyRight;
    private Label keyUpLabel, keyDownLabel, keyLeftLabel, keyRightLabel, messageLabel;
    private TextButton waitingButton = null;
    private TextButton.TextButtonStyle originalButtonStyle, waitingButtonStyle;
    private SelectBox<String> displayModeSelectBox;
    private SelectBox<String> languageSelectBox;
    private int displayMode;
    private Slider volumeSlider;
    private Label titleLabel, volumeLabel, displayLabel, controlsLabel, upLabel, downLabel, leftLabel, rightLabel, languageLabel;
    private TextButton saveButton;


    public PreferenciasScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();
        gestorIdiomas = GestorIdiomas.obtenerInstancia();
        backgroundTexture = new Texture(Gdx.files.internal("background3.png"));
        setupFonts();
        createUI();
        loadPreferences();
        setupInputProcessor();
        updateKeyLabels();
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

        titleLabel = new Label("", titleLabelStyle);
        mainTable.add(titleLabel).padBottom(20).row();

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

        volumeLabel = new Label("", sectionStyle);
        contentTable.add(volumeLabel).colspan(3).padBottom(5).row();
        volumeSlider = new Slider(0, 1, 0.01f, false, skin);
        contentTable.add(volumeSlider).width(400).colspan(3).padBottom(25).row();

        displayLabel = new Label("", sectionStyle);
        contentTable.add(displayLabel).colspan(3).padBottom(5).row();
        displayModeSelectBox = new SelectBox<>(skin);
        displayModeSelectBox.setItems("Fullscreen", "Windowed", "Mini");
        contentTable.add(displayModeSelectBox).width(400).colspan(3).padBottom(25).row();

        languageLabel = new Label("", sectionStyle);
        contentTable.add(languageLabel).colspan(3).padBottom(5).row();
        languageSelectBox = new SelectBox<>(skin);
        languageSelectBox.setItems(gestorIdiomas.obtenerIdiomasDisponibles());
        contentTable.add(languageSelectBox).width(400).colspan(3).padBottom(25).row();

        controlsLabel = new Label("", sectionStyle);
        contentTable.add(controlsLabel).colspan(3).padBottom(15).row();
        keyUpLabel = new Label("", labelStyle);
        keyDownLabel = new Label("", labelStyle);
        keyLeftLabel = new Label("", labelStyle);
        keyRightLabel = new Label("", labelStyle);
        upLabel = new Label("", labelStyle);
        downLabel = new Label("", labelStyle);
        leftLabel = new Label("", labelStyle);
        rightLabel = new Label("", labelStyle);
        createKeybindRow(contentTable, "preferences.arriba", upLabel, keyUpLabel);
        createKeybindRow(contentTable, "preferences.abajo", downLabel, keyDownLabel);
        createKeybindRow(contentTable, "preferences.izquierda", leftLabel, keyLeftLabel);
        createKeybindRow(contentTable, "preferences.derecha", rightLabel, keyRightLabel);

        messageLabel = new Label("", labelStyle);
        contentTable.add(messageLabel).colspan(3).padTop(15).row();

        saveButton = new TextButton("", originalButtonStyle);
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

        languageSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String nuevoIdioma = languageSelectBox.getSelected();
                gestorIdiomas.cambiarIdioma(nuevoIdioma);
                updateLanguageLabels();
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
        updateLanguageLabels();
    }

    private void createKeybindRow(Table table, String descKey, Label descLabel, Label keyLabel) {
        descLabel.setText(gestorIdiomas.setTexto(descKey));
        table.add(descLabel).left().expandX().padLeft(50);
        table.add(keyLabel).width(120).center();

        TextButton changeButton = new TextButton(gestorIdiomas.setTexto("preferences.cambiar"), originalButtonStyle);
        changeButton.setName(descKey);

        changeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (waitingButton != null) {
                    waitingButton.setText(gestorIdiomas.setTexto("preferences.cambiar"));
                    waitingButton.setStyle(originalButtonStyle);
                }
                stage.setKeyboardFocus(null);
                waitingButton = (TextButton) event.getListenerActor();
                waitingButton.setText(gestorIdiomas.setTexto("preferences.esperando"));
                waitingButton.setStyle(waitingButtonStyle);
                messageLabel.setText(gestorIdiomas.setTexto("preferences.espera_tecla") + descLabel.getText());
                messageLabel.setColor(Color.WHITE);
            }
        });
        table.add(changeButton).size(150, 40).padBottom(10).padRight(50).row();
    }

    private void updateLanguageLabels() {
        titleLabel.setText(gestorIdiomas.setTexto("preferences.titulo"));
        volumeLabel.setText(gestorIdiomas.setTexto("preferences.volumen"));
        displayLabel.setText(gestorIdiomas.setTexto("preferences.display"));
        languageLabel.setText(gestorIdiomas.setTexto("preferences.idioma"));
        controlsLabel.setText(gestorIdiomas.setTexto("preferences.controles"));
        upLabel.setText(gestorIdiomas.setTexto("preferences.arriba"));
        downLabel.setText(gestorIdiomas.setTexto("preferences.abajo"));
        leftLabel.setText(gestorIdiomas.setTexto("preferences.izquierda"));
        rightLabel.setText(gestorIdiomas.setTexto("preferences.derecha"));
        saveButton.setText(gestorIdiomas.setTexto("preferences.guardar"));
        messageLabel.setText(gestorIdiomas.setTexto("preferences.esperando"));
    }


    private void setupInputProcessor() {
        multiplexer = new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (waitingButton != null) {
                    if (isKeyAlreadyUsed(keycode, waitingButton.getName())) {
                        messageLabel.setText(gestorIdiomas.setTexto("preferences.tecla_en_uso"));
                        messageLabel.setColor(Color.RED);
                    } else {
                        assignKey(waitingButton.getName(), keycode);
                        updateKeyLabels();
                        messageLabel.setText(gestorIdiomas.setTexto("preferences.control_actualizado"));
                        messageLabel.setColor(Color.GREEN);
                    }
                    waitingButton.setText(gestorIdiomas.setTexto("preferences.cambiar"));
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
        if (!"preferences.arriba".equals(action) && newKey == keyUp) return true;
        if (!"preferences.abajo".equals(action) && newKey == keyDown) return true;
        if (!"preferences.izquierda".equals(action) && newKey == keyLeft) return true;
        if (!"preferences.derecha".equals(action) && newKey == keyRight) return true;
        return false;
    }

    private void assignKey(String action, int keycode) {
        switch (action) {
            case "preferences.arriba": keyUp = keycode; break;
            case "preferences.abajo": keyDown = keycode; break;
            case "preferences.izquierda": keyLeft = keycode; break;
            case "preferences.derecha": keyRight = keycode; break;
        }
    }

    private void loadPreferences() {
        int[] prefs = userLogic.getPreferencias(main.username);
        main.setVolume(prefs[0] / 100f);
        volumeSlider.setValue(main.getVolume());
        gestorIdiomas.cambiarIdioma(gestorIdiomas.obtenerIdiomasDisponibles()[prefs[1]]);
        languageSelectBox.setSelectedIndex(prefs[1]);
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
        userLogic.setPreferencias(main.username, (int)(main.getVolume()*100), (byte) gestorIdiomas.getIdiomaIndex(), (byte)oldPrefs[2], oldPrefs[3] == 1, keyUp, keyDown, keyLeft, keyRight, displayMode);
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