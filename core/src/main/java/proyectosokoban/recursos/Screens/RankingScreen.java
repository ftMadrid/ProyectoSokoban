// Ruta: core/src/main/java/proyectosokoban/recursos/Screens/RankingScreen.java
package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
    private BitmapFont font, titleFont, smallFont;

    private Table rankingContentTable;
    private Table levelSelectorTable;
    private Table panelTable;

    private enum RankingType { TOTAL, LEVEL }
    private enum RankingScope { GLOBAL, FRIENDS }

    private RankingType currentRankingType = RankingType.TOTAL;
    private RankingScope currentRankingScope = RankingScope.GLOBAL;
    private int selectedLevel = 1;

    public RankingScreen(final Main main) {
        this.main = main;
        this.stage = new Stage(new ScreenViewport());
        this.userLogic = new LogicaUsuarios();
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();
        this.backgroundTexture = new Texture(Gdx.files.internal("background2.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size = 24;
        p.color = Color.valueOf("1E1E1E");
        smallFont = generator.generateFont(p);

        p.size = 28;
        font = generator.generateFont(p);

        p.size = 48;
        titleFont = generator.generateFont(p);
        generator.dispose();

        createUI();
        populateRankingTable();
    }

    private TextureRegionDrawable solid(float r, float g, float b, float a) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(r, g, b, a);
        pm.fill();
        TextureRegionDrawable dr = new TextureRegionDrawable(new TextureRegion(new Texture(pm)));
        pm.dispose();
        return dr;
    }

    private void createUI() {
        // Tabla raíz que ocupa toda la pantalla
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Panel principal con un tamaño proporcional a la pantalla
        panelTable = new Table();
        panelTable.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        panelTable.pad(20, 40, 20, 40);

        float panelWidth = Gdx.graphics.getWidth() * 0.9f;
        float panelHeight = Gdx.graphics.getHeight() * 0.9f;
        
        rootTable.add(panelTable).width(panelWidth).height(panelHeight);

        // --- Título ---
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.valueOf("1E1E1E"));
        Label titleLabel = new Label(gestorIdiomas.setTexto("ranking.title"), titleStyle);
        panelTable.add(titleLabel).expandX().center().padTop(60).padBottom(15).row();

        Table filtersTable = new Table();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = smallFont;
        buttonStyle.fontColor = Color.valueOf("1E1E1E");
        buttonStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        Label.LabelStyle sectionStyle = new Label.LabelStyle(smallFont, Color.valueOf("1E1E1E"));
        filtersTable.add(new Label(gestorIdiomas.setTexto("ranking.view"), sectionStyle)).padRight(10);

        TextButton globalButton = new TextButton(gestorIdiomas.setTexto("ranking.global"), buttonStyle);
        globalButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingScope = RankingScope.GLOBAL; populateRankingTable(); }});

        TextButton friendsButton = new TextButton(gestorIdiomas.setTexto("ranking.friends"), buttonStyle);
        friendsButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingScope = RankingScope.FRIENDS; populateRankingTable(); }});

        filtersTable.add(globalButton).height(45).padRight(5);
        filtersTable.add(friendsButton).height(45).padRight(30);

        filtersTable.add(new Label(gestorIdiomas.setTexto("ranking.type"), sectionStyle)).padRight(10);
        TextButton totalButton = new TextButton(gestorIdiomas.setTexto("ranking.total"), buttonStyle);
        totalButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingType = RankingType.TOTAL; levelSelectorTable.setVisible(false); populateRankingTable(); }});

        TextButton byLevelButton = new TextButton(gestorIdiomas.setTexto("ranking.by_level"), buttonStyle);
        byLevelButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingType = RankingType.LEVEL; levelSelectorTable.setVisible(true); populateRankingTable(); }});

        filtersTable.add(totalButton).height(45).padRight(5);
        filtersTable.add(byLevelButton).height(45);
        panelTable.add(filtersTable).center().padBottom(10).row();
        
        // --- Selector de Nivel ---
        levelSelectorTable = new Table();
        levelSelectorTable.setVisible(false);
        populateLevelSelector();
        panelTable.add(levelSelectorTable).center().padBottom(10).row();

        // --- Contenido del Ranking (con Scroll) ---
        rankingContentTable = new Table();
        rankingContentTable.top();
        ScrollPane scrollPane = new ScrollPane(rankingContentTable, new ScrollPane.ScrollPaneStyle());
        scrollPane.setFadeScrollBars(false);
        panelTable.add(scrollPane).expand().fill().padBottom(15).row();

        // --- Botón de Volver ---
        TextButton backButton = new TextButton(gestorIdiomas.setTexto("back.button"), buttonStyle);
        backButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main)); }});
        panelTable.add(backButton).width(200).height(50).center().padTop(10);
    }

    private void populateLevelSelector() {
        levelSelectorTable.clear();
        TextButton.TextButtonStyle levelButtonStyle = new TextButton.TextButtonStyle();
        levelButtonStyle.font = smallFont;
        levelButtonStyle.fontColor = Color.valueOf("1E1E1E");
        levelButtonStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/botonespeq.png")));

        for (int i = 1; i <= 7; i++) {
            final int level = i;
            TextButton levelButton = new TextButton(String.valueOf(level), levelButtonStyle);
            levelButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { selectedLevel = level; populateRankingTable(); }});
            levelSelectorTable.add(levelButton).size(45, 45).pad(3);
        }
    }

    private void populateRankingTable() {
        rankingContentTable.clear();
        Label.LabelStyle headerStyle = new Label.LabelStyle(font, Color.valueOf("1E1E1E"));
        Label.LabelStyle rowStyle = new Label.LabelStyle(smallFont, Color.valueOf("2F2F2F"));

        // Calcular el ancho disponible dentro del panel (restando padding)
        float availableWidth = panelTable.getWidth() - 80; // 40 padding left + 40 padding right
        if (availableWidth <= 0) {
            availableWidth = Gdx.graphics.getWidth() * 0.9f - 80;
        }
        
        // Distribución de anchos: 20% para rank, 50% para usuario, 30% para score
        float rankWidth = availableWidth * 0.2f;
        float userWidth = availableWidth * 0.5f;
        float scoreWidth = availableWidth * 0.3f;

        // --- Cabeceras ---
        Table headerTable = new Table();
        Label rankHeader = new Label(gestorIdiomas.setTexto("ranking.header.rank"), headerStyle);
        rankHeader.setAlignment(Align.center);
        headerTable.add(rankHeader).width(rankWidth).center();
        
        Label userHeader = new Label(gestorIdiomas.setTexto("ranking.header.user"), headerStyle);
        userHeader.setAlignment(Align.center);
        headerTable.add(userHeader).width(userWidth).center();
        
        Label scoreHeader = new Label(gestorIdiomas.setTexto("ranking.header.score"), headerStyle);
        scoreHeader.setAlignment(Align.center);
        headerTable.add(scoreHeader).width(scoreWidth).center();
        
        rankingContentTable.add(headerTable).width(availableWidth).center().padBottom(10).row();

        List<LogicaUsuarios.RankingEntry> rankingList;
        if (currentRankingType == RankingType.TOTAL) {
            rankingList = (currentRankingScope == RankingScope.GLOBAL) ? userLogic.getRankingGlobal() : userLogic.getRankingAmigos(main.username);
        } else {
            rankingList = (currentRankingScope == RankingScope.GLOBAL) ? userLogic.getRankingGlobalPorNivel(selectedLevel) : userLogic.getRankingAmigosPorNivel(main.username, selectedLevel);
        }

        Drawable rowBackground = solid(0, 0, 0, 0.08f);

        if (rankingList == null || rankingList.isEmpty()) {
            Label emptyLabel = new Label(gestorIdiomas.setTexto("ranking.empty"), rowStyle);
            emptyLabel.setAlignment(Align.center);
            rankingContentTable.add(emptyLabel).width(availableWidth).center().pad(20).row();
        } else {
            int rank = 1;
            for (LogicaUsuarios.RankingEntry entry : rankingList) {
                Table rowContent = new Table();
                if (rank % 2 == 0) {
                    rowContent.setBackground(rowBackground);
                }
                
                Label rankLabel = new Label(String.valueOf(rank), rowStyle);
                rankLabel.setAlignment(Align.center);
                rowContent.add(rankLabel).width(rankWidth).center();
                
                Label userLabel = new Label(entry.username, rowStyle);
                userLabel.setAlignment(Align.center);
                rowContent.add(userLabel).width(userWidth).center();
                
                Label scoreLabel = new Label(String.valueOf(entry.totalScore), rowStyle);
                scoreLabel.setAlignment(Align.center);
                rowContent.add(scoreLabel).width(scoreWidth).center();

                rankingContentTable.add(rowContent).width(availableWidth).center().padTop(5).padBottom(5).row();
                rank++;
            }
        }
    }
    
    // Métodos restantes (show, render, resize, etc.) sin cambios...
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        transicionSuave.fadeIn(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
        // Volvemos a construir la UI para que se ajuste al nuevo tamaño
        createUI();
        populateRankingTable();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        font.dispose();
        titleFont.dispose();
        smallFont.dispose();
    }
}