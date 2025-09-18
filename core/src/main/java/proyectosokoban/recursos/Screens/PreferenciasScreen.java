package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class PreferenciasScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;
    private InputMultiplexer multiplexer;

    // Variables para los controles del juego
    private int keyUp = Input.Keys.UP;
    private int keyDown = Input.Keys.DOWN;
    private int keyLeft = Input.Keys.LEFT;
    private int keyRight = Input.Keys.RIGHT;

    // Elementos de la UI
    private Label keyUpLabel, keyDownLabel, keyLeftLabel, keyRightLabel;
    private Label messageLabel;
    private TextButton waitingButton = null; // Boton que esta esperando una tecla

    public PreferenciasScreen(final Main main) {
        this.main = main;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        userLogic = new LogicaUsuarios();

        createUI();
        loadPreferences();

        // Se usa un InputMultiplexer para capturar tanto los clics en la UI como las teclas presionadas
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                // Si estamos esperando a que se presione una tecla para asignarla a un control...
                if (waitingButton != null) {
                    // Validar que la tecla no este en uso
                    if (isKeyAlreadyUsed(keycode, waitingButton.getName())) {
                        messageLabel.setText("Esa tecla ya esta en uso. Elige otra.");
                        messageLabel.setColor(Color.RED);
                    } else {
                        // Asignar la nueva tecla
                        assignKey(waitingButton.getName(), keycode);
                        updateKeyLabels();
                        messageLabel.setText("Control actualizado.");
                        messageLabel.setColor(Color.GREEN);
                    }
                    
                    // Restaurar el boton a su estado original
                    waitingButton.setText("Cambiar");
                    waitingButton = null;
                }
                return true;
            }
        });
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.pad(40);
        table.center();
        stage.addActor(table);
        
        Label title = new Label("Preferencias", skin, "default-font", Color.WHITE);
        title.setFontScale(2.5f);
        table.add(title).colspan(3).padBottom(40).row();
        
        // --- SECCION DE AUDIO ---
        table.add(new Label("Audio", skin)).colspan(3).padBottom(20).row();
        
        table.add(new Label("Volumen:", skin)).left().padRight(10);
        Slider volumeSlider = new Slider(0, 1, 0.01f, false, skin);
        volumeSlider.setValue(main.getVolume());
        table.add(volumeSlider).width(300).colspan(2).row();

        // --- SECCION DE CONTROLES ---
        table.add(new Label("Controles", skin)).colspan(3).padTop(40).padBottom(20).row();

        // Labels para mostrar la tecla actual
        keyUpLabel = new Label("", skin);
        keyDownLabel = new Label("", skin);
        keyLeftLabel = new Label("", skin);
        keyRightLabel = new Label("", skin);

        // Crear filas para cada control
        createKeybindRow(table, "Mover Arriba", "up", keyUpLabel);
        createKeybindRow(table, "Mover Abajo", "down", keyDownLabel);
        createKeybindRow(table, "Mover Izquierda", "left", keyLeftLabel);
        createKeybindRow(table, "Mover Derecha", "right", keyRightLabel);

        // Mensajes para el usuario
        messageLabel = new Label("Haz clic en 'Cambiar' y presiona la nueva tecla", skin);
        table.add(messageLabel).colspan(3).padTop(30).row();

        // Boton de Guardar
        TextButton saveButton = new TextButton("Guardar y Volver", skin);
        table.add(saveButton).colspan(3).size(300, 50).padTop(40);
        
        // --- LISTENERS ---
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.setVolume(((Slider)actor).getValue());
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

    /**
     * Crea una fila en la tabla para un control especifico.
     */
    private void createKeybindRow(Table table, String description, String action, Label keyLabel) {
        table.add(new Label(description, skin)).left().padRight(20);
        table.add(keyLabel).width(120).left();
        TextButton changeButton = new TextButton("Cambiar", skin);
        changeButton.setName(action); // Usamos el nombre para identificar la accion
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
        table.add(changeButton).size(100, 40).padBottom(10).row();
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
            (byte)oldPrefs[1], // idioma (no se cambia aqui)
            (byte)oldPrefs[2], // control (no se cambia aqui)
            oldPrefs[3] == 1,   // mute
            keyUp, keyDown, keyLeft, keyRight // Nuevos controles
        );
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
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
    }
}