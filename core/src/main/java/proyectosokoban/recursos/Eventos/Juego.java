package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.Screen;
import proyectosokoban.recursos.Main;

public abstract class Juego implements Screen {

    protected final Main main;

    public Juego(final Main main) {
        this.main = main;
    }

    // Métodos abstractos que deben ser implementados por los juegos específicos
    public abstract void inicializarRecursos();

    public abstract void actualizar(float delta);

    public abstract void renderizar();

    public abstract void dispose();

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
