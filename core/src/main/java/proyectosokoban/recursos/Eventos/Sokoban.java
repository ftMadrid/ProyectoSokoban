package proyectosokoban.recursos.Eventos;

import proyectosokoban.recursos.Eventos.Juego;
import proyectosokoban.recursos.Main;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sokoban extends Juego {

    private Nivel nivelActual;
    private Jugador jugador;
    private Music musicafondo;
    private Sound audiomove;
    private Sound sonidoVictoria;

    // Hilos para manejo de eventos en tiempo real
    private ExecutorService collisionDetector;
    private ExecutorService animationUpdater;
    private final AtomicBoolean gameRunning = new AtomicBoolean(true);

    // Flags para comunicación entre hilos
    private final AtomicBoolean needsVictoryCheck = new AtomicBoolean(false);
    private final AtomicBoolean playMoveSound = new AtomicBoolean(false);
    private final AtomicBoolean juegoGanado = new AtomicBoolean(false);

    private volatile int movimientos = 0;

    public Sokoban(final Main main) {
        super(main);
    }

    @Override
    public void inicializarRecursos() {
        // Inicialización específica de Sokoban
        nivelActual = new Nivel(1); // Nivel por defecto
        jugador = new Jugador(nivelActual.getPosicionJugadorX(), nivelActual.getPosicionJugadorY());

        // Cargar sonidos
        musicafondo = com.badlogic.gdx.Gdx.audio.newMusic(com.badlogic.gdx.Gdx.files.internal("audiofondo.mp3"));
        musicafondo.setLooping(true);
        musicafondo.setVolume(0.3f);
        musicafondo.play();

        audiomove = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("movimiento.mp3"));

        try {
            sonidoVictoria = com.badlogic.gdx.Gdx.audio.newSound(com.badlogic.gdx.Gdx.files.internal("audiovictoria.mp3"));
        } catch (Exception e) {
            sonidoVictoria = null;
        }

        initializeThreads();
    }

    private void initializeThreads() {
        // Hilo para detección de colisiones en tiempo real
        collisionDetector = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "CollisionDetector");
            t.setDaemon(true);
            return t;
        });

        // Hilo para actualización de animaciones
        animationUpdater = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "AnimationUpdater");
            t.setDaemon(true);
            return t;
        });

        startCollisionDetectionThread();
        startAnimationUpdateThread();
    }

    private void startCollisionDetectionThread() {
        collisionDetector.submit(() -> {
            while (gameRunning.get()) {
                try {
                    // Verificar victoria continuamente
                    if (!juegoGanado.get() && nivelActual.verificarVictoria()) {
                        needsVictoryCheck.set(true);
                    }

                    Thread.sleep(50); // Verificar cada 50ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error en CollisionDetector: " + e.getMessage());
                }
            }
        });
    }

    private void startAnimationUpdateThread() {
        animationUpdater.submit(() -> {
            long lastTime = System.nanoTime();
            while (gameRunning.get()) {
                try {
                    if (jugador.estaMoviendose() || jugador.estaEmpujando()) {
                        long currentTime = System.nanoTime();
                        float delta = (currentTime - lastTime) / 1000000000f;
                        lastTime = currentTime;

                        jugador.actualizarAnimacion(delta);
                        nivelActual.actualizarAnimacionCajas(delta);
                    } else {
                        lastTime = System.nanoTime(); // Reset cuando no se está moviendo
                    }

                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error en AnimationUpdater: " + e.getMessage());
                }
            }
        });
    }

    public void moverJugador(int dx, int dy) {
        if (juegoGanado.get() || jugador.estaMoviendose()) {
            return;
        }

        if (jugador.mover(dx, dy, nivelActual)) {
            movimientos++;
            playMoveSound.set(true);
        }
    }

    public void verificarVictoria() {
        if (juegoGanado.getAndSet(true)) {
            return; // Ya se procesó
        }

        if (sonidoVictoria != null) {
            sonidoVictoria.play(0.3f);
        }
        musicafondo.pause();
    }

    @Override
    public void actualizar(float delta) {
        // Verificar flags de los hilos secundarios
        if (needsVictoryCheck.getAndSet(false)) {
            verificarVictoria();
        }

        if (playMoveSound.getAndSet(false)) {
            audiomove.play(0.6f);
        }

        jugador.actualizar(delta);
        nivelActual.actualizar(delta);
    }

    @Override
    public void renderizar() {
        // Renderizado específico de Sokoban
        nivelActual.render();
        jugador.render();
    }

    public int getMovimientos() {
        return movimientos;
    }

    public boolean isJuegoGanado() {
        return juegoGanado.get();
    }

    // Implementación de los métodos de Screen que faltaban
    @Override
    public void render(float delta) {
        actualizar(delta);
        renderizar();
    }

    @Override
    public void show() {
        // Lógica cuando se muestra la pantalla
    }

    @Override
    public void resize(int width, int height) {
        // Lógica para redimensionamiento
    }

    @Override
    public void pause() {
        // Lógica cuando se pausa el juego
    }

    @Override
    public void resume() {
        // Lógica cuando se reanuda el juego
    }

    @Override
    public void hide() {
        // Lógica cuando se oculta la pantalla
    }

    @Override
    public void dispose() {
        gameRunning.set(false);

        if (collisionDetector != null && !collisionDetector.isShutdown()) {
            collisionDetector.shutdown();
        }
        if (animationUpdater != null && !animationUpdater.isShutdown()) {
            animationUpdater.shutdown();
        }

        // Liberar recursos específicos de Sokoban
        if (jugador != null) {
            jugador.dispose();
        }
        if (nivelActual != null) {
            nivelActual.dispose();
        }

        if (musicafondo != null) {
            musicafondo.dispose();
        }
        if (audiomove != null) {
            audiomove.dispose();
        }
        if (sonidoVictoria != null) {
            sonidoVictoria.dispose();
        }
    }
}
