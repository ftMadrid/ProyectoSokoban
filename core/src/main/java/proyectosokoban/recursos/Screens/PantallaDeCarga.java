package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PantallaDeCarga implements Screen {

    private Stage stage;
    private Animation<TextureRegion> walkAnimation;
    private Texture[] walkFrames;
    private AnimatedActor transitionActor;
    private boolean isTransitioning;

    public PantallaDeCarga() {
        stage = new Stage(new ScreenViewport());
        
        walkFrames = new Texture[6];
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 6; i++) {
            walkFrames[i] = new Texture(Gdx.files.internal("Juego/muneco/moves/east_00" + i + ".png"));
            frames.add(new TextureRegion(walkFrames[i]));
        }
        walkAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        
        transitionActor = new AnimatedActor();
        transitionActor.setAnimation(walkAnimation);
        transitionActor.setSize(64, 64);
        transitionActor.setVisible(false);
        stage.addActor(transitionActor);

        isTransitioning = false;
    }

    public void startTransitionAnimation() {
        transitionActor.setVisible(true);
        transitionActor.setPosition(stage.getWidth(), 20);
        transitionActor.addAction(Actions.moveTo(stage.getWidth() - transitionActor.getWidth() - 20, 20, 0.5f));
        isTransitioning = true;
    }

    public void stopTransitionAnimation() {
        transitionActor.clearActions();
        transitionActor.setVisible(false);
        isTransitioning = false;
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
        if (transitionActor.isVisible()) {
            transitionActor.setPosition(width - transitionActor.getWidth() - 20, 20);
        }
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
        for (Texture frame : walkFrames) {
            if (frame != null) frame.dispose();
        }
    }
}