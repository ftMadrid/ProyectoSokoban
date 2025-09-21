package proyectosokoban.recursos.Eventos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import proyectosokoban.recursos.Main;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sokoban extends Juego {

    private Nivel nivelActual;
    private Jugador jugador;

    public Sound audiomove;
    public Sound sonidoVictoria;
    private SpriteBatch batch;
    private int nivelNumero;
    private String username;

    public float soundVolume = 1.0f;
    public boolean isMuted = false;

    private ExecutorService collisionDetector;
    private ExecutorService animationUpdater;
    private final AtomicBoolean gameRunning = new AtomicBoolean(true);

    private final AtomicBoolean needsVictoryCheck = new AtomicBoolean(false);
    private final AtomicBoolean playMoveSound = new AtomicBoolean(false);
    private final AtomicBoolean juegoGanado = new AtomicBoolean(false);

    private volatile int movimientos = 0;
    private volatile int empujes = 0;

    private Viewport viewport;
    private OrthographicCamera camera;

    public Sokoban(final Main main, int nivel, String username) {
        super(main);
        this.nivelNumero = nivel;
        this.username = username;
    }
    
    public Nivel getNivelActual(){
        return nivelActual;
    }

    @Override
    public void inicializarRecursos() {
        nivelActual = new Nivel(nivelNumero);
        jugador = new Jugador(nivelActual.getSpawnJugadorX(), nivelActual.getSpawnJugadorY(), nivelActual.getTILE());
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        float worldWidth = nivelActual.getCOLUMNAS() * nivelActual.getTILE();
        float worldHeight = nivelActual.getFILAS() * nivelActual.getTILE();
        viewport = new ExtendViewport(worldWidth, worldHeight, camera);
        
        audiomove = Gdx.audio.newSound(Gdx.files.internal("Juego/audios/movimiento.mp3"));

        try {
            sonidoVictoria = Gdx.audio.newSound(Gdx.files.internal("Juego/audios/audiovictoria.mp3"));
        } catch (Exception e) {
            sonidoVictoria = null;
        }

        initializeThreads();
    }

    private void initializeThreads() {
        collisionDetector = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "CollisionDetector");
            t.setDaemon(true);
            return t;
        });
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
                    if (!juegoGanado.get() && nivelActual.verificarVictoria()) {
                        needsVictoryCheck.set(true);
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
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
                        jugador.actualizar(delta);
                        nivelActual.actualizarAnimacionCajas(delta);
                    } else {
                        lastTime = System.nanoTime();
                    }
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public void moverJugador(int dx, int dy) {
        if (juegoGanado.get() || jugador.estaMoviendose()) {
            return;
        }
        if (jugador.mover(dx, dy, nivelActual, nivelActual.getTILE())) {
            movimientos++;
            if (jugador.estaEmpujando()) {
                empujes++;
            }
            playMoveSound.set(true);
        }
    }

    public void verificarVictoria() {
        if (juegoGanado.getAndSet(true)) {
            return;
        }
        main.stopAllMusic();
        if (sonidoVictoria != null) {
            sonidoVictoria.play(soundVolume);
        }
    }

    @Override
    public void actualizar(float delta) {
        if (needsVictoryCheck.getAndSet(false)) {
            verificarVictoria();
        }
        if (playMoveSound.getAndSet(false)) {
            if (audiomove != null && !isMuted) {
                audiomove.play((soundVolume));
            }
        }
        jugador.actualizar(delta);
        nivelActual.actualizar(delta);
    }

    @Override
    public void renderizar() {
        viewport.apply(true);
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        nivelActual.render(batch);
        jugador.render(batch, nivelActual.getTILE());
        batch.end();
    }

    @Override
    public void render(float delta) {
        actualizar(delta);
        renderizar();
    }

    @Override
    public void dispose() {
        gameRunning.set(false);
        if (collisionDetector != null) {
            collisionDetector.shutdown();
        }
        if (animationUpdater != null) {
            animationUpdater.shutdown();
        }
        if (jugador != null) {
            jugador.dispose();
        }
        if (nivelActual != null) {
            nivelActual.dispose();
        }
        if (audiomove != null) {
            audiomove.dispose();
        }
        if (sonidoVictoria != null) {
            sonidoVictoria.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }

    public int getMovimientos() {
        return movimientos;
    }

    public int getEmpujes() {
        return empujes;
    }

    public int getNivelNumero() {
        return nivelNumero;
    }

    public String getUsername() {
        return username;
    }

    public boolean isJuegoGanado() {
        return juegoGanado.get();
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
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