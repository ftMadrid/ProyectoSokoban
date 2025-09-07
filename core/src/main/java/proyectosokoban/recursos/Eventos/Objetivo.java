package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Objetivo {

    private Texture textura;
    private int x, y;

    public Objetivo(int x, int y) {
        this.x = x;
        this.y = y;
        this.textura = new Texture("objetivo.png");
    }

    public void render(SpriteBatch batch, int tileSize) {
        batch.draw(textura, x * tileSize + 30, y * tileSize + 30, 40, 40);
    }

    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
