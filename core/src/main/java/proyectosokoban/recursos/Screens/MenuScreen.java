package proyectosokoban.recursos.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.text.SimpleDateFormat;
import java.util.*;

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
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.valueOf("F5F5DC");
        pixelFont = generator.generateFont(parameter);

        parameter.size = 84;
        titleFont = generator.generateFont(parameter);
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

        TextButton playButton = new TextButton(gestorIdiomas.setTexto("menu.jugar"), buttonStyle);
        table.add(playButton).width(380).height(60).pad(10).row();

        TextButton friendsButton = new TextButton(gestorIdiomas.setTexto("menu.amigos"), buttonStyle);
        table.add(friendsButton).width(380).height(60).pad(10).row();

        TextButton preferencesButton = new TextButton(gestorIdiomas.setTexto("menu.preferencias"), buttonStyle);
        table.add(preferencesButton).width(380).height(60).pad(10).row();

        TextButton logoutButton = new TextButton(gestorIdiomas.setTexto("menu.cerrar_sesion"), buttonStyle);
        table.add(logoutButton).width(380).height(60).pad(10).row();

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LevelSelectScreen(main));
            }
        });

        friendsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new AmigosScreen(main));
            }
        });

        preferencesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new PreferenciasScreen(main));
            }
        });

        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.username = null;
                LogicaUsuarios.usuarioLogged = null;
                main.resetToDefaults();
                gestorIdiomas.resetToDefault();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LoginScreen(main));
            }
        });
    }

    private void showProfileDialog() {
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK, new TextureRegionDrawable(new Texture("ui/field 2.png")));
        final Dialog profileDialog = new Dialog("", windowStyle);

        Table root = new Table();
        root.pad(18);
        root.defaults().pad(6);

        Label lblTitle = new Label(gestorIdiomas.setTexto("profile.title"), new Label.LabelStyle(pixelFont, Color.BLACK));
        lblTitle.setAlignment(Align.center);
        root.add(lblTitle).growX().center().padBottom(8).row();

        String[] perfil = userLogic.getPerfil(main.username);
        Image profileAvatar = new Image(new Texture(Gdx.files.internal(userLogic.getAvatar(main.username))));

        Label.LabelStyle style = new Label.LabelStyle(pixelFont, Color.BLACK);
        Label nameUser = new Label(gestorIdiomas.setTexto("profile.username") + main.username, style);
        Label fullName = new Label(gestorIdiomas.setTexto("profile.fullname") + (perfil != null && perfil.length >= 3 ? perfil[2] : ""), style);

        Table header = new Table();
        header.add(profileAvatar).size(96, 96).padRight(16);
        Table textInfo = new Table();
        textInfo.add(nameUser).left().row();
        textInfo.add(fullName).left().padTop(6).row();
        header.add(textInfo).left();
        root.add(header).left().padBottom(6).row();

        Label hsTitle = new Label(gestorIdiomas.setTexto("profile.highscores"), style);
        hsTitle.setAlignment(Align.center);
        root.add(hsTitle).growX().center().padTop(6).row();

        Table scoresTable = new Table();
        Map<Integer, Integer> highScores = userLogic.getHighScores(main.username);
        if (highScores.isEmpty()) {
            Label empty = new Label(gestorIdiomas.setTexto("amigos.no_amigos").replace("No tienes amigos agregados.", "No hay puntuaciones guardadas."), style);
            empty.setAlignment(Align.center);
            scoresTable.add(empty).pad(6);
        } else {
            ArrayList<Integer> sortedLevels = new ArrayList<>(highScores.keySet());
            Collections.sort(sortedLevels);
            for (Integer level : sortedLevels) {
                scoresTable.add(new Label("Nivel " + level + ": ", style)).right().padRight(10);
                scoresTable.add(new Label(String.valueOf(highScores.get(level)), style)).left().row();
            }
        }
        ScrollPane sp = new ScrollPane(scoresTable);
        sp.setFadeScrollBars(false);
        sp.setScrollingDisabled(true, false);
        sp.setOverscroll(false, false);
        root.add(sp).width(520).height(150).padTop(6).row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.BLACK;
        btnStyle.up = new TextureRegionDrawable(new Texture("ui/button1.png"));

        TextButton changeAvatarButton = new TextButton(gestorIdiomas.setTexto("profile.change_avatar"), btnStyle);
        TextButton viewHistoryButton = new TextButton("Ver historial", btnStyle);
        TextButton closeButton = new TextButton(gestorIdiomas.setTexto("profile.close"), btnStyle);

        Table btns = new Table();
        btns.defaults().width(220).height(52).pad(8);
        btns.add(changeAvatarButton);
        btns.add(viewHistoryButton);
        btns.add(closeButton);
        root.add(btns).padTop(8).row();

        changeAvatarButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
                showAvatarSelectionDialog(profileAvatar);
            }
        });

        viewHistoryButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
                showHistoryDialog();
            }
        });

        closeButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
            }
        });

        profileDialog.getContentTable().add(root);
        profileDialog.show(stage);
    }

    private void showAvatarSelectionDialog(final Image profileAvatarImage) {
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK, new TextureRegionDrawable(new Texture("ui/field 2.png")));
        final Dialog avatarDialog = new Dialog("", windowStyle);

        Table avatarTable = new Table();
        avatarTable.pad(18);
        avatarTable.defaults().size(80, 80).pad(10);

        final String[] avatarPaths = {
                "avatares/south.png", "avatares/avatar1.png", "avatares/avatar2.png",
                "avatares/avatar3.png", "avatares/avatar4.png", "avatares/avatar5.png", "avatares/avatar6.png"
        };

        int col = 0;
        for (final String path : avatarPaths) {
            Image img = new Image(new Texture(Gdx.files.internal(path)));
            img.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
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
            if (col % 4 == 0) avatarTable.row();
        }

        ScrollPane sp = new ScrollPane(avatarTable);
        sp.setFadeScrollBars(false);
        sp.setOverscroll(false, false);
        sp.setScrollingDisabled(true, false);

        Table wrapper = new Table();
        wrapper.add(sp).width(520).height(260).row();

        avatarDialog.getContentTable().add(wrapper).pad(6);
        avatarDialog.show(stage);
    }

    private String t(String key, String fallback) {
        String s = gestorIdiomas.setTexto(key);
        if (s == null || s.startsWith("[")) return fallback;
        return s;
    }

    private void showHistoryDialog() {
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK, new TextureRegionDrawable(new Texture("ui/field 2.png")));
        final Dialog dlg = new Dialog("", windowStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle(pixelFont, Color.BLACK);

        Table root = new Table();
        root.pad(18);
        root.defaults().pad(6);

        Label title = new Label(t("history.title", "Historial de partidas"), labelStyle);
        title.setAlignment(Align.center);
        root.add(title).growX().center().padBottom(4).row();

        float W = 580f;
        float[] colW = new float[]{150f, 70f, 80f, 90f, 100f, 90f};

        Table header = new Table();
        header.defaults().pad(4);
        header.add(new Label(t("history.fecha", "Fecha"), labelStyle)).width(colW[0]).center();
        header.add(new Label(t("history.nivel", "Nivel"), labelStyle)).width(colW[1]).center();
        header.add(new Label(t("history.score", "Score"), labelStyle)).width(colW[2]).center();
        header.add(new Label(t("history.intentos", "Intentos"), labelStyle)).width(colW[3]).center();
        header.add(new Label(t("history.duracion", "Duracion"), labelStyle)).width(colW[4]).center();
        header.add(new Label(t("history.resultado", "Resultado"), labelStyle)).width(colW[5]).center();

        root.add(header).width(W).row();

        Table rows = new Table();
        rows.defaults().pad(4);
        
        List<LogicaUsuarios.HistorialRegistro> lista = userLogic.leerHistorial(main.username);

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (LogicaUsuarios.HistorialRegistro r : lista) {
            rows.add(new Label(fmt.format(new Date(r.fechaMs)), labelStyle)).width(colW[0]).left();
            rows.add(new Label(String.valueOf(r.nivel), labelStyle)).width(colW[1]).center();
            rows.add(new Label(String.valueOf(r.score), labelStyle)).width(colW[2]).center();
            rows.add(new Label(String.valueOf(r.intentos), labelStyle)).width(colW[3]).center();
            rows.add(new Label(formatDur(r.duracionMs), labelStyle)).width(colW[4]).center();
            rows.add(new Label(r.exito ? "✓" : "✗", labelStyle)).width(colW[5]).center();
            rows.row();
        }


        ScrollPane sp = new ScrollPane(rows);
        sp.setFadeScrollBars(false);
        sp.setScrollingDisabled(true, false);
        sp.setOverscroll(false, false);

        root.add(sp).width(W).height(260).row();

        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle();
        btn.font = pixelFont;
        btn.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        TextButton back = new TextButton(t("back.button", "Volver"), btn);

        Table btnRow = new Table();
        btnRow.add(back).width(220).height(52).padTop(6).center();
        root.add(btnRow).center().padTop(6).row();

        back.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dlg.hide();
                showProfileDialog();
            }
        });

        dlg.getContentTable().add(root);
        dlg.show(stage);
    }

    private String formatDur(long ms) {
        long sec = Math.max(0, ms / 1000);
        long m = sec / 60;
        long s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    @Override
    public void show() {
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
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        pixelFont.dispose();
        titleFont.dispose();
    }
}