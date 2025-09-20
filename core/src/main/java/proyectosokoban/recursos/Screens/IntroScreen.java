package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import proyectosokoban.recursos.Main;

public class IntroScreen implements Screen {

    private Main main;
    private SpriteBatch batch;
    private Texture introImage;
    private Music introMusic;

    private float elapsedTime = 0f;
    private final float duracion = 6f;
    private final float entrada = 1.0f;
    private final float salida = 1.5f;
    private final float display = 3f;

    private float alpha = 0f;

    public IntroScreen(Main main) {
        this.main = main;
        this.batch = new SpriteBatch();

        try {
            introImage = new Texture("intro.png");
        } catch (Exception e) {
            System.out.println("Error: No se pudo cargar 'intro.png'");
            introImage = createDefaultTexture();
        }

        try {
            introMusic = Gdx.audio.newMusic(Gdx.files.internal("intromusic.mp3"));
            introMusic.setLooping(false);
            introMusic.setVolume(0.7f);
        } catch (Exception e) {
            System.out.println("Error: No se pudo cargar 'intromusic.mp3'");
        }
    }

    private Texture createDefaultTexture() {
        return new Texture(new Pixmap(1, 1, Pixmap.Format.RGBA8888));
    }

    @Override
    public void show() {
        elapsedTime = 0f;
        alpha = 0f;

        if (introMusic != null) {
            introMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;

        calculateAlpha();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setColor(1f, 1f, 1f, alpha);

        float imageWidth = introImage.getWidth();
        float imageHeight = introImage.getHeight();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float scaleX = screenWidth * 0.8f / imageWidth;
        float scaleY = screenHeight * 0.8f / imageHeight;
        float scale = Math.min(scaleX, scaleY);

        float finalWidth = imageWidth * scale;
        float finalHeight = imageHeight * scale;

        float x = (screenWidth - finalWidth) / 2f;
        float y = (screenHeight - finalHeight) / 2f;

        batch.draw(introImage, x, y, finalWidth, finalHeight);
        batch.end();

        if (elapsedTime >= duracion) {
            goToMenuScreen();
        }
    }

    private void calculateAlpha() {
        if (elapsedTime <= entrada) {
            float progress = elapsedTime / entrada;
            alpha = Interpolation.fade.apply(progress);

        } else if (elapsedTime <= entrada + display) {
            alpha = 1f;

        } else {
            float fadeOutStart = entrada + display;
            float progress = (elapsedTime - fadeOutStart) / salida;
            alpha = 1f - Interpolation.fade.apply(progress);
        }
        alpha = Math.max(0f, Math.min(1f, alpha));
    }

    private void goToMenuScreen() {
        if (introMusic != null && introMusic.isPlaying()) {
            introMusic.stop();
        }

        main.setScreen(new LoginScreen(main));
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        if (introMusic != null && introMusic.isPlaying()) {
            introMusic.pause();
        }
    }

    @Override
    public void resume() {
        if (introMusic != null) {
            introMusic.play();
        }
    }

    @Override
    public void hide() {
        if (introMusic != null && introMusic.isPlaying()) {
            introMusic.stop();
        }
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (introImage != null) {
            introImage.dispose();
        }
        if (introMusic != null) {
            introMusic.dispose();
        }
    }
}
