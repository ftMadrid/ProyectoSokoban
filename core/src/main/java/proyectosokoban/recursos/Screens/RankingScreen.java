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
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        outerTable.center();
        stage.addActor(outerTable);

        Table panelTable = new Table();
        panelTable.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));

        float panelWidth = Gdx.graphics.getWidth() * 0.85f;
        float panelHeight = Gdx.graphics.getHeight() * 0.9f;

        outerTable.add(panelTable).size(panelWidth, panelHeight);
        panelTable.pad(20, 40, 20, 40);

        Table titleBand = new Table();
        titleBand.setBackground(solid(0, 0, 0, 0.08f));
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.valueOf("1E1E1E"));
        Label titleLabel = new Label(gestorIdiomas.setTexto("ranking.title"), titleStyle);
        titleBand.add(titleLabel).pad(10);
        panelTable.add(titleBand).growX().padTop(25).padBottom(15).row();

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

        filtersTable.add(globalButton).size(120, 50).padRight(5);
        filtersTable.add(friendsButton).size(120, 50).padRight(30);

        filtersTable.add(new Label(gestorIdiomas.setTexto("ranking.type"), sectionStyle)).padRight(10);
        TextButton totalButton = new TextButton(gestorIdiomas.setTexto("ranking.total"), buttonStyle);
        totalButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingType = RankingType.TOTAL; levelSelectorTable.setVisible(false); populateRankingTable(); }});

        TextButton byLevelButton = new TextButton(gestorIdiomas.setTexto("ranking.by_level"), buttonStyle);
        byLevelButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingType = RankingType.LEVEL; levelSelectorTable.setVisible(true); populateRankingTable(); }});

        filtersTable.add(totalButton).size(120, 50).padRight(5);
        filtersTable.add(byLevelButton).size(120, 50);
        panelTable.add(filtersTable).center().padBottom(10).row();

        levelSelectorTable = new Table();
        levelSelectorTable.setVisible(false);
        populateLevelSelector();
        panelTable.add(levelSelectorTable).center().padBottom(10).row();

        rankingContentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(rankingContentTable);
        scrollPane.setFadeScrollBars(false);
        panelTable.add(scrollPane).expand().fill().padBottom(15).row();

        TextButton backButton = new TextButton(gestorIdiomas.setTexto("back.button"), buttonStyle);
        backButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main)); }});
        panelTable.add(backButton).size(200, 50).center().padTop(10);
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

        // === CABECERAS DE LA TABLA (CON PADDING) ===
        Table headerTable = new Table();
        // Se añade padLeft y padRight para que el texto no toque los bordes del panel.
        headerTable.add(new Label(gestorIdiomas.setTexto("ranking.header.rank"), headerStyle)).width(150).left().padLeft(15);
        headerTable.add(new Label(gestorIdiomas.setTexto("ranking.header.user"), headerStyle)).expandX().center();
        headerTable.add(new Label(gestorIdiomas.setTexto("ranking.header.score"), headerStyle)).width(150).right().padRight(15);
        rankingContentTable.add(headerTable).expandX().fillX().padBottom(5).row();

        List<LogicaUsuarios.RankingEntry> rankingList;
        if (currentRankingType == RankingType.TOTAL) {
            rankingList = (currentRankingScope == RankingScope.GLOBAL) ? userLogic.getRankingGlobal() : userLogic.getRankingAmigos(main.username);
        } else {
            rankingList = (currentRankingScope == RankingScope.GLOBAL) ? userLogic.getRankingGlobalPorNivel(selectedLevel) : userLogic.getRankingAmigosPorNivel(main.username, selectedLevel);
        }

        Drawable rowBackground = solid(0, 0, 0, 0.08f);

        if (rankingList == null || rankingList.isEmpty()) {
            rankingContentTable.add(new Label(gestorIdiomas.setTexto("ranking.empty"), rowStyle)).center().colspan(3).pad(20);
        } else {
            int rank = 1;
            for (LogicaUsuarios.RankingEntry entry : rankingList) {
                // === FILAS DE DATOS (CON PADDING) ===
                Table rowContent = new Table();
                if (rank % 2 == 0) {
                    rowContent.setBackground(rowBackground);
                }
                
                // Se añade el mismo padLeft y padRight que en las cabeceras para una alineación perfecta.
                rowContent.add(new Label(String.valueOf(rank), rowStyle)).width(150).left().padLeft(15);
                rowContent.add(new Label(entry.username, rowStyle)).expandX().center();
                rowContent.add(new Label(String.valueOf(entry.totalScore), rowStyle)).width(150).right().padRight(15);

                rankingContentTable.add(rowContent).expandX().fillX().padTop(5).row();
                rank++;
            }
        }
    }

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