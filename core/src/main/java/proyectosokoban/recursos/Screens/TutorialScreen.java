package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Music;

import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

public class TutorialScreen implements Screen {

    private final Main main;
    private Stage stage;
    private GestorIdiomas gestorIdiomas;
    private Texture[] imagenesPresentacion;
    private Image imagenActual;
    private int indiceImagenActual = 0;
    private boolean primeraVezJugar;
    private Music tutorialMusic;

    public TutorialScreen(final Main main, boolean primeraVezJugar) {
        this.main = main;
        this.stage = new Stage(new ScreenViewport());
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();
        this.primeraVezJugar = primeraVezJugar;

        cargarImagenesSegunIdioma();
        configurarUI();

        tutorialMusic = Gdx.audio.newMusic(Gdx.files.internal("tutorial/audio_tutorial.mp3"));
    }

    private void cargarImagenesSegunIdioma() {
        String idioma = gestorIdiomas.obtenerCodigoIdioma();
        String rutaBase = "tutorial/" + idioma + "/";

        imagenesPresentacion = new Texture[3];
        for (int i = 0; i < 3; i++) {
            String rutaImagen = rutaBase + "tutorial" + (i + 1) + ".png";
            imagenesPresentacion[i] = new Texture(Gdx.files.internal(rutaImagen));
        }
    }

    private void configurarUI() {
        stage.clear();

        if (imagenesPresentacion != null && indiceImagenActual < imagenesPresentacion.length) {
            imagenActual = new Image(imagenesPresentacion[indiceImagenActual]);
            imagenActual.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            imagenActual.setPosition(0, 0);
            stage.addActor(imagenActual);
        }
    }

    private void avanzarImagen() {
        indiceImagenActual++;

        if (indiceImagenActual < imagenesPresentacion.length) {
            configurarUI();
        } else {
            if (tutorialMusic != null) {
                tutorialMusic.stop();
            }

            main.playLobbyMusic();

            LogicaUsuarios userLogic = new LogicaUsuarios();
            userLogic.marcarTutorialCompletado(main.username);

            Screen siguienteScreen = new LevelSelectScreen(main);
            transicionSuave.fadeOutAndChangeScreen(main, stage, siguienteScreen);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        tutorialMusic.setLooping(true);
        tutorialMusic.play();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            avanzarImagen();
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (imagenActual != null) {
            imagenActual.setSize(width, height);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        tutorialMusic.stop();
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (imagenesPresentacion != null) {
            for (Texture texture : imagenesPresentacion) {
                if (texture != null) {
                    texture.dispose();
                }
            }
        }
        tutorialMusic.dispose();
    }
}
