package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class PreferenciasScreen implements Screen {

    private final Main main;
    private final Stage stage;
    private final LogicaUsuarios userLogic;
    private final GestorIdiomas gestorIdiomas;

    // Assets
    private Texture backgroundTexture;
    private Texture fieldTex;
    private Texture buttonTex;
    private Texture sliderTrackTex;
    private Texture sliderKnobTex;
    private Texture solidTexture;

    // Fuentes
    private BitmapFont titleFont, sectionFont, pixelFont, smallFont;

    // UI
    private Slider volumeSlider;
    private CycleButton displayButton;
    private CycleButton languageButton;
    private Label msgLabel;
    private KeyButton keyUpButton, keyDownButton, keyLeftButton, keyRightButton;

    // Controles actuales
    private int keyUp, keyDown, keyLeft, keyRight;

    // Estado de captura de teclas
    private InputMultiplexer multiplexer;
    private KeyButton waitingKeyButton = null;
    private boolean captureMode = false;
    private long lastMsgTime = 0;

    public PreferenciasScreen(Main main) {
        this.main = main;
        this.stage = new Stage(new ScreenViewport());
        this.userLogic = new LogicaUsuarios();
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();

        // Cargar assets
        backgroundTexture = new Texture(Gdx.files.internal("background2.png"));
        fieldTex = new Texture(Gdx.files.internal("ui/field 2.png"));
        buttonTex = new Texture(Gdx.files.internal("ui/button1.png"));

        createSliderTextures();
        solidTexture = createSolidTexture(1, 1, Color.GRAY);
        loadFonts();
        buildUI();
        setupInput();
        loadPreferences();
    }

    private void createSliderTextures() {
        // Track del slider
        Pixmap track = new Pixmap(200, 6, Pixmap.Format.RGBA8888);
        track.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        track.fill();
        sliderTrackTex = new Texture(track);
        track.dispose();

        // Knob del slider
        Pixmap knob = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        knob.setColor(0.95f, 0.85f, 0.75f, 1f);
        knob.fillCircle(10, 10, 9);
        sliderKnobTex = new Texture(knob);
        knob.dispose();
    }
    
    private Texture createSolidTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    private void setupInput() {
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void loadFonts() {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size = 64; p.color = Color.valueOf("F5F5DC");
        titleFont = gen.generateFont(p);
        
        p.size = 32; p.color = Color.valueOf("1E1E1E");
        sectionFont = gen.generateFont(p);
        
        p.size = 24;
        pixelFont = gen.generateFont(p);
        
        p.size = 18; p.color = Color.valueOf("F5F5DC");
        smallFont = gen.generateFont(p);

        gen.dispose();
    }

    private void buildUI() {
    Table root = new Table();
    root.setFillParent(true);
    stage.addActor(root);

    // Título
    Label titulo = new Label(gestorIdiomas.setTexto("app.name"), new Label.LabelStyle(titleFont, Color.valueOf("F5F5DC")));
    root.add(titulo).padBottom(10).row();

    // Panel principal - ligeramente más pequeño para reducir espacio libre
    Table mainPanel = new Table();
    mainPanel.setBackground(new TextureRegionDrawable(new TextureRegion(fieldTex)));
    
    float panelWidth = Math.min(800, Gdx.graphics.getWidth() * 0.8f);
    float panelHeight = Math.min(600, Gdx.graphics.getHeight() * 0.7f);
    
    root.add(mainPanel).width(panelWidth).height(panelHeight).padBottom(5).row();

    // Contenido del panel con menos padding vertical
    Table content = new Table();
    content.pad(20, 20, 15, 20); // top, left, bottom, right (menos espacio abajo)
    mainPanel.add(content).expand().fill();

    // SECCIÓN SUPERIOR: Opciones generales
    Table topSection = new Table();
    content.add(topSection).expandX().fillX().padBottom(15).row();

    Table optionsTable = new Table();
    optionsTable.defaults().uniform().fill();
    topSection.add(optionsTable).expandX().fillX().padBottom(8).row();

    // Volumen
    Table volumeContainer = new Table();
    optionsTable.add(volumeContainer).expand().fill().padRight(10);
    
    Label volumeLabel = new Label(gestorIdiomas.setTexto("preferences.volumen"), 
                                 new Label.LabelStyle(sectionFont, Color.valueOf("1E1E1E")));
    volumeContainer.add(volumeLabel).center().padBottom(4).row();
    
    volumeSlider = new Slider(0, 1, 0.01f, false, createSliderStyle());
    volumeSlider.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            main.setVolume(volumeSlider.getValue());
        }
    });
    volumeContainer.add(volumeSlider).width(140).height(18).center();

    // Display
    Table displayContainer = new Table();
    optionsTable.add(displayContainer).expand().fill().padRight(10);
    
    Label displayLabel = new Label(gestorIdiomas.setTexto("preferences.display"), 
                                  new Label.LabelStyle(sectionFont, Color.valueOf("1E1E1E")));
    displayContainer.add(displayLabel).center().padBottom(4).row();
    
    displayButton = new CycleButton(
        new String[]{"Fullscreen", "Windowed", "Borderless"},
        createButtonStyle()
    );
    displayButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            int displayMode = displayButton.getIndex();
            main.applyDisplayMode(displayMode);
        }
    });
    displayContainer.add(displayButton).width(130).height(32).center();

    // Idioma
    Table languageContainer = new Table();
    optionsTable.add(languageContainer).expand().fill();
    
    Label languageLabel = new Label(gestorIdiomas.setTexto("preferences.idioma"), 
                                   new Label.LabelStyle(sectionFont, Color.valueOf("1E1E1E")));
    languageContainer.add(languageLabel).center().padBottom(4).row();
    
    languageButton = new CycleButton(
        new String[]{"Español", "English", "Italiano"},
        createButtonStyle()
    );
    languageButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            String[] idiomas = {"es", "en", "ita"};
            gestorIdiomas.cambiarIdioma(idiomas[languageButton.getIndex()]);
            updateUITexts();
        }
    });
    languageContainer.add(languageButton).width(120).height(32).center();

    // Espacio moderado entre opciones y controles
    content.add().height(15).row(); // Espacio fijo en lugar de expandible

    // SECCIÓN DE CONTROLES
    Label controlsLabel = new Label(gestorIdiomas.setTexto("preferences.controles"), 
                                   new Label.LabelStyle(sectionFont, Color.valueOf("1E1E1E")));
    content.add(controlsLabel).center().padBottom(10).row();

    // Tabla para el D-pad
    Table dpadTable = new Table();
    content.add(dpadTable).center().row();

    // Fila de ARRIBA
    Table upTable = new Table();
    upTable.defaults().padBottom(3);
    Label upLabel = new Label(gestorIdiomas.setTexto("preferences.arriba"), 
                             new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E")));
    keyUpButton = new KeyButton("W", "up", createKeyButtonStyle());
    keyUpButton.addListener(createKeyListener(keyUpButton));
    upTable.add(upLabel).row();
    upTable.add(keyUpButton).width(70).height(35);
    dpadTable.add(upTable).center().row();

    // Fila de IZQUIERDA y DERECHA
    Table middleTable = new Table();
    middleTable.defaults().pad(8);

    // Izquierda
    Table leftTable = new Table();
    Label leftLabel = new Label(gestorIdiomas.setTexto("preferences.izquierda"), 
                               new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E")));
    keyLeftButton = new KeyButton("A", "left", createKeyButtonStyle());
    keyLeftButton.addListener(createKeyListener(keyLeftButton));
    leftTable.add(leftLabel).padRight(5);
    leftTable.add(keyLeftButton).width(70).height(35);

    // Derecha
    Table rightTable = new Table();
    Label rightLabel = new Label(gestorIdiomas.setTexto("preferences.derecha"), 
                                new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E")));
    keyRightButton = new KeyButton("D", "right", createKeyButtonStyle());
    keyRightButton.addListener(createKeyListener(keyRightButton));
    rightTable.add(keyRightButton).width(70).height(35);
    rightTable.add(rightLabel).padLeft(5);

    middleTable.add(leftTable);
    middleTable.add().width(40);
    middleTable.add(rightTable);
    dpadTable.add(middleTable).center().row();

    // Fila de ABAJO
    Table downTable = new Table();
    downTable.defaults().padTop(3);
    Label downLabel = new Label(gestorIdiomas.setTexto("preferences.abajo"), 
                               new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E")));
    keyDownButton = new KeyButton("S", "down", createKeyButtonStyle());
    keyDownButton.addListener(createKeyListener(keyDownButton));
    downTable.add(downLabel).row();
    downTable.add(keyDownButton).width(70).height(35);
    dpadTable.add(downTable).center().row();

    // Espacio moderado entre controles y botones
    content.add().height(20).row(); // Espacio fijo en lugar de expandible

    // Botones inferiores - centrados en la parte inferior
    Table buttonTable = new Table();
    buttonTable.defaults().pad(5);
    content.add(buttonTable).center().padBottom(10).row();

    // Botón guardar
    TextButton saveButton = new TextButton(gestorIdiomas.setTexto("preferences.guardar"), createButtonStyle());
    saveButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            savePreferences();
            transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
        }
    });
    buttonTable.add(saveButton).width(180).height(38).padRight(10);

    // Botón cancelar
    TextButton cancelButton = new TextButton(gestorIdiomas.setTexto("back.button"), createButtonStyle());
    // Ajustar escala de fuente si el texto es demasiado largo
    if (gestorIdiomas.setTexto("back.button").length() > 12) {
        cancelButton.getLabel().setFontScale(0.85f);
    }
    cancelButton.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            loadPreferences();
            transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
        }
    });
    buttonTable.add(cancelButton).width(150).height(38);

    // Mensaje de estado
    msgLabel = new Label("", new Label.LabelStyle(smallFont, Color.WHITE));
    msgLabel.setAlignment(Align.center);
    root.add(msgLabel).height(20).padBottom(5).row();
}

    private void updateUITexts() {
        // Actualizar todos los textos de la interfaz según el idioma actual
        msgLabel.setText("");
    }

    private Slider.SliderStyle createSliderStyle() {
        Slider.SliderStyle style = new Slider.SliderStyle();
        style.background = new TextureRegionDrawable(new TextureRegion(sliderTrackTex));
        TextureRegionDrawable knobDrawable = new TextureRegionDrawable(new TextureRegion(sliderKnobTex));
        knobDrawable.setMinWidth(20);
        knobDrawable.setMinHeight(20);
        style.knob = knobDrawable;
        return style;
    }

    private TextButton.TextButtonStyle createButtonStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTex));
        style.up = buttonDrawable;
        style.down = buttonDrawable;
        style.over = buttonDrawable;
        style.font = pixelFont;
        style.fontColor = Color.valueOf("1E1E1E");
        return style;
    }

    private TextButton.TextButtonStyle createKeyButtonStyle() {
        TextButton.TextButtonStyle style = createButtonStyle();
        style.fontColor = Color.valueOf("1E1E1E");
        style.overFontColor = Color.valueOf("FFD700");
        style.downFontColor = Color.valueOf("FF6B6B");
        return style;
    }

    private ClickListener createKeyListener(final KeyButton button) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startKeyCapture(button);
            }
        };
    }

    private void startKeyCapture(KeyButton button) {
        // Cancelar captura anterior si existe
        if (waitingKeyButton != null) {
            waitingKeyButton.setWaiting(false);
        }
        
        waitingKeyButton = button;
        captureMode = true;
        button.setWaiting(true);
        
        msgLabel.setText(gestorIdiomas.setTexto("preferences.espera_tecla") + getActionName(button.getAction()));
        msgLabel.setColor(Color.YELLOW);
        lastMsgTime = System.currentTimeMillis();
        
        // Cambiar el input processor para capturar solo teclas
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (captureMode && waitingKeyButton != null) {
                    handleKeyCapture(keycode);
                    return true;
                }
                return false;
            }
        });
    }

    private void handleKeyCapture(int keycode) {
        // Ignorar teclas especiales
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.UNKNOWN) {
            cancelKeyCapture();
            return;
        }
        
        // Verificar si la tecla ya está en uso
        String action = waitingKeyButton.getAction();
        if (isKeyInUse(keycode, action)) {
            msgLabel.setText(gestorIdiomas.setTexto("preferences.tecla_en_uso"));
            msgLabel.setColor(Color.RED);
            lastMsgTime = System.currentTimeMillis();
            return;
        }
        
        // Asignar la nueva tecla
        assignKey(action, keycode);
        updateKeyButtons();
        
        msgLabel.setText(gestorIdiomas.setTexto("preferences.control_actualizado") + ": " + Input.Keys.toString(keycode));
        msgLabel.setColor(Color.GREEN);
        lastMsgTime = System.currentTimeMillis();
        
        // Restaurar el input processor normal
        Gdx.input.setInputProcessor(multiplexer);
        
        // Limpiar estado de captura
        waitingKeyButton.setWaiting(false);
        waitingKeyButton = null;
        captureMode = false;
    }

    private void cancelKeyCapture() {
        if (waitingKeyButton != null) {
            waitingKeyButton.setWaiting(false);
            waitingKeyButton = null;
        }
        captureMode = false;
        msgLabel.setText(gestorIdiomas.setTexto("preferences.esperando"));
        msgLabel.setColor(Color.ORANGE);
        lastMsgTime = System.currentTimeMillis();
        
        // Restaurar el input processor normal
        Gdx.input.setInputProcessor(multiplexer);
    }

    private boolean isKeyInUse(int keycode, String currentAction) {
        if (!"up".equals(currentAction) && keycode == keyUp) return true;
        if (!"down".equals(currentAction) && keycode == keyDown) return true;
        if (!"left".equals(currentAction) && keycode == keyLeft) return true;
        if (!"right".equals(currentAction) && keycode == keyRight) return true;
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

    private void updateKeyButtons() {
        keyUpButton.setText(Input.Keys.toString(keyUp));
        keyDownButton.setText(Input.Keys.toString(keyDown));
        keyLeftButton.setText(Input.Keys.toString(keyLeft));
        keyRightButton.setText(Input.Keys.toString(keyRight));
    }

    private String getActionName(String action) {
        switch (action) {
            case "up": return gestorIdiomas.setTexto("preferences.arriba");
            case "down": return gestorIdiomas.setTexto("preferences.abajo");
            case "left": return gestorIdiomas.setTexto("preferences.izquierda");
            case "right": return gestorIdiomas.setTexto("preferences.derecha");
            default: return action.toUpperCase();
        }
    }

    private void loadPreferences() {
        int[] prefs = userLogic.getPreferencias(main.username);
        
        // Volumen
        float volume = prefs[0] / 100f;
        main.setVolume(volume);
        volumeSlider.setValue(volume);
        
        // Display
        int displayIndex = Math.max(0, Math.min(2, prefs[8]));
        displayButton.setIndex(displayIndex);
        
        // Idioma
        int languageIndex = Math.max(0, Math.min(2, prefs[1]));
        languageButton.setIndex(languageIndex);
        
        // Controles - cargar los valores guardados
        keyUp = prefs[4];
        keyDown = prefs[5];
        keyLeft = prefs[6];
        keyRight = prefs[7];
        
        // Si no hay controles guardados o son inválidos, usar WASD por defecto
        if (keyUp == 0 || keyDown == 0 || keyLeft == 0 || keyRight == 0) {
            keyUp = Input.Keys.W;
            keyDown = Input.Keys.S;
            keyLeft = Input.Keys.A;
            keyRight = Input.Keys.D;
        }
        
        // Actualizar controles en Main también
        main.updateControls(keyUp, keyDown, keyLeft, keyRight);
        
        updateKeyButtons();
    }

    private void savePreferences() {
        int[] oldPrefs = userLogic.getPreferencias(main.username);

        userLogic.setPreferencias(
            main.username,
            (int)(volumeSlider.getValue() * 100),
            (byte) languageButton.getIndex(),  // idioma
            (byte) oldPrefs[2],  // otros ajustes
            (oldPrefs[3] == 1),  // mute (como boolean - true si es 1, false si es 0)
            keyUp, keyDown, keyLeft, keyRight,  // guardar controles actuales
            (byte) displayButton.getIndex()
        );
        
        // Aplicar los nuevos controles al juego
        main.updateControls(keyUp, keyDown, keyLeft, keyRight);
        
        // Mensaje de confirmación
        msgLabel.setText(gestorIdiomas.setTexto("preferences.control_actualizado"));
        msgLabel.setColor(Color.GREEN);
        lastMsgTime = System.currentTimeMillis();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
        transicionSuave.fadeIn(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar fondo
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, 
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
        
        // Limpiar mensaje después de 3 segundos
        if (msgLabel.getText().length > 0 && System.currentTimeMillis() - lastMsgTime > 3000) {
            msgLabel.setText("");
        }
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
        sectionFont.dispose();
        pixelFont.dispose();
        smallFont.dispose();
        backgroundTexture.dispose();
        fieldTex.dispose();
        buttonTex.dispose();
        sliderTrackTex.dispose();
        sliderKnobTex.dispose();
        solidTexture.dispose();
    }

    // Clase para botones cíclicos
    private static class CycleButton extends TextButton {
        private String[] options;
        private int currentIndex = 0;

        public CycleButton(String[] options, TextButtonStyle style) {
            super(options[0], style);
            this.options = options;
            
            addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    cycle();
                }
            });
        }

        private void cycle() {
            currentIndex = (currentIndex + 1) % options.length;
            setText(options[currentIndex]);
        }

        public int getIndex() {
            return currentIndex;
        }

        public void setIndex(int index) {
            currentIndex = Math.max(0, Math.min(index, options.length - 1));
            setText(options[currentIndex]);
        }
    }

    // Clase para botones de teclas
    private static class KeyButton extends TextButton {
        private String action;
        private boolean waiting = false;
        private Color normalColor;
        private Color waitingColor = Color.YELLOW;

        public KeyButton(String text, String action, TextButtonStyle style) {
            super(text, style);
            this.action = action;
            this.normalColor = style.fontColor.cpy();
        }

        public String getAction() {
            return action;
        }

        public void setWaiting(boolean waiting) {
            this.waiting = waiting;
            if (waiting) {
                getStyle().fontColor = waitingColor;
                setText("...");
            } else {
                getStyle().fontColor = normalColor;
            }
        }

        public boolean isWaiting() {
            return waiting;
        }
    }
}