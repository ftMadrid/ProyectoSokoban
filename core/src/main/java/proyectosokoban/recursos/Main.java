package proyectosokoban.recursos;

import proyectosokoban.recursos.Screens.MenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        
        this.setScreen(new MenuScreen(this));
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
