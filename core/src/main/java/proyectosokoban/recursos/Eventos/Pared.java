package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Pared {

    private Texture textura;
    private int x, y;

    public Pared(int x, int y) {
        this.x = x;
        this.y = y;
        this.textura = new Texture("pared.png");
    }

    public void render(SpriteBatch batch, int tileSize) {
        batch.draw(textura, x * tileSize, y * tileSize, tileSize, tileSize);
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
