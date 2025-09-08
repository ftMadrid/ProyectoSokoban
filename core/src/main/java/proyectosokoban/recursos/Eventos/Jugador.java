package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import proyectosokoban.recursos.Eventos.Nivel;
import java.util.concurrent.atomic.AtomicBoolean;

public class Jugador {

    private Texture[] texturasNormales; // Arriba, abajo, izquierda, derecha
    private Texture[][] texturasEmpuje; // Animaciones de empuje [direccion][frame]
    private Texture[][] texturasMovimiento; // Animaciones de movimiento normal [direccion][frame]

    private volatile int x, y;
    private volatile float renderX, renderY;
    private volatile int direccion; // 0=abajo, 1=arriba, 2=izquierda, 3=derecha

    private final AtomicBoolean estaMoviendose = new AtomicBoolean(false);
    private final AtomicBoolean estaEmpujando = new AtomicBoolean(false);

    // Duraciones adaptadas
    private volatile float tiempoAnimacion = 0f;
    private final float duracionAnimacion = 0.8f; // Duración total de un movimiento (más lenta que antes)

// Animación de movimiento normal
    private volatile float tiempoAnimacionMovimiento = 0f;
    private final float duracionFrameMovimiento = duracionAnimacion / 4f; // 4 frames por movimiento
    private volatile int frameActualMovimiento = 0;

// Animación de empuje
    private volatile float tiempoAnimacionEmpuje = 0f;
    private final int FRAMES_EMPUEJE = 6;
    private volatile int frameActualEmpuje = 0;
    private final float duracionFrameEmpuje = duracionAnimacion / FRAMES_EMPUEJE; // Sincronizado con el movimiento

    // Posiciones de inicio y destino
    private volatile float startX, startY, targetX, targetY;

    private final int TILE = 90;

    public Jugador(int x, int y) {
        this.x = x;
        this.y = y;
        this.renderX = x * TILE;
        this.renderY = y * TILE;
        this.direccion = 0; // Mirando hacia abajo por defecto
        cargarTexturas();
    }

    private void cargarTexturas() {
        // Texturas normales
        texturasNormales = new Texture[4];
        texturasNormales[0] = new Texture("muneco/south.png"); // Abajo
        texturasNormales[1] = new Texture("muneco/north.png"); // Arriba
        texturasNormales[2] = new Texture("muneco/west.png");  // Izquierda
        texturasNormales[3] = new Texture("muneco/east.png");  // Derecha

        // Animación de empuje (4 direcciones × 6 frames)
        texturasEmpuje = new Texture[4][6];
        for (int i = 0; i < 6; i++) {
            texturasEmpuje[0][i] = new Texture("muneco/caja/south_00" + i + ".png");
            texturasEmpuje[1][i] = new Texture("muneco/caja/north_00" + i + ".png");
            texturasEmpuje[2][i] = new Texture("muneco/caja/west_00" + i + ".png");
            texturasEmpuje[3][i] = new Texture("muneco/caja/east_00" + i + ".png");
        }

        // Animación de movimiento normal (4 direcciones × 4 frames)
        texturasMovimiento = new Texture[4][4];
        for (int i = 0; i < 4; i++) {
            texturasMovimiento[0][i] = new Texture("muneco/moves/south_00" + i + ".png");
            texturasMovimiento[1][i] = new Texture("muneco/moves/north_00" + i + ".png");
            texturasMovimiento[2][i] = new Texture("muneco/moves/west_00" + i + ".png");
            texturasMovimiento[3][i] = new Texture("muneco/moves/east_00" + i + ".png");
        }
    }

    public boolean mover(int dx, int dy, Nivel nivel) {
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
            // Movimiento normal
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
        actualizarAnimacion(delta);
    }

    public void actualizarAnimacion(float delta) {
        // Animación movimiento
        if (estaMoviendose.get()) {
            tiempoAnimacion += delta;
            float progreso = Math.min(tiempoAnimacion / duracionAnimacion, 1.0f);
            progreso = 1f - (1f - progreso) * (1f - progreso);
            renderX = com.badlogic.gdx.math.MathUtils.lerp(startX, targetX, progreso);
            renderY = com.badlogic.gdx.math.MathUtils.lerp(startY, targetY, progreso);

            // Animación de frames normales si no empuja
            if (!estaEmpujando.get()) {
                tiempoAnimacionMovimiento += delta;
                if (tiempoAnimacionMovimiento >= duracionFrameMovimiento) {
                    frameActualMovimiento = (frameActualMovimiento + 1) % texturasMovimiento[direccion].length;
                    tiempoAnimacionMovimiento = 0f;
                }
            }

            // Fin de movimiento
            if (progreso >= 1.0f) {
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
            if (tiempoAnimacionEmpuje >= duracionFrameEmpuje) {
                frameActualEmpuje = (frameActualEmpuje + 1) % texturasEmpuje[direccion].length;
                tiempoAnimacionEmpuje = 0f;
            }
        }
    }

    private Texture getTexturaActual() {
        if (estaEmpujando.get()) {
            return texturasEmpuje[direccion][frameActualEmpuje];
        } else if (estaMoviendose.get()) {
            return texturasMovimiento[direccion][frameActualMovimiento];
        }
        return texturasNormales[direccion];
    }

    public void render(SpriteBatch batch) {
        int jugadorWidth = 140;
        int jugadorHeight = 140;
        float offsetX = (TILE - jugadorWidth) / 2f;
        float offsetY = (TILE - jugadorHeight) / 2f;
        float jugadorPosX = renderX + offsetX;
        float jugadorPosY = renderY + offsetY;

        batch.draw(getTexturaActual(), jugadorPosX, jugadorPosY, jugadorWidth, jugadorHeight);
    }

    public void dispose() {
        for (Texture tex : texturasNormales) {
            if (tex != null) {
                tex.dispose();
            }
        }
        for (int dir = 0; dir < 4; dir++) {
            for (Texture tex : texturasEmpuje[dir]) {
                if (tex != null) {
                    tex.dispose();
                }
            }
            for (Texture tex : texturasMovimiento[dir]) {
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
