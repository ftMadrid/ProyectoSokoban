package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

import java.util.List;

public class RankingScreen implements Screen {

    private final Main main;
    private Stage stage;
    private LogicaUsuarios userLogic;
    private GestorIdiomas gestorIdiomas;
    private Texture backgroundTexture;
    private BitmapFont font, titleFont;
    
    private Table rankingContentTable;
    private Table levelSelectorTable;

    // Estados para controlar la vista actual
    private enum RankingType { TOTAL, LEVEL_GLOBAL, LEVEL_FRIENDS }
    private RankingType currentRankingType = RankingType.TOTAL;
    private boolean isGlobalTotal = true; // Para el ranking total
    private int selectedLevel = 1;

    public RankingScreen(final Main main) {
        this.main = main;
        this.stage = new Stage(new ScreenViewport());
        this.userLogic = new LogicaUsuarios();
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();
        this.backgroundTexture = new Texture(Gdx.files.internal("background2.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 32;
        p.color = Color.WHITE;
        font = generator.generateFont(p);
        p.size = 72;
        titleFont = generator.generateFont(p);
        generator.dispose();

        createUI();
        // Cargar ranking total global por defecto
        populateRankingTable();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        stage.addActor(mainTable);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label titleLabel = new Label(t("ranking.title", "Ranking"), titleStyle);
        mainTable.add(titleLabel).padBottom(20).row();

        // Botones para cambiar el tipo de ranking
        Table viewToggleTable = new Table();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        TextButton totalScoreButton = new TextButton(t("ranking.total_score", "Puntuación Total"), buttonStyle);
        totalScoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentRankingType = RankingType.TOTAL;
                levelSelectorTable.setVisible(false);
                populateRankingTable();
            }
        });

        TextButton levelGlobalButton = new TextButton(t("ranking.level_global", "Nivel (Global)"), buttonStyle);
        levelGlobalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentRankingType = RankingType.LEVEL_GLOBAL;
                levelSelectorTable.setVisible(true);
                populateRankingTable();
            }
        });
        
        TextButton levelFriendsButton = new TextButton(t("ranking.level_friends", "Nivel (Amigos)"), buttonStyle);
        levelFriendsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentRankingType = RankingType.LEVEL_FRIENDS;
                levelSelectorTable.setVisible(true);
                populateRankingTable();
            }
        });

        viewToggleTable.add(totalScoreButton).width(350).height(60).pad(10);
        viewToggleTable.add(levelGlobalButton).width(350).height(60).pad(10);
        viewToggleTable.add(levelFriendsButton).width(350).height(60).pad(10);
        mainTable.add(viewToggleTable).padBottom(10).row();

        // Selector de nivel (inicialmente oculto)
        levelSelectorTable = new Table();
        levelSelectorTable.setVisible(false);
        populateLevelSelector();
        mainTable.add(levelSelectorTable).padBottom(10).row();

        // Tabla de contenido del ranking
        rankingContentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(rankingContentTable);
        mainTable.add(scrollPane).expand().fill().padBottom(20).row();

        // Botón de Volver
        TextButton backButton = new TextButton(t("back.button", "Volver"), buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main));
            }
        });
        mainTable.add(backButton).width(300).height(60).row();
    }

    private void populateLevelSelector() {
        levelSelectorTable.clear();
        TextButton.TextButtonStyle levelButtonStyle = new TextButton.TextButtonStyle();
        levelButtonStyle.font = font;
        levelButtonStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/botonespeq.png")));

        for (int i = 1; i <= 7; i++) { // Asumiendo que hay 7 niveles
            final int level = i;
            TextButton levelButton = new TextButton(String.valueOf(level), levelButtonStyle);
            levelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedLevel = level;
                    populateRankingTable();
                }
            });
            levelSelectorTable.add(levelButton).size(60, 60).pad(5);
        }
    }

    private void populateRankingTable() {
        rankingContentTable.clear();
        Label.LabelStyle headerStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle rowStyle = new Label.LabelStyle(font, Color.LIGHT_GRAY);

        // Cabeceras
        rankingContentTable.add(new Label(t("ranking.header.rank", "#"), headerStyle)).pad(10).width(100);
        rankingContentTable.add(new Label(t("ranking.header.user", "Usuario"), headerStyle)).pad(10).expandX();
        rankingContentTable.add(new Label(t("ranking.header.score", "Puntuación"), headerStyle)).pad(10).width(300).row();

        List<LogicaUsuarios.RankingEntry> rankingList = null;
        
        switch (currentRankingType) {
            case TOTAL:
                // Para el total, alternamos entre global y amigos con un botón (o podrías tener dos botones separados)
                // Por simplicidad, lo basaremos en una variable booleana, como en la versión anterior.
                rankingList = isGlobalTotal ? userLogic.getRankingGlobal() : userLogic.getRankingAmigos(main.username);
                break;
            case LEVEL_GLOBAL:
                rankingList = userLogic.getRankingGlobalPorNivel(selectedLevel);
                break;
            case LEVEL_FRIENDS:
                rankingList = userLogic.getRankingAmigosPorNivel(main.username, selectedLevel);
                break;
        }

        if (rankingList == null || rankingList.isEmpty()) {
            rankingContentTable.add(new Label(t("ranking.empty", "No hay datos disponibles"), rowStyle)).colspan(3).pad(20);
        } else {
            int rank = 1;
            for (LogicaUsuarios.RankingEntry entry : rankingList) {
                rankingContentTable.add(new Label(String.valueOf(rank++), rowStyle)).pad(5);
                rankingContentTable.add(new Label(entry.username, rowStyle)).pad(5);
                rankingContentTable.add(new Label(String.valueOf(entry.totalScore), rowStyle)).pad(5).row();
            }
        }
    }

    private String t(String key, String fallback) {
        String s = gestorIdiomas.setTexto(key);
        return (s == null || s.startsWith("[")) ? fallback : s;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        transicionSuave.fadeIn(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        font.dispose();
        titleFont.dispose();
    }
}