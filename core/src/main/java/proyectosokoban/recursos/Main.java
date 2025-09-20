package proyectosokoban.recursos;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.GdxRuntimeException;
import proyectosokoban.recursos.Screens.IntroScreen;
import proyectosokoban.recursos.Screens.PantallaDeCarga;

public class Main extends Game {

    public String username;
    public Music menuMusic;
    public Music gameMusic;
    private float volume = 1.0f;
    private PantallaDeCarga backgroundScreen;

    @Override
    public void create() {
        try {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("main.mp3"));
            menuMusic.setLooping(true);
        } catch (GdxRuntimeException e) {
            menuMusic = null;
        }

        try {
            gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audiofondo.mp3"));
            gameMusic.setLooping(true);
        } catch (GdxRuntimeException e) {
            gameMusic = null;
        }

        backgroundScreen = new PantallaDeCarga();
        setScreen(new IntroScreen(this));
    }

    public PantallaDeCarga getBackgroundScreen() {
        return backgroundScreen;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        if (backgroundScreen != null) {
            backgroundScreen.render(delta);
        }

        if (screen != null) {
            screen.render(delta);
        }

        if (backgroundScreen != null) {
            backgroundScreen.renderAnimation(delta);
        }
    }

    @Override
    public void setScreen(Screen screen) {
        if (this.screen != null) {
            this.screen.hide();
            this.screen.dispose();
        }
        this.screen = screen;
        if (this.screen != null) {
            this.screen.show();
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    public void playMenuMusic() {
        if (gameMusic != null && gameMusic.isPlaying()) {
            gameMusic.stop();
        }
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.setVolume(this.volume);
            menuMusic.play();
        }
    }

    public void playGameMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
        if (gameMusic != null && !gameMusic.isPlaying()) {
            gameMusic.setVolume(this.volume);
            gameMusic.play();
        }
    }

    public void stopAllMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
        }
        if (gameMusic != null) {
            gameMusic.stop();
        }
    }

    public void setVolume(float vol) {
        this.volume = vol;
        if (menuMusic != null) {
            menuMusic.setVolume(this.volume);
        }
        if (gameMusic != null) {
            gameMusic.setVolume(this.volume);
        }
    }

    public float getVolume() {
        return this.volume;
    }

    @Override
    public void dispose() {
        if (menuMusic != null) {
            menuMusic.dispose();
        }
        if (gameMusic != null) {
            gameMusic.dispose();
        }
        if (backgroundScreen != null) {
            backgroundScreen.dispose();
        }
        super.dispose();
    }
}
