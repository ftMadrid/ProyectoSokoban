package proyectosokoban.recursos;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.GdxRuntimeException;
import proyectosokoban.recursos.Screens.IntroScreen;
import proyectosokoban.recursos.Screens.LoginScreen;

public class Main extends Game {
    public String username;
    public Music menuMusic;
    public Music gameMusic;
    private float volume = 1.0f;

    @Override
    public void create() {
        try {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("main.mp3"));
            menuMusic.setLooping(true);
        } catch (GdxRuntimeException e) {
            Gdx.app.log("MusicLoader", "No se pudo cargar 'main.mp3'.");
            menuMusic = null;
        }

        try {
            gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audiofondo.mp3"));
            gameMusic.setLooping(true);
        } catch (GdxRuntimeException e) {
            Gdx.app.log("MusicLoader", "No se pudo cargar 'audiofondo.mp3'.");
            gameMusic = null;
        }
        
        setScreen(new IntroScreen(this));
    }

    // --- METODOS PARA CONTROLAR LA MUSICA ---

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
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
        if (gameMusic != null && gameMusic.isPlaying()) {
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
        if (menuMusic != null) menuMusic.dispose();
        if (gameMusic != null) gameMusic.dispose();
        super.dispose();
    }
}