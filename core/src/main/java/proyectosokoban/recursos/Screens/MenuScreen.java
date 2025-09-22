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
<<<<<<< HEAD
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
=======
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
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
import java.util.Map;
import java.util.Date;

public class MenuScreen implements Screen {

    private final Main main;
    private Stage stage;
    private LogicaUsuarios userLogic;
    private GestorIdiomas gestorIdiomas;
    private Texture backgroundTexture;
    private BitmapFont pixelFont;
    private BitmapFont titleFont;
    private Image avatarImage;

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

        Table profileTable = new Table();
        profileTable.setFillParent(true);
        profileTable.top().right();
        stage.addActor(profileTable);

        String usuarioActual = (main.username != null) ? main.username : LogicaUsuarios.usuarioLogged;
        String avatarPath = userLogic.getAvatar(usuarioActual);
        avatarImage = new Image(new Texture(Gdx.files.internal(avatarPath)));

        Label.LabelStyle nameStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        Label nameLabel = new Label(usuarioActual, nameStyle);

        profileTable.add(nameLabel).right().pad(20);
        profileTable.add(avatarImage).size(80, 80).pad(20);

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

        TextButton preferencesButton = new TextButton(gestorIdiomas.setTexto("menu.preferencias"), buttonStyle);
        table.add(preferencesButton).width(380).height(60).pad(10).row();

        TextButton logoutButton = new TextButton(gestorIdiomas.setTexto("menu.cerrar_sesion"), buttonStyle);
        table.add(logoutButton).width(380).height(60).pad(10).row();

        playButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LevelSelectScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                playButton.addAction(
                        Actions.sequence(
                                Actions.scaleTo(0.9f, 0.9f, 0.05f),
                                Actions.scaleTo(1f, 1f, 0.05f)
                        )
                );
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        friendsButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new AmigosScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                friendsButton.addAction(
                        Actions.sequence(
                                Actions.scaleTo(0.9f, 0.9f, 0.05f),
                                Actions.scaleTo(1f, 1f, 0.05f)
                        )
                );
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        preferencesButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new PreferenciasScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                preferencesButton.addAction(
                        Actions.sequence(
                                Actions.scaleTo(0.9f, 0.9f, 0.05f),
                                Actions.scaleTo(1f, 1f, 0.05f)
                        )
                );
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        logoutButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                main.username = null;
                LogicaUsuarios.usuarioLogged = null;
                main.resetToDefaults();
                gestorIdiomas.resetToDefault();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LoginScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                logoutButton.addAction(
                        Actions.sequence(
                                Actions.scaleTo(0.9f, 0.9f, 0.05f),
                                Actions.scaleTo(1f, 1f, 0.05f)
                        )
                );
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
            }
        });

        playButton.setTransform(true);
        playButton.setOrigin(Align.center);
        playButton.setScale(1f);
        playButton.getColor().a = 1f;

        friendsButton.setTransform(true);
        friendsButton.setOrigin(Align.center);
        friendsButton.setScale(1f);
        friendsButton.getColor().a = 1f;

        preferencesButton.setTransform(true);
        preferencesButton.setOrigin(Align.center);
        preferencesButton.setScale(1f);
        preferencesButton.getColor().a = 1f;

        logoutButton.setTransform(true);
        logoutButton.setOrigin(Align.center);
        logoutButton.setScale(1f);
        logoutButton.getColor().a = 1f;

    }

    // ---------- Helpers de estilo para foregrounds locales (no se salen del field) ----------
    private TextureRegionDrawable solid(float r, float g, float b, float a) {
        Pixmap pm = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pm.setColor(r,g,b,a);
        pm.fill();
        TextureRegionDrawable dr = new TextureRegionDrawable(new TextureRegion(new Texture(pm)));
        pm.dispose();
        return dr;
    }

    private void showProfileDialog() {
        Window.WindowStyle windowStyle =
            new Window.WindowStyle(pixelFont, Color.BLACK,
                    new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog profileDialog = new Dialog("", windowStyle);

        // wrapper = field, content = interior
        Table wrapper = new Table();
        wrapper.pad(26);
        wrapper.defaults().pad(6);

        // Foreground interno (banda translúcida) – NO full-screen
        Table headerBand = new Table();
        headerBand.setBackground(solid(0,0,0,0.08f));
        headerBand.add(new Label(gestorIdiomas.setTexto("profile.title"),
                new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"))))
                .growX().center().pad(8);
        wrapper.add(headerBand).growX().row();

        // Datos de usuario
        String[] perfil = userLogic.getPerfil(main.username);
        Image profileAvatar = new Image(new Texture(Gdx.files.internal(userLogic.getAvatar(main.username))));

        Label.LabelStyle styleDark = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));

        Label nameUser = new Label(gestorIdiomas.setTexto("profile.username") + main.username, styleDark);
        Label fullName = new Label(gestorIdiomas.setTexto("profile.fullname") + (perfil != null && perfil.length >= 3 ? perfil[2] : ""), styleDark);

        Table header = new Table();
        header.add(profileAvatar).size(128, 128).padRight(16).padTop(6).padBottom(6);
        Table textInfo = new Table();
        textInfo.add(nameUser).left().row();
        textInfo.add(fullName).left().padTop(6).row();

        // Botón cambiar avatar arriba a la derecha
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        final TextButton changeAvatarButton = new TextButton(gestorIdiomas.setTexto("profile.change_avatar"), btnStyle);

        Table headerRow = new Table();
        headerRow.add(header).left().expandX();
        headerRow.add(changeAvatarButton).right().width(260).height(60);
        wrapper.add(headerRow).growX().row();

        // Título de Highscores con banda
        Table bandScoresTitle = new Table();
        bandScoresTitle.setBackground(solid(0,0,0,0.08f));
        Label hsTitle = new Label(gestorIdiomas.setTexto("profile.highscores"), styleDark);
        hsTitle.setAlignment(Align.center);
        bandScoresTitle.add(hsTitle).growX().pad(6);
        wrapper.add(bandScoresTitle).growX().padTop(6).row();

        // Cabecera tabla
        Table headerScores = new Table();
        headerScores.setBackground(solid(0,0,0,0.05f));
        float[] cw = new float[]{320f, 320f};
        Label.LabelStyle headerStyle = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        headerScores.add(new Label("Nivel", headerStyle)).width(cw[0]).center().pad(4);
        headerScores.add(new Label("Puntuacion", headerStyle)).width(cw[1]).center().pad(4);
        wrapper.add(headerScores).padTop(4).row();

        // Filas highscores
        Table scoresTable = new Table();
        scoresTable.defaults().pad(4);
        Map<Integer, Integer> highScores = userLogic.getHighScores(main.username);
        if (highScores.isEmpty()) {
            Label empty = new Label(gestorIdiomas.setTexto("amigos.no_amigos")
                    .replace("No tienes amigos agregados.", "No hay puntuaciones guardadas."), styleDark);
            empty.setAlignment(Align.center);
            scoresTable.add(empty).pad(6).colspan(2);
        } else {
            ArrayList<Integer> sortedLevels = new ArrayList<>(highScores.keySet());
            Collections.sort(sortedLevels);
            for (Integer level : sortedLevels) {
                scoresTable.add(new Label("Nivel " + level, styleDark)).width(cw[0]).right().padRight(10);
                scoresTable.add(new Label(String.valueOf(highScores.get(level)), styleDark)).width(cw[1]).left();
                scoresTable.row();
            }
        }
        ScrollPane sp = new ScrollPane(scoresTable);
        sp.setFadeScrollBars(false);
        sp.setOverscroll(false, false);
        sp.setScrollingDisabled(true, false);
        wrapper.add(sp).width(700).height(220).padTop(6).row();

        // Botonera inferior
        TextButton viewHistoryButton = new TextButton(t("history.view", "Ver historial"), btnStyle);
        TextButton closeButton = new TextButton(gestorIdiomas.setTexto("profile.close"), btnStyle);

        Table btns = new Table();
        btns.defaults().width(280).height(58).pad(8);
        btns.add(viewHistoryButton);
        btns.add(closeButton);
        wrapper.add(btns).padTop(8).row();

        final Dialog dlg = profileDialog; // para inner listeners

        changeAvatarButton.addListener(new ClickListener() {
<<<<<<< HEAD
            @Override public void clicked(InputEvent event, float x, float y) {
                dlg.hide();
=======
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
                showAvatarSelectionDialog(profileAvatar);
            }
        });

        viewHistoryButton.addListener(new ClickListener() {
<<<<<<< HEAD
            @Override public void clicked(InputEvent event, float x, float y) {
                dlg.hide();
=======
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
                showHistoryDialog();
            }
        });

        closeButton.addListener(new ClickListener() {
<<<<<<< HEAD
            @Override public void clicked(InputEvent event, float x, float y) {
                dlg.hide();
=======
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
            }
        });

        // Ajustes de tamaño del field del diálogo (grande para que quepa todo)
        profileDialog.getContentTable().add(wrapper).prefWidth(1040).prefHeight(560);
        profileDialog.show(stage);
    }

    private void showAvatarSelectionDialog(final Image profileAvatarImage) {
        Window.WindowStyle windowStyle =
                new Window.WindowStyle(pixelFont, Color.BLACK,
                        new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog avatarDialog = new Dialog("", windowStyle);

        Table avatarTable = new Table();
        avatarTable.pad(18);
        avatarTable.defaults().size(96, 96).pad(12);

        final String[] avatarPaths = {
            "avatares/south.png", "avatares/avatar1.png", "avatares/avatar2.png",
            "avatares/avatar3.png", "avatares/avatar4.png", "avatares/avatar5.png", "avatares/avatar6.png"
        };

        int col = 0;
        for (final String path : avatarPaths) {
            Image img = new Image(new Texture(Gdx.files.internal(path)));
            img.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    userLogic.setAvatar(main.username, path);
                    Texture newAvatarTexture = new Texture(Gdx.files.internal(path));
                    avatarImage.setDrawable(new TextureRegionDrawable(newAvatarTexture));
                    profileAvatarImage.setDrawable(new TextureRegionDrawable(newAvatarTexture));
                    avatarDialog.hide();
                    showProfileDialog();
                }
            });
            avatarTable.add(img);
            col++;
            if (col % 4 == 0) {
                avatarTable.row();
            }
        }

        ScrollPane sp = new ScrollPane(avatarTable);
        sp.setFadeScrollBars(false);
        sp.setOverscroll(false, false);
        sp.setScrollingDisabled(true, false);

        Table wrapper = new Table();
        wrapper.add(sp).width(720).height(320).row();

        avatarDialog.getContentTable().add(wrapper).pad(6);
        avatarDialog.show(stage);
    }

    private String t(String key, String fallback) {
        String s = gestorIdiomas.setTexto(key);
        if (s == null || s.startsWith("[")) {
            return fallback;
        }
        return s;
    }

    private void showHistoryDialog() {
        Window.WindowStyle windowStyle =
                new Window.WindowStyle(pixelFont, Color.BLACK,
                        new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog dlg = new Dialog("", windowStyle);

        // contenedor del field
        Table root = new Table();
        root.pad(22);
        root.defaults().pad(6);

        // Título con banda interna (foreground) — centrado
        Table band = new Table();
        band.setBackground(solid(0,0,0,0.08f));
        Label title = new Label(t("history.title", "Historial de partidas"),
                new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E")));
        title.setAlignment(Align.center);
        band.add(title).growX().pad(6);
        root.add(band).growX().row();

        // Tamaño grande del field
        float W = 1100f;
        float H = 560f;

        // estilos más grandes
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter ph = new FreeTypeFontGenerator.FreeTypeFontParameter();
        ph.size = 26; ph.color = Color.valueOf("1E1E1E");
        BitmapFont headerFont = gen.generateFont(ph);
        ph.size = 22;
        BitmapFont cellFont = gen.generateFont(ph);
        gen.dispose();

        Label.LabelStyle headerStyle = new Label.LabelStyle(headerFont, Color.valueOf("1E1E1E"));
        Label.LabelStyle cellStyle = new Label.LabelStyle(cellFont, Color.valueOf("1E1E1E"));

        float[] colW = new float[]{320f, 90f, 110f, 110f, 120f, 120f};

        // Cabecera de la tabla con banda
        Table header = new Table();
        header.setBackground(solid(0,0,0,0.06f));
        header.defaults().pad(4);
        header.add(new Label(t("history.fecha", "Fecha"), headerStyle)).width(colW[0]).center();
        header.add(new Label(t("history.nivel", "Nivel"), headerStyle)).width(colW[1]).center();
        header.add(new Label(t("history.score", "Score"), headerStyle)).width(colW[2]).center();
        header.add(new Label(t("history.intentos", "Intentos"), headerStyle)).width(colW[3]).center();
        header.add(new Label(t("history.duracion", "Duración"), headerStyle)).width(colW[4]).center();
        header.add(new Label(t("history.resultado", "Resultado"), headerStyle)).width(colW[5]).center();
        root.add(header).width(W).row();

        // Filas
        Table rows = new Table();
        rows.defaults().pad(4);
<<<<<<< HEAD
=======

        List<LogicaUsuarios.HistorialRegistro> lista = userLogic.leerHistorial(main.username);
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516

        java.util.List<LogicaUsuarios.HistorialRegistro> lista = userLogic.leerHistorial(main.username);
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

<<<<<<< HEAD
        boolean par = false;
        for (LogicaUsuarios.HistorialRegistro r : lista) {
            Table line = new Table();
            if (par) line.setBackground(solid(0,0,0,0.05f));
            par = !par;

            line.add(new Label(fmt.format(new Date(r.fechaMs)), cellStyle)).width(colW[0]).left();
            line.add(new Label(String.valueOf(r.nivel), cellStyle)).width(colW[1]).center();
            line.add(new Label(String.valueOf(r.score), cellStyle)).width(colW[2]).center();
            line.add(new Label(String.valueOf(r.intentos), cellStyle)).width(colW[3]).center();
            line.add(new Label(formatDur(r.duracionMs), cellStyle)).width(colW[4]).center();
            line.add(new Label(r.exito ? "✓" : "✗", cellStyle)).width(colW[5]).center();

            rows.add(line).growX().row();
        }

=======
>>>>>>> e726e7a1af429cf58bd574b8a8f1d1a0e5674516
        ScrollPane sp = new ScrollPane(rows);
        sp.setFadeScrollBars(false);
        sp.setScrollingDisabled(false, false);
        sp.setOverscroll(false, false);

        // el scroll ocupa el campo interior; no sobresale
        root.add(sp).width(W).height(H - 140f).padTop(6).row();

        // Botón volver
        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle();
        btn.font = pixelFont;
        btn.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        TextButton back = new TextButton(t("back.button", "VOLVER AL MENU"), btn);

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

        // finalmente, añadir al diálogo con tamaño amplio
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

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        pixelFont.dispose();
        titleFont.dispose();
    }
}
