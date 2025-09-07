package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import proyectosokoban.recursos.Eventos.Nivel;
import java.util.concurrent.atomic.AtomicBoolean;

public class Jugador {

    private Texture[] texturasNormales; // Arriba, abajo, izquierda, derecha
    private Texture[] texturasEmpuje; // Animación de empuje

    private volatile int x, y;
    private volatile float renderX, renderY;
    private volatile int direccion; // 0=abajo, 1=arriba, 2=izquierda, 3=derecha

    private final AtomicBoolean estaMoviendose = new AtomicBoolean(false);
    private final AtomicBoolean estaEmpujando = new AtomicBoolean(false);

    // Variables para la animación
    private volatile float tiempoAnimacion = 0f;
    private final float duracionAnimacion = 0.3f;

    // Variables específicas para la animación del jugador empujando
    private volatile float tiempoAnimacionEmpuje = 0f;
    private final float duracionFrameEmpuje = 0.05f; // Duración de cada frame (50ms)
    private volatile int frameActualEmpuje = 0;

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
        // Cargar texturas del jugador
        texturasNormales = new Texture[4];
        texturasNormales[0] = new Texture("muneco/south.png"); // Abajo
        texturasNormales[1] = new Texture("muneco/north.png"); // Arriba
        texturasNormales[2] = new Texture("muneco/west.png");  // Izquierda
        texturasNormales[3] = new Texture("muneco/east.png");  // Derecha

        // Cargar texturas de animación de empuje
        texturasEmpuje = new Texture[6];
        texturasEmpuje[0] = new Texture("caja/frame_000.png");
        texturasEmpuje[1] = new Texture("caja/frame_001.png");
        texturasEmpuje[2] = new Texture("caja/frame_002.png");
        texturasEmpuje[3] = new Texture("caja/frame_003.png");
        texturasEmpuje[4] = new Texture("caja/frame_004.png");
        texturasEmpuje[5] = new Texture("caja/frame_005.png");
    }

    public boolean mover(int dx, int dy, Nivel nivel) {
        // Actualizar dirección del sprite según el movimiento
        if (dx > 0) {
            direccion = 3; // Derecha
        } else if (dx < 0) {
            direccion = 2; // Izquierda  
        } else if (dy > 0) {
            direccion = 1; // Arriba
        } else if (dy < 0) {
            direccion = 0; // Abajo
        }

        int nuevoX = x + dx;
        int nuevoY = y + dy;

        if (nivel.esPared(nuevoX, nuevoY)) {
            return false;
        }

        // Configurar posiciones de inicio
        startX = renderX;
        startY = renderY;

        // Verificar si va a empujar una caja
        if (nivel.hayCajaEn(nuevoX, nuevoY)) {
            int nuevoCajaX = nuevoX + dx;
            int nuevoCajaY = nuevoY + dy;

            if (!nivel.esPared(nuevoCajaX, nuevoCajaY) && !nivel.hayCajaEn(nuevoCajaX, nuevoCajaY)) {
                nivel.moverCaja(nuevoX, nuevoY, nuevoCajaX, nuevoCajaY);
                x = nuevoX;
                y = nuevoY;

                targetX = x * TILE;
                targetY = y * TILE;

                // Iniciar animación de empuje del jugador
                estaEmpujando.set(true);
                frameActualEmpuje = 0;
                tiempoAnimacionEmpuje = 0f;

                estaMoviendose.set(true);
                tiempoAnimacion = 0f;

                return true;
            }
            return false;
        } else {
            // Movimiento normal (sin empujar caja)
            x = nuevoX;
            y = nuevoY;

            targetX = x * TILE;
            targetY = y * TILE;

            // No iniciar animación de empuje
            estaEmpujando.set(false);

            estaMoviendose.set(true);
            tiempoAnimacion = 0f;

            return true;
        }
    }

    public void actualizar(float delta) {
        // La lógica de animación se maneja en actualizarAnimacion
    }

    public void actualizarAnimacion(float delta) {
        // Actualizar animación de movimiento
        if (estaMoviendose.get()) {
            tiempoAnimacion += delta;

            float progreso = Math.min(tiempoAnimacion / duracionAnimacion, 1.0f);
            progreso = 1f - (1f - progreso) * (1f - progreso);

            // Actualizar posiciones de renderizado
            renderX = com.badlogic.gdx.math.MathUtils.lerp(startX, targetX, progreso);
            renderY = com.badlogic.gdx.math.MathUtils.lerp(startY, targetY, progreso);

            // Verificar si la animación terminó
            if (progreso >= 1.0f) {
                estaMoviendose.set(false);
                renderX = targetX;
                renderY = targetY;

                // Detener la animación de empuje cuando termina el movimiento
                estaEmpujando.set(false);
                frameActualEmpuje = 0;
                tiempoAnimacionEmpuje = 0f;
            }
        }

        // Actualizar animación de frames del jugador empujando
        if (estaEmpujando.get()) {
            tiempoAnimacionEmpuje += delta;

            if (tiempoAnimacionEmpuje >= duracionFrameEmpuje) {
                frameActualEmpuje = (frameActualEmpuje + 1) % texturasEmpuje.length;
                tiempoAnimacionEmpuje = 0f;
            }
        }
    }

    private Texture getTexturaActual() {
        // Si está empujando, usar la animación de empuje
        if (estaEmpujando.get()) {
            return texturasEmpuje[frameActualEmpuje];
        }

        // Si no está empujando, usar las texturas direccionales normales
        return texturasNormales[direccion];
    }

    public void render() {
        SpriteBatch batch = new SpriteBatch();
        batch.begin();

        int jugadorWidth = 140;
        int jugadorHeight = 140;
        float offsetX = (TILE - jugadorWidth) / 2f;
        float offsetY = (TILE - jugadorHeight) / 2f;

        float jugadorPosX = renderX + offsetX;
        float jugadorPosY = renderY + offsetY;

        Texture texturaJugadorActual = getTexturaActual();
        batch.draw(texturaJugadorActual, jugadorPosX, jugadorPosY, jugadorWidth, jugadorHeight);

        batch.end();
    }

    public void dispose() {
        // Liberar texturas
        for (Texture tex : texturasNormales) {
            if (tex != null) {
                tex.dispose();
            }
        }
        for (Texture tex : texturasEmpuje) {
            if (tex != null) {
                tex.dispose();
            }
        }
    }

    // Getters y setters
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
