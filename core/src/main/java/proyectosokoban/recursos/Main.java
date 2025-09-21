package proyectosokoban.recursos;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.GdxRuntimeException;
import proyectosokoban.recursos.Screens.IntroScreen;
import proyectosokoban.recursos.Screens.PantallaDeCarga;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;

public class Main extends Game {

    public String username;
    public Music menuMusic;
    public Music gameMusic;
    public Music lobbyMusic;
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
            gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Juego/audios/audiofondo.mp3"));
            gameMusic.setLooping(true);
        } catch (GdxRuntimeException e) {
            gameMusic = null;
        }
        
        try {
            lobbyMusic = Gdx.audio.newMusic(Gdx.files.internal("lobby.mp3"));
            lobbyMusic.setLooping(true);
        } catch (GdxRuntimeException e) {
            lobbyMusic = null;
        }
        
        backgroundScreen = new PantallaDeCarga();
        setScreen(new IntroScreen(this));
    }
    
    public void applyDisplayPreferences() {
        if (username != null && !username.isEmpty()) {
            LogicaUsuarios lu = new LogicaUsuarios();
            int[] prefs = lu.getPreferencias(username);
            int displayMode = prefs[8]; 

            switch (displayMode) {
                case 0: 
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    break;
                case 1: 
                    Gdx.graphics.setWindowedMode(1280, 720);
                    break;
                case 2: 
                    Gdx.graphics.setWindowedMode(900, 900);
                    break;
            }
        }
    }

    /**
     * Carga y aplica la preferencia de volumen guardada por el usuario.
     */
    public void loadAndApplyVolumePreference() {
        if (username != null && !username.isEmpty()) {
            LogicaUsuarios lu = new LogicaUsuarios();
            int[] prefs = lu.getPreferencias(username);
            setVolume(prefs[0] / 100f);
        }
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
        if (lobbyMusic != null && lobbyMusic.isPlaying()) {
            lobbyMusic.stop();
        }
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.setVolume(this.volume);
            menuMusic.play();
        }
    }
    
    public void playLobbyMusic() {
        if (gameMusic != null && gameMusic.isPlaying()) {
            gameMusic.stop();
        }
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
        if (lobbyMusic != null && !lobbyMusic.isPlaying()) {
            lobbyMusic.setVolume(this.volume);
            lobbyMusic.play();
        }
    }

    public void playGameMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
        if (lobbyMusic != null && lobbyMusic.isPlaying()) {
            lobbyMusic.stop();
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
        if (lobbyMusic != null) {
            lobbyMusic.stop();
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
        if (lobbyMusic != null) {
            lobbyMusic.setVolume(this.volume);
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
        if(lobbyMusic != null){
            lobbyMusic.dispose();
        }
        if (backgroundScreen != null) {
            backgroundScreen.dispose();
        }
        super.dispose();
    }
}