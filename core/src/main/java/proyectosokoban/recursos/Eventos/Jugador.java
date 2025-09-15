package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.concurrent.atomic.AtomicBoolean;
import com.badlogic.gdx.math.MathUtils;

public class Jugador {

    private Texture[] texturasNormales; // Arriba, abajo, izquierda, derecha
    private Texture[][] texturasEmpuje; // Animaciones de empuje [direccion][frame]
    private Texture[][] texturasMovimiento; // Animaciones de movimiento normal [direccion][frame]

    private volatile int x, y; // coordenadas de celda
    private volatile float renderX, renderY; // posición interpolada
    private volatile int direccion; // 0=abajo, 1=arriba, 2=izquierda, 3=derecha

    private final AtomicBoolean estaMoviendose = new AtomicBoolean(false);
    private final AtomicBoolean estaEmpujando = new AtomicBoolean(false);

    // Duraciones de animación
    private volatile float tiempoAnimacion = 0f;
    private final float duracionAnimacion = 1.0f;

    private volatile float tiempoAnimacionMovimiento = 0f;
    private volatile int frameActualMovimiento = 0;

    private volatile float tiempoAnimacionEmpuje = 0f;
    private final int FRAMES_EMPUEJE = 6;
    private volatile int frameActualEmpuje = 0;

    private volatile float startX, startY, targetX, targetY;

    public Jugador(int x, int y, int TILE) {
        this.x = x;
        this.y = y;
        this.renderX = x * TILE;
        this.renderY = y * TILE;
        this.direccion = 0; // mirando abajo
        cargarTexturas();
    }

    private void cargarTexturas() {
        texturasNormales = new Texture[4];
        texturasNormales[0] = new Texture("muneco/south.png"); // abajo
        texturasNormales[1] = new Texture("muneco/north.png"); // arriba
        texturasNormales[2] = new Texture("muneco/west.png");  // izquierda
        texturasNormales[3] = new Texture("muneco/east.png");  // derecha

        texturasEmpuje = new Texture[4][FRAMES_EMPUEJE];
        for (int i = 0; i < FRAMES_EMPUEJE; i++) {
            texturasEmpuje[0][i] = new Texture("muneco/caja/south_00" + i + ".png");
            texturasEmpuje[1][i] = new Texture("muneco/caja/north_00" + i + ".png");
            texturasEmpuje[2][i] = new Texture("muneco/caja/west_00" + i + ".png");
            texturasEmpuje[3][i] = new Texture("muneco/caja/east_00" + i + ".png");
        }

        texturasMovimiento = new Texture[4][4]; // 4 frames por movimiento normal
        for (int i = 0; i < 4; i++) {
            texturasMovimiento[0][i] = new Texture("muneco/moves/south_00" + i + ".png");
            texturasMovimiento[1][i] = new Texture("muneco/moves/north_00" + i + ".png");
            texturasMovimiento[2][i] = new Texture("muneco/moves/west_00" + i + ".png");
            texturasMovimiento[3][i] = new Texture("muneco/moves/east_00" + i + ".png");
        }
    }

    public boolean mover(int dx, int dy, Nivel nivel, int TILE) {
        // Actualizar dirección
        
        if (dx > 0) {
            direccion = 3;
        } else if (dx < 0) {
            direccion = 2;
        } else if (dy > 0) {
            direccion = 1;
        } else if (dy < 0) {
            direccion = 0;
        }

        int nuevoX = x + dx;
        int nuevoY = y + dy;

        if (nivel.esPared(nuevoX, nuevoY)) {
            return false;
        }

        startX = renderX;
        startY = renderY;

        if (nivel.hayCajaEn(nuevoX, nuevoY)) {
            int nuevoCajaX = nuevoX + dx;
            int nuevoCajaY = nuevoY + dy;

            if (!nivel.esPared(nuevoCajaX, nuevoCajaY) && !nivel.hayCajaEn(nuevoCajaX, nuevoCajaY)) {
                nivel.moverCaja(nuevoX, nuevoY, nuevoCajaX, nuevoCajaY);
                x = nuevoX;
                y = nuevoY;

                targetX = x * TILE;
                targetY = y * TILE;

                estaEmpujando.set(true);
                frameActualEmpuje = 0;
                tiempoAnimacionEmpuje = 0f;

                estaMoviendose.set(true);
                tiempoAnimacion = 0f;

                return true;
            }
            return false;
        } else {
            x = nuevoX;
            y = nuevoY;

            targetX = x * TILE;
            targetY = y * TILE;

            estaEmpujando.set(false);
            estaMoviendose.set(true);
            tiempoAnimacion = 0f;

            return true;
        }
    }

    public void actualizar(float delta) {
        if (estaMoviendose.get()) {
            tiempoAnimacion += delta;
            float progreso = Math.min(tiempoAnimacion / duracionAnimacion, 1f);
            progreso = 1f - (1f - progreso) * (1f - progreso); // easing
            renderX = MathUtils.lerp(startX, targetX, progreso);
            renderY = MathUtils.lerp(startY, targetY, progreso);

            // Movimiento normal
            if (!estaEmpujando.get()) {
                tiempoAnimacionMovimiento += delta;
                if (tiempoAnimacionMovimiento >= duracionAnimacion / 4f) {
                    frameActualMovimiento = (frameActualMovimiento + 1) % texturasMovimiento[direccion].length;
                    tiempoAnimacionMovimiento = 0f;
                }
            }

            if (progreso >= 1f) {
                estaMoviendose.set(false);
                renderX = targetX;
                renderY = targetY;
                frameActualMovimiento = 0;
                tiempoAnimacionMovimiento = 0f;
                estaEmpujando.set(false);
                frameActualEmpuje = 0;
                tiempoAnimacionEmpuje = 0f;
            }
        }

        // Animación de empuje
        if (estaEmpujando.get()) {
            tiempoAnimacionEmpuje += delta;
            float duracionFrameEmpuje = duracionAnimacion / FRAMES_EMPUEJE;
            if (tiempoAnimacionEmpuje >= duracionFrameEmpuje) {
                frameActualEmpuje = (frameActualEmpuje + 1) % FRAMES_EMPUEJE;
                tiempoAnimacionEmpuje = 0f;
            }
        }
    }

    private Texture getTexturaActual() {
        if (estaEmpujando.get()) {
            return texturasEmpuje[direccion][frameActualEmpuje];
        }
        if (estaMoviendose.get()) {
            return texturasMovimiento[direccion][frameActualMovimiento];
        }
        return texturasNormales[direccion];
    }

    public void render(SpriteBatch batch, int TILE) {
        float ancho = TILE + 35;
        float alto = TILE + 35;

        float offsetX = (TILE - ancho) / 2f;  // negativo si ancho > TILE
        float offsetY = (TILE - alto) / 2f;   // negativo si alto > TILE

        batch.draw(getTexturaActual(), renderX + offsetX, renderY + offsetY, ancho, alto);
    }

    public void dispose() {
        for (Texture tex : texturasNormales) {
            if (tex != null) {
                tex.dispose();
            }
        }
        for (int d = 0; d < 4; d++) {
            for (Texture tex : texturasEmpuje[d]) {
                if (tex != null) {
                    tex.dispose();
                }
            }
            for (Texture tex : texturasMovimiento[d]) {
                if (tex != null) {
                    tex.dispose();
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean estaMoviendose() {
        return estaMoviendose.get();
    }

    public boolean estaEmpujando() {
        return estaEmpujando.get();
    }
}
