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
        float scale = 0.6f; // 80% del tile
        float renderX = x * tileSize + (tileSize * (1 - scale) / 2f);
        float renderY = y * tileSize + (tileSize * (1 - scale) / 2f);
        float renderSize = tileSize * scale;

        batch.draw(textura, renderX-5, renderY-5, renderSize+10, renderSize+10);
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
