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
import java.util.Set;

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

        Label.LabelStyle nameStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        Label nameLabel = new Label(usuarioActual, nameStyle);

        profileAccessTable.add(nameLabel).padRight(15);
        profileAccessTable.add(avatarImage).size(80, 80).padTop(10).padRight(20).padBottom(10);

        avatarImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showProfileDialog();
                avatarImage.addAction(
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

        TextButton rankingButton = new TextButton(gestorIdiomas.setTexto("ranking.title"), buttonStyle);
        table.add(rankingButton).width(380).height(60).pad(10).row();

        TextButton preferencesButton = new TextButton(gestorIdiomas.setTexto("menu.preferencias"), buttonStyle);
        table.add(preferencesButton).width(380).height(60).pad(10).row();

        TextButton logoutButton = new TextButton(gestorIdiomas.setTexto("menu.cerrar_sesion"), buttonStyle);
        table.add(logoutButton).width(380).height(60).pad(10).row();

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.stopAllMusic();
                LogicaUsuarios userLogic = new LogicaUsuarios();
                boolean tutorialCompletado = userLogic.esPrimeraVezJugando(main.username);
                Screen siguienteScreen = !tutorialCompletado ? new TutorialScreen(main, false) : new LevelSelectScreen(main);
                transicionSuave.fadeOutAndChangeScreen(main, stage, siguienteScreen);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                playButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new AmigosScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                friendsButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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

        rankingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new RankingScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                rankingButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transicionSuave.fadeOutAndChangeScreen(main, stage, new PreferenciasScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                preferencesButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.username = null;
                LogicaUsuarios.usuarioLogged = null;
                main.resetToDefaults();
                gestorIdiomas.resetToDefault();
                transicionSuave.fadeOutAndChangeScreen(main, stage, new LoginScreen(main));
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                logoutButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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

        avatarImage.setOrigin(Align.center);
        playButton.setTransform(true);
        playButton.setOrigin(Align.center);
        friendsButton.setTransform(true);
        friendsButton.setOrigin(Align.center);
        rankingButton.setTransform(true);
        rankingButton.setOrigin(Align.center);
        preferencesButton.setTransform(true);
        preferencesButton.setOrigin(Align.center);
        logoutButton.setTransform(true);
        logoutButton.setOrigin(Align.center);
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
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK, new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog profileDialog = new Dialog("", windowStyle);

        Table wrapper = new Table();
        wrapper.pad(26);
        wrapper.defaults().pad(6);

        Table profileHeader = new Table();
        String[] perfil = userLogic.getPerfil(main.username);
        final Image profileAvatar = new Image(new Texture(Gdx.files.internal(userLogic.getAvatar(main.username))));

        Table textInfo = new Table();
        Label.LabelStyle styleDark = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        textInfo.add(new Label(gestorIdiomas.setTexto("profile.username") + main.username, styleDark)).left().row();
        textInfo.add(new Label(gestorIdiomas.setTexto("profile.fullname") + (perfil != null && perfil.length >= 3 ? perfil[2] : ""), styleDark)).left().padTop(6).row();

        profileHeader.add(profileAvatar).size(128, 128).padRight(16);
        profileHeader.add(textInfo).left().expandX();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        final TextButton changeAvatarButton = new TextButton(gestorIdiomas.setTexto("profile.change_avatar"), btnStyle);
        profileHeader.add(changeAvatarButton).right().width(260).height(60);

        wrapper.add(profileHeader).growX().row();

        Table bandScoresTitle = new Table();
        bandScoresTitle.setBackground(solid(0, 0, 0, 0.08f));
        Label hsTitle = new Label(gestorIdiomas.setTexto("profile.highscores"), styleDark);
        hsTitle.setAlignment(Align.center);
        bandScoresTitle.add(hsTitle).growX().pad(6);
        wrapper.add(bandScoresTitle).growX().padTop(12).row();

        Table headerScores = new Table();
        headerScores.setBackground(solid(0, 0, 0, 0.05f));
        headerScores.add(new Label(gestorIdiomas.setTexto("history.nivel"), styleDark)).width(320f).center().pad(4);
        headerScores.add(new Label(gestorIdiomas.setTexto("history.score"), styleDark)).width(320f).center().pad(4);
        wrapper.add(headerScores).padTop(4).row();

        Table scoresTable = new Table();
        scoresTable.defaults().pad(4);
        Map<Integer, Integer> highScores = userLogic.getHighScores(main.username);
        if (highScores.isEmpty()) {
            scoresTable.add(new Label(gestorIdiomas.setTexto("profile.no_scores"), styleDark)).pad(6).colspan(2).center();
        } else {
            ArrayList<Integer> sortedLevels = new ArrayList<>(highScores.keySet());
            Collections.sort(sortedLevels);
            for (Integer level : sortedLevels) {
                scoresTable.add(new Label(gestorIdiomas.setTexto("history.nivel") + " " + level, styleDark)).width(320f).center();
                scoresTable.add(new Label(String.valueOf(highScores.get(level)), styleDark)).width(320f).center();
                scoresTable.row();
            }
        }
        wrapper.add(new ScrollPane(scoresTable)).width(700).height(220).padTop(6).row();

        TextButton viewHistoryButton = new TextButton(gestorIdiomas.setTexto("historial.view"), btnStyle);
        TextButton achievementsButton = new TextButton(gestorIdiomas.setTexto("profile.achievements"), btnStyle);
        TextButton closeButton = new TextButton(gestorIdiomas.setTexto("profile.close"), btnStyle);

        Table btns = new Table();
        btns.defaults().width(280).height(58).pad(8);
        btns.add(viewHistoryButton);
        btns.add(achievementsButton);
        btns.add(closeButton);
        wrapper.add(btns).padTop(8).row();

        changeAvatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
                showAvatarSelectionDialog(profileAvatar);
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                changeAvatarButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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

        viewHistoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
                showHistoryDialog();
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                viewHistoryButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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

        achievementsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
                showAchievementsDialog();
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                achievementsButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                closeButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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

        changeAvatarButton.setTransform(true);
        changeAvatarButton.setOrigin(Align.center);
        viewHistoryButton.setTransform(true);
        viewHistoryButton.setOrigin(Align.center);
        achievementsButton.setTransform(true);
        achievementsButton.setOrigin(Align.center);
        closeButton.setTransform(true);
        closeButton.setOrigin(Align.center);

        profileDialog.getContentTable().add(wrapper).prefWidth(1040).prefHeight(560);
        profileDialog.show(stage);
    }

    private void showAvatarSelectionDialog(final Image profileAvatarImage) {
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK, new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog avatarDialog = new Dialog("", windowStyle);

        Table wrapper = new Table();
        wrapper.pad(28);

        Label.LabelStyle styleDark = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        wrapper.add(new Label(gestorIdiomas.setTexto("avatar.select"), styleDark)).padBottom(15).row();

        Table avatarTable = new Table();
        avatarTable.defaults().size(96, 96).pad(12);
        selectedAvatarPath = null;

        final String[] avatarPaths = {"avatares/south.png", "avatares/avatar1.png", "avatares/avatar2.png", "avatares/avatar3.png", "avatares/avatar4.png", "avatares/avatar5.png", "avatares/avatar6.png"};
        final List<Image> avatarImages = new ArrayList<>();

        for (final String path : avatarPaths) {
            final Image img = new Image(new Texture(Gdx.files.internal(path)));
            img.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedAvatarPath = path;
                    for (Image image : avatarImages) {
                        image.setColor(Color.WHITE);
                    }
                    img.setColor(Color.LIME);
                }
            });
            avatarTable.add(img);
            avatarImages.add(img);
            if (avatarImages.size() % 4 == 0) {
                avatarTable.row();
            }
        }
        wrapper.add(avatarTable).row();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        TextButton saveButton = new TextButton(gestorIdiomas.setTexto("avatar.save"), btnStyle);
        TextButton backButton = new TextButton(gestorIdiomas.setTexto("avatar.back"), btnStyle);

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
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                saveButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                avatarDialog.hide();
                showProfileDialog();
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                backButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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

        saveButton.setTransform(true);
        saveButton.setOrigin(Align.center);

        backButton.setTransform(true);
        backButton.setOrigin(Align.center);

        avatarDialog.getContentTable().add(wrapper).prefWidth(820).prefHeight(480);
        avatarDialog.show(stage);
    }

    private void showHistoryDialog() {
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK, new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog dlg = new Dialog("", windowStyle);

        Table root = new Table();
        root.pad(22).defaults().pad(6);

        Table band = new Table();
        band.setBackground(solid(0, 0, 0, 0.08f));
        Label title = new Label(gestorIdiomas.setTexto("history.title"), new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E")));
        title.setAlignment(Align.center);
        band.add(title).growX().pad(15, 0, 15, 0).row();
        root.add(band).growX().row();

        Label.LabelStyle headerStyle = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        Label.LabelStyle cellStyle = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));

        float[] colW = {320f, 90f, 110f, 110f, 120f, 120f};
        Table header = new Table();
        header.setBackground(solid(0, 0, 0, 0.06f));
        header.defaults().pad(4);
        header.add(new Label(gestorIdiomas.setTexto("history.fecha"), headerStyle)).width(colW[0]).center();
        header.add(new Label(gestorIdiomas.setTexto("history.nivel"), headerStyle)).width(colW[1]).center();
        header.add(new Label(gestorIdiomas.setTexto("history.score"), headerStyle)).width(colW[2]).center();
        header.add(new Label(gestorIdiomas.setTexto("history.intentos"), headerStyle)).width(colW[3]).center();
        header.add(new Label(gestorIdiomas.setTexto("history.duracion"), headerStyle)).width(colW[4]).center();
        header.add(new Label(gestorIdiomas.setTexto("history.resultado"), headerStyle)).width(colW[5]).center();
        root.add(header).width(1100f).row();

        Table rows = new Table();
        List<LogicaUsuarios.HistorialRegistro> lista = userLogic.leerHistorial(main.username);
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (int i = 0; i < lista.size(); i++) {
            LogicaUsuarios.HistorialRegistro r = lista.get(i);
            Table line = new Table();
            if (i % 2 == 0) {
                line.setBackground(solid(0, 0, 0, 0.05f));
            }
            line.defaults().pad(5);
            line.add(new Label(fmt.format(new Date(r.fechaMs)), cellStyle)).width(colW[0]).left().padLeft(15);
            line.add(new Label(String.valueOf(r.nivel), cellStyle)).width(colW[1]).center();
            line.add(new Label(String.valueOf(r.score), cellStyle)).width(colW[2]).center();
            line.add(new Label(String.valueOf(r.intentos), cellStyle)).width(colW[3]).center();
            line.add(new Label(String.format("%02d:%02d", (r.duracionMs / 1000) / 60, (r.duracionMs / 1000) % 60), cellStyle)).width(colW[4]).center();
            line.add(new Label(r.exito ? gestorIdiomas.setTexto("history.yes") : gestorIdiomas.setTexto("history.no"), cellStyle)).width(colW[5]).center();
            rows.add(line).growX().row();
        }

        root.add(new ScrollPane(rows)).width(1100f).height(560f - 140f).padTop(6).row();

        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle();
        btn.font = pixelFont;
        btn.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        TextButton back = new TextButton(gestorIdiomas.setTexto("back.button"), btn);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dlg.hide();
                showProfileDialog();
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                back.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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
        
        back.setTransform(true);
        back.setOrigin(Align.center);

        Table btnRow = new Table();
        btnRow.add(back).width(340).height(60).padTop(6).center();
        root.add(btnRow).center().padTop(10).row();

        dlg.getContentTable().add(root).prefWidth(1120).prefHeight(620);
        dlg.show(stage);
    }

    // --- MÉTODO DE LOGROS MODIFICADO ---
    private void showAchievementsDialog() {
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.valueOf("1E1E1E"),
                new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));

        final Dialog achievementsDialog = new Dialog(gestorIdiomas.setTexto("achievements.title"), windowStyle);
        achievementsDialog.getTitleLabel().setAlignment(Align.center);

        achievementsDialog.padTop(85);

        Table contentTable = new Table();
        contentTable.top();

        Set<String> unlockedAchievements = userLogic.getLogrosDesbloqueados(main.username);
        String[] achievementKeys = {"complete_3_levels", "complete_6_levels", "complete_all_levels", "high_score", "speed_demon"};

        Texture unlockedTex = new Texture(Gdx.files.internal("ui/checkbox.png"));
        Texture lockedTex = new Texture(Gdx.files.internal("ui/chekbox no fill.png"));
        Label.LabelStyle nameStyle = new Label.LabelStyle(pixelFont, Color.valueOf("1E1E1E"));
        Label.LabelStyle descStyle = new Label.LabelStyle(pixelFont, Color.GRAY);

        for (String key : achievementKeys) {
            Table achievementRow = new Table();
            achievementRow.left().defaults().pad(5);
            achievementRow.add(new Image(unlockedAchievements.contains(key) ? unlockedTex : lockedTex)).size(40, 40);

            Table textTable = new Table();
            textTable.left();

            textTable.add(new Label(gestorIdiomas.setTexto("achievement." + key + ".name"), nameStyle)).left().padBottom(5).row();
            textTable.add(new Label(gestorIdiomas.setTexto("achievement." + key + ".desc"), descStyle)).left();

            achievementRow.add(textTable).expandX().left();

            contentTable.add(achievementRow).expandX().fillX().padLeft(20).padBottom(10).row();
        }

        ScrollPane scrollPane = new ScrollPane(contentTable, new ScrollPane.ScrollPaneStyle());
        achievementsDialog.getContentTable().add(scrollPane).expand().fill().pad(10, 40, 10, 40);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = pixelFont;
        btnStyle.fontColor = Color.valueOf("1E1E1E");
        btnStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        TextButton backButton = new TextButton(gestorIdiomas.setTexto("achievements.back"), btnStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                achievementsDialog.hide();
                showProfileDialog();
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                backButton.addAction(Actions.sequence(Actions.scaleTo(0.9f, 0.9f, 0.05f), Actions.scaleTo(1f, 1f, 0.05f)));
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
        
        backButton.setTransform(true);
        backButton.setOrigin(Align.center);

        achievementsDialog.getButtonTable().add(backButton).width(280).height(58).padBottom(30);

        achievementsDialog.show(stage);
        achievementsDialog.setSize(1040, 560);
        achievementsDialog.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, Align.center);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        main.playMenuMusic();
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
