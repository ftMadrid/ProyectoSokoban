package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MenuScreen implements Screen {

    private final Main main;
    private Stage stage;
    private LogicaUsuarios userLogic;
    private GestorIdiomas gestorIdiomas;
    private Texture backgroundTexture;
    private BitmapFont pixelFont;
    private BitmapFont titleFont;
    private Image avatarImage;
    private String selectedAvatarPath = null;

    public MenuScreen(final Main main) {
        this.main = main;
        this.stage = new Stage(new ScreenViewport());
        this.userLogic = new LogicaUsuarios();
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();
        this.backgroundTexture = new Texture(Gdx.files.internal("background2.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 32;
        p.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(p);

        p.size = 84;
        titleFont = generator.generateFont(p);
        generator.dispose();

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Table profileAccessTable = new Table();
        profileAccessTable.setFillParent(true);
        profileAccessTable.top().right();
        stage.addActor(profileAccessTable);

        String usuarioActual = (main.username != null) ? main.username : LogicaUsuarios.usuarioLogged;
        String avatarPath = userLogic.getAvatar(usuarioActual);
        avatarImage = new Image(new Texture(Gdx.files.internal(avatarPath)));

        // --- CORRECCIÓN AQUÍ: Añadir el nombre de usuario al lado del avatar ---
        Label.LabelStyle nameStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        Label nameLabel = new Label(usuarioActual, nameStyle);

        profileAccessTable.add(nameLabel).padRight(15);
        profileAccessTable.add(avatarImage).size(80, 80).padTop(10).padRight(20).padBottom(10);
        // --- FIN DE LA CORRECCIÓN ---

        avatarImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showProfileDialog();
            }
        });

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label(gestorIdiomas.setTexto("app.name"), titleStyle);
        table.add(title).padBottom(40).row();

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = pixelFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));

        TextButton playButton = new TextButton(gestorIdiomas.setTexto("menu.jugar"), buttonStyle);
        table.add(playButton).width(380).height(60).pad(10).row();

        TextButton friendsButton = new TextButton(gestorIdiomas.setTexto("menu.amigos"), buttonStyle);
        table.add(friendsButton).width(380).height(60).pad(10).row();
        
        // --- INICIO DEL CÓDIGO AÑADIDO ---
        TextButton rankingButton = new TextButton(gestorIdiomas.setTexto("ranking.title"), buttonStyle);
        table.add(rankingButton).width(380).height(60).pad(10).row();
        // --- FIN DEL CÓDIGO AÑADIDO ---

        TextButton preferencesButton = new TextButton(gestorIdiomas.setTexto("menu.preferencias"), buttonStyle);
        table.add(preferencesButton).width(380).height(60).pad(10).row();

        TextButton logoutButton = new TextButton(gestorIdiomas.setTexto("menu.cerrar_sesion"), buttonStyle);
        table.add(logoutButton).width(380).height(60).pad(10).row();

        playButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LevelSelectScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand); }
            @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); }
        });
        friendsButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new AmigosScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand); }
            @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); }
        });

        // --- INICIO DEL CÓDIGO AÑADIDO ---
        rankingButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new RankingScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand); }
            @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); }
        });
        // --- FIN DEL CÓDIGO AÑADIDO ---

        preferencesButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new PreferenciasScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand); }
            @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); }
        });
        logoutButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                main.username = null;
                LogicaUsuarios.usuarioLogged = null;
                main.resetToDefaults();
                gestorIdiomas.resetToDefault();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LoginScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand); }
            @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) { Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow); }
        });
    }

    private TextureRegionDrawable solid(float r, float g, float b, float a) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(r, g, b, a);
        pm.fill();
        TextureRegionDrawable dr = new TextureRegionDrawable(new TextureRegion(new Texture(pm)));
        pm.dispose();
        return dr;
    }

    private void showProfileDialog() {
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK,
                new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog profileDialog = new Dialog("", windowStyle);

        Table wrapper = new Table();
        wrapper.pad(26);
        wrapper.defaults().pad(6);

        Table profileHeader = new Table();
        String[] perfil = userLogic.getPerfil(main.username);
        final Image profileAvatar = new Image(new Texture(Gdx.files.internal(userLogic.getAvatar(main.username))));

        Table textInfo = new Table();
        Label.LabelStyle styleDark = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        Label nameUser = new Label(t("profile.username", "Usuario: ") + main.username, styleDark);
        Label fullName = new Label(t("profile.fullname", "Nombre: ") + (perfil != null && perfil.length >= 3 ? perfil[2] : ""), styleDark);
        textInfo.add(nameUser).left().row();
        textInfo.add(fullName).left().padTop(6).row();

        profileHeader.add(profileAvatar).size(128, 128).padRight(16);
        profileHeader.add(textInfo).left().expandX();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        final TextButton changeAvatarButton = new TextButton(t("profile.change_avatar", "Cambiar Avatar"), btnStyle);
        profileHeader.add(changeAvatarButton).right().width(260).height(60);

        wrapper.add(profileHeader).growX().row();

        Table bandScoresTitle = new Table();
        bandScoresTitle.setBackground(solid(0, 0, 0, 0.08f));
        Label hsTitle = new Label(t("profile.highscores", "Mejores Puntuaciones"), styleDark);
        hsTitle.setAlignment(Align.center);
        bandScoresTitle.add(hsTitle).growX().pad(6);
        wrapper.add(bandScoresTitle).growX().padTop(12).row();

        Table headerScores = new Table();
        headerScores.setBackground(solid(0, 0, 0, 0.05f));
        float[] cw = new float[]{320f, 320f};
        headerScores.add(new Label(t("history.nivel", "Nivel"), styleDark)).width(cw[0]).center().pad(4);
        headerScores.add(new Label(t("history.score", "Puntuacion"), styleDark)).width(cw[1]).center().pad(4);
        wrapper.add(headerScores).padTop(4).row();

        Table scoresTable = new Table();
        scoresTable.defaults().pad(4);
        Map<Integer, Integer> highScores = userLogic.getHighScores(main.username);
        if (highScores.isEmpty()) {
            Label empty = new Label(t("profile.no_scores", "No hay puntuaciones guardadas."), styleDark);
            empty.setAlignment(Align.center);
            scoresTable.add(empty).pad(6).colspan(2);
        } else {
            ArrayList<Integer> sortedLevels = new ArrayList<>(highScores.keySet());
            Collections.sort(sortedLevels);
            for (Integer level : sortedLevels) {
                scoresTable.add(new Label(t("history.nivel", "Nivel") + " " + level, styleDark)).width(cw[0]).center();
                scoresTable.add(new Label(String.valueOf(highScores.get(level)), styleDark)).width(cw[1]).center();
                scoresTable.row();
            }
        }
        ScrollPane sp = new ScrollPane(scoresTable);
        wrapper.add(sp).width(700).height(220).padTop(6).row();

        TextButton viewHistoryButton = new TextButton(t("history.view", "Ver Historial"), btnStyle);
        TextButton closeButton = new TextButton(t("profile.close", "Cerrar"), btnStyle);

        Table btns = new Table();
        btns.defaults().width(280).height(58).pad(8);
        btns.add(viewHistoryButton);
        btns.add(closeButton);
        wrapper.add(btns).padTop(8).row();

        changeAvatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
                showAvatarSelectionDialog(profileAvatar);
            }
        });
        viewHistoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
                showHistoryDialog();
            }
        });
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
            }
        });

        profileDialog.getContentTable().add(wrapper).prefWidth(1040).prefHeight(560);
        profileDialog.show(stage);
    }

    private void showAvatarSelectionDialog(final Image profileAvatarImage) {
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK,
                new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog avatarDialog = new Dialog("", windowStyle);

        Table wrapper = new Table();
        wrapper.pad(28);

        Label.LabelStyle styleDark = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        Label title = new Label(t("avatar.select", "Selecciona un Avatar"), styleDark);
        wrapper.add(title).padBottom(15).row();

        Table avatarTable = new Table();
        avatarTable.defaults().size(96, 96).pad(12);
        selectedAvatarPath = null;

        final String[] avatarPaths = {
                "avatares/south.png", "avatares/avatar1.png", "avatares/avatar2.png",
                "avatares/avatar3.png", "avatares/avatar4.png", "avatares/avatar5.png", "avatares/avatar6.png"
        };

        final List<Image> avatarImages = new ArrayList<>();
        int col = 0;
        for (final String path : avatarPaths) {
            final Image img = new Image(new Texture(Gdx.files.internal(path)));
            img.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedAvatarPath = path;
                    for(Image image : avatarImages){
                        image.setColor(Color.WHITE);
                    }
                    img.setColor(Color.LIME);
                }
            });
            avatarTable.add(img);
            avatarImages.add(img);
            col++;
            if (col % 4 == 0) {
                avatarTable.row();
            }
        }
        wrapper.add(avatarTable).row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        TextButton saveButton = new TextButton(t("avatar.save", "Guardar"), btnStyle);
        TextButton backButton = new TextButton(t("avatar.back", "Volver"), btnStyle);

        Table buttonTable = new Table();
        buttonTable.defaults().width(250).height(58).pad(10);
        buttonTable.add(saveButton).padRight(10);
        buttonTable.add(backButton);
        wrapper.add(buttonTable).padTop(20).row();

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedAvatarPath != null) {
                    userLogic.setAvatar(main.username, selectedAvatarPath);
                    Texture newAvatarTexture = new Texture(Gdx.files.internal(selectedAvatarPath));
                    avatarImage.setDrawable(new TextureRegionDrawable(newAvatarTexture));
                    profileAvatarImage.setDrawable(new TextureRegionDrawable(newAvatarTexture));
                }
                avatarDialog.hide();
                showProfileDialog();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                avatarDialog.hide();
                showProfileDialog();
            }
        });


        avatarDialog.getContentTable().add(wrapper).prefWidth(820).prefHeight(480);
        avatarDialog.show(stage);
    }

    private String t(String key, String fallback) {
        String s = gestorIdiomas.setTexto(key);
        return (s == null || s.startsWith("[")) ? fallback : s;
    }

    private void showHistoryDialog() {
        Window.WindowStyle windowStyle =
                new Window.WindowStyle(pixelFont, Color.BLACK,
                        new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog dlg = new Dialog("", windowStyle);

        Table root = new Table();
        root.pad(22);
        root.defaults().pad(6);

        Table band = new Table();
        band.setBackground(solid(0,0,0,0.08f));
        Label title = new Label(t("history.title", "Historial de partidas"),
                new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E")));
        title.setAlignment(Align.center);
        band.add(title).growX().pad(15, 0, 15, 0).row();
        root.add(band).growX().row();

        float W = 1100f;
        float H = 560f;

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter ph = new FreeTypeFontGenerator.FreeTypeFontParameter();
        ph.size = 28;
        ph.color = Color.valueOf("1E1E1E");
        BitmapFont headerFont = gen.generateFont(ph);

        ph.size = 28;
        BitmapFont cellFont = gen.generateFont(ph);
        gen.dispose();

        Label.LabelStyle headerStyle = new Label.LabelStyle(headerFont, Color.valueOf("1E1E1E"));
        Label.LabelStyle cellStyle = new Label.LabelStyle(cellFont, Color.valueOf("1E1E1E"));

        float[] colW = new float[]{320f, 90f, 110f, 110f, 120f, 120f};

        Table header = new Table();
        header.setBackground(solid(0,0,0,0.06f));
        header.defaults().pad(4);
        header.add(new Label(t("history.fecha", "Fecha"), headerStyle)).width(colW[0]).center();
        header.add(new Label(t("history.nivel", "Nivel"), headerStyle)).width(colW[1]).center();
        header.add(new Label(t("history.score", "Score"), headerStyle)).width(colW[2]).center();
        header.add(new Label(t("history.intentos", "Intentos"), headerStyle)).width(colW[3]).center();
        header.add(new Label(t("history.duracion", "Duracion"), headerStyle)).width(colW[4]).center();
        header.add(new Label(t("history.resultado", "Resultado"), headerStyle)).width(colW[5]).center();
        root.add(header).width(W).row();

        Table rows = new Table();

        List<LogicaUsuarios.HistorialRegistro> lista = userLogic.leerHistorial(main.username);
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        boolean par = false;
        for (LogicaUsuarios.HistorialRegistro r : lista) {
            Table line = new Table();
            if (par) line.setBackground(solid(0,0,0,0.05f));
            par = !par;

            line.defaults().pad(5);
            line.add(new Label(fmt.format(new Date(r.fechaMs)), cellStyle)).width(colW[0]).left().padLeft(15);
            line.add(new Label(String.valueOf(r.nivel), cellStyle)).width(colW[1]).center();
            line.add(new Label(String.valueOf(r.score), cellStyle)).width(colW[2]).center();
            line.add(new Label(String.valueOf(r.intentos), cellStyle)).width(colW[3]).center();
            line.add(new Label(formatDur(r.duracionMs), cellStyle)).width(colW[4]).center();
            line.add(new Label(r.exito ? "SI" : "NO", cellStyle)).width(colW[5]).center();

            rows.add(line).growX().row();
        }

        ScrollPane sp = new ScrollPane(rows);
        root.add(sp).width(W).height(H - 140f).padTop(6).row();

        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle();
        btn.font = pixelFont;
        btn.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        TextButton back = new TextButton(t("back.button", "VOLVER"), btn);

        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dlg.hide();
                showProfileDialog();
            }
        });

        Table btnRow = new Table();
        btnRow.add(back).width(340).height(60).padTop(6).center();
        root.add(btnRow).center().padTop(10).row();

        dlg.getContentTable().add(root).prefWidth(1120).prefHeight(620);
        dlg.show(stage);
    }

    private String formatDur(long ms) {
        long sec = Math.max(0, ms / 1000);
        long m = sec / 60;
        long s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playLobbyMusic();
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

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        pixelFont.dispose();
        titleFont.dispose();
    }
}