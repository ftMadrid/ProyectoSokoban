package proyectosokoban.recursos.Utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import proyectosokoban.recursos.Main;

public class transicionSuave {

    private static final float TRANSITION_DURATION = 1.0f;

    public static void fadeOutAndChangeScreen(final Main main, final Stage currentStage, final Screen newScreen) {
        main.getTransitionAnimation().startTransitionAnimation();
        Gdx.input.setInputProcessor(null);
        currentStage.getRoot().addAction(Actions.sequence(
            Actions.fadeOut(TRANSITION_DURATION),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    main.setScreen(newScreen);
                }
            })
        ));
    }

    public static void fadeIn(final Stage stage) {
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(Actions.fadeIn(TRANSITION_DURATION));
        Gdx.input.setInputProcessor(stage);
    }
}