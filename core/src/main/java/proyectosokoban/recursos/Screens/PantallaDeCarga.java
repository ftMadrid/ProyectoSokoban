package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class PantallaDeCarga implements Screen {

    private Stage stage;
    private Animation<TextureRegion> walkAnimation;
    private Texture[] walkFrames;
    private AnimatedActor transitionActor;

    public PantallaDeCarga(Stage stage) {
        this.stage = stage;
        walkFrames = new Texture[6];
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 6; i++) {
            walkFrames[i] = new Texture(Gdx.files.internal("Juego/muneco/moves/east_00" + i + ".png"));
            frames.add(new TextureRegion(walkFrames[i]));
        }
        walkAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        transitionActor = new AnimatedActor();
        transitionActor.setAnimation(walkAnimation);
        transitionActor.setSize(128, 128);
        transitionActor.setVisible(false);
        this.stage.addActor(transitionActor);
    }

    public void startTransitionAnimation() {
        transitionActor.setVisible(true);
        transitionActor.setPosition(-transitionActor.getWidth(), stage.getHeight() / 2 - transitionActor.getHeight() / 2);
        transitionActor.addAction(Actions.sequence(
            Actions.moveTo(stage.getWidth(), stage.getHeight() / 2 - transitionActor.getHeight() / 2, 1.0f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    transitionActor.setVisible(false);
                }
            })
        ));
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        for (Texture frame : walkFrames) {
            if (frame != null) frame.dispose();
        }
    }
}