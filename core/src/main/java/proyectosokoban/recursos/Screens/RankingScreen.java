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
        
        // --- Fuentes con color oscuro para que contrasten con el fondo claro del 'field' ---
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

    private void createUI() {
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        outerTable.center();
        stage.addActor(outerTable);

        // --- Usamos el asset "field 2.png" que ya existe en tu proyecto ---
        Table panelTable = new Table();
        panelTable.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        
        float panelWidth = Gdx.graphics.getWidth() * 0.85f;
        float panelHeight = Gdx.graphics.getHeight() * 0.9f;
        
        outerTable.add(panelTable).size(panelWidth, panelHeight);

        // --- Contenido que irá DENTRO del panelTable ---
        panelTable.pad(20, 40, 20, 40); // Padding interno

        // 1. Título
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.valueOf("1E1E1E"));
        Label titleLabel = new Label(t("ranking.title", "Ranking"), titleStyle);
        panelTable.add(titleLabel).center().padTop(10).padBottom(15).row();

        // 2. Filtros
        Table filtersTable = new Table();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = smallFont;
        buttonStyle.fontColor = Color.valueOf("1E1E1E");
        buttonStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        Label.LabelStyle sectionStyle = new Label.LabelStyle(smallFont, Color.valueOf("1E1E1E"));
        filtersTable.add(new Label(t("ranking.view", "Vista"), sectionStyle)).padRight(10);

        TextButton globalButton = new TextButton(t("ranking.global", "Global"), buttonStyle);
        globalButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingScope = RankingScope.GLOBAL; populateRankingTable(); }});

        TextButton friendsButton = new TextButton(t("ranking.friends", "Amigos"), buttonStyle);
        friendsButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingScope = RankingScope.FRIENDS; populateRankingTable(); }});

        filtersTable.add(globalButton).size(120, 50).padRight(5);
        filtersTable.add(friendsButton).size(120, 50).padRight(30);
        
        filtersTable.add(new Label(t("ranking.type", "Tipo"), sectionStyle)).padRight(10);
        TextButton totalButton = new TextButton(t("ranking.total", "Total"), buttonStyle);
        totalButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingType = RankingType.TOTAL; levelSelectorTable.setVisible(false); populateRankingTable(); }});

        TextButton byLevelButton = new TextButton(t("ranking.by_level", "Por Nivel"), buttonStyle);
        byLevelButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { currentRankingType = RankingType.LEVEL; levelSelectorTable.setVisible(true); populateRankingTable(); }});

        filtersTable.add(totalButton).size(120, 50).padRight(5);
        filtersTable.add(byLevelButton).size(120, 50);

        panelTable.add(filtersTable).center().padBottom(10).row();

        // 3. Selector de nivel
        levelSelectorTable = new Table();
        levelSelectorTable.setVisible(false);
        populateLevelSelector();
        panelTable.add(levelSelectorTable).center().padBottom(10).row();

        // 4. Área del ranking con scroll
        rankingContentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(rankingContentTable);
        scrollPane.setFadeScrollBars(false);
        // La clave: .expand() fuerza a esta celda a ocupar el espacio vertical sobrante
        panelTable.add(scrollPane).expand().fill().padBottom(15).row();

        // 5. Botón de volver
        TextButton backButton = new TextButton(t("back.button", "BACK TO MENU"), buttonStyle);
        backButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { transicionSuave.fadeOutAndChangeScreen(main, stage, new MenuScreen(main)); }});
        panelTable.add(backButton).size(200, 50).align(Align.right);
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

        rankingContentTable.add(new Label("#", headerStyle)).width(80).center();
        rankingContentTable.add(new Label(t("ranking.header.user", "User"), headerStyle)).expandX().center();
        rankingContentTable.add(new Label(t("ranking.header.score", "Score"), headerStyle)).width(150).center().row();

        List<LogicaUsuarios.RankingEntry> rankingList;

        if (currentRankingType == RankingType.TOTAL) {
            rankingList = (currentRankingScope == RankingScope.GLOBAL) ? userLogic.getRankingGlobal() : userLogic.getRankingAmigos(main.username);
        } else {
            rankingList = (currentRankingScope == RankingScope.GLOBAL) ? userLogic.getRankingGlobalPorNivel(selectedLevel) : userLogic.getRankingAmigosPorNivel(main.username, selectedLevel);
        }
        
        // --- LÓGICA PARA EL FONDO DE LAS FILAS ---
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.08f); // Color negro semitransparente
        pixmap.fill();
        Drawable rowBackground = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        if (rankingList == null || rankingList.isEmpty()) {
            rankingContentTable.add(new Label(t("ranking.empty", "No data available"), rowStyle)).center().colspan(3).pad(20);
        } else {
            int rank = 1;
            for (LogicaUsuarios.RankingEntry entry : rankingList) {
                Table rowTable = new Table();
                if(rank % 2 == 0){ // Si la fila es par, le ponemos fondo
                    rowTable.setBackground(rowBackground);
                }
                rowTable.add(new Label(String.valueOf(rank), rowStyle)).width(80).center();
                rowTable.add(new Label(entry.username, rowStyle)).expandX().center();
                rowTable.add(new Label(String.valueOf(entry.totalScore), rowStyle)).width(150).center();
                
                rankingContentTable.add(rowTable).expandX().fillX().padTop(5).row();
                rank++;
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