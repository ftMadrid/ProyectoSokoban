package proyectosokoban.recursos.Screens;

import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PreferenciasScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Skin skin;
    private LogicaUsuarios userLogic;

    private Slider volumenSlider;
    private TextButton muteButton;
    private float lastVolume;
    private boolean isMuted;

    public PreferenciasScreen(final Main main) {
        this.main = main;
        this.userLogic = new LogicaUsuarios();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        createUI();
        cargarPreferencias();
    }

    private void createUI() {
        Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Preferencias", skin);
        title.setFontScale(2.0f);
        table.add(title).padBottom(40).row();
        
        Table audioTable = new Table();
        audioTable.add(new Label("Volumen:", skin)).padRight(10);
        
        volumenSlider = new Slider(0, 100, 1, false, skin);
        audioTable.add(volumenSlider).width(300).row();

        muteButton = new TextButton("Silenciar", skin);
        audioTable.add(muteButton).colspan(2).padTop(20).size(300, 50).row();

        table.add(audioTable).padBottom(40).row();

        TextButton backButton = new TextButton("Volver", skin);
        table.add(backButton).size(300, 50);

        volumenSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volumen = volumenSlider.getValue() / 100f;
                main.musicafondo.setVolume(volumen);
                if (volumen > 0) {
                    isMuted = false;
                } else {
                    isMuted = true;
                }
                guardarPreferencias();
            }
        });

        muteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isMuted = !isMuted;
                if (isMuted) {
                    lastVolume = main.musicafondo.getVolume();
                    main.musicafondo.setVolume(0);
                    volumenSlider.setValue(0);
                } else {
                    if (lastVolume == 0) lastVolume = 0.8f;
                    main.musicafondo.setVolume(lastVolume);
                    volumenSlider.setValue((int)(lastVolume * 100));
                }
                guardarPreferencias();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(new MenuScreen(main));
                dispose();
            }
        });
    }

    private void cargarPreferencias() {
        if (main.username != null) {
            int[] prefs = userLogic.getPreferencias(main.username);
            int volumen = prefs[0];
            isMuted = prefs[3] == 1;
            
            lastVolume = volumen / 100f;
            
            main.musicafondo.setVolume(isMuted ? 0 : lastVolume);
            volumenSlider.setValue(isMuted ? 0 : volumen);
        } else {
            lastVolume = 0.8f;
            isMuted = false;
            volumenSlider.setValue(80);
            main.musicafondo.setVolume(lastVolume);
        }
    }

    private void guardarPreferencias() {
        if (main.username != null) {
            int volumen = (int) volumenSlider.getValue();
            userLogic.setPreferencias(main.username, volumen, (byte) 0, (byte) 0, isMuted);
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
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
        skin.dispose();
    }
}