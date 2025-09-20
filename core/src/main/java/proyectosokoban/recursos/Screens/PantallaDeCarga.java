package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaDeCarga implements Screen {

    private Stage stage;
    private SpriteBatch batch;
    private Animation<TextureRegion> walkAnimation;
    private Texture[] walkFrames;
    private float stateTime;
    private boolean isTransitioning;

    public PantallaDeCarga() {
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        
        walkFrames = new Texture[6];
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 6; i++) {
            walkFrames[i] = new Texture(Gdx.files.internal("muneco/moves/east_00" + i + ".png"));
            frames.add(new TextureRegion(walkFrames[i]));
        }
        walkAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        
        isTransitioning = false;
    }

    public void startTransitionAnimation() {
        stateTime = 0f;
        isTransitioning = true;
    }

    public void stopTransitionAnimation() {
        isTransitioning = false;
    }

    public void renderAnimation(float delta) {
        if (isTransitioning) {
            stateTime += delta;
            TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
            batch.begin();
            batch.draw(currentFrame, Gdx.graphics.getWidth() - currentFrame.getRegionWidth() - 20, 20);
            batch.end();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void show() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        for (Texture frame : walkFrames) {
            if (frame != null) frame.dispose();
        }
    }
}