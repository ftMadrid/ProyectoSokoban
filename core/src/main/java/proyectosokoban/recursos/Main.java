package proyectosokoban.recursos;

import proyectosokoban.recursos.Screens.LoginScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public String username;
    public Music musicafondo;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        musicafondo = Gdx.audio.newMusic(Gdx.files.internal("main.mp3"));
        musicafondo.setLooping(true);
        musicafondo.setVolume(0.8f);

        this.setScreen(new LoginScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        musicafondo.dispose();
    }
}