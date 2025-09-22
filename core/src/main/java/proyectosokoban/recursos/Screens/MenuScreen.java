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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import proyectosokoban.recursos.Main;
import proyectosokoban.recursos.Utilidades.GestorIdiomas;
import proyectosokoban.recursos.Utilidades.LogicaUsuarios;
import proyectosokoban.recursos.Utilidades.transicionSuave;

import java.util.ArrayList;
import java.util.Collections;
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

        // --- CORRECCIÓN: Se usa el método que sí existe ---
        String avatarPath = userLogic.getAvatar(main.username);
        avatarImage = new Image(new Texture(Gdx.files.internal(avatarPath)));
        
        Label.LabelStyle nameStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        Label nameLabel = new Label(main.username, nameStyle);
        
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
        final Dialog profileDialog = new Dialog(gestorIdiomas.setTexto("profile.title"), windowStyle);
        
        Table contentTable = new Table();
        contentTable.pad(20);

        // --- CORRECCIÓN: Se usa el método nuevo para obtener todos los datos ---
        String[] perfil = userLogic.getPerfil(main.username);
        final Image profileAvatar = new Image(new Texture(Gdx.files.internal(perfil[3])));
        
        Label.LabelStyle style = new Label.LabelStyle(pixelFont, Color.BLACK);
        Label nameLabel = new Label(gestorIdiomas.setTexto("profile.username") + main.username, style);
        Label fullNameLabel = new Label(gestorIdiomas.setTexto("profile.fullname") + perfil[2], style);
        
        Table textInfoTable = new Table();
        textInfoTable.add(nameLabel).left().row();
        textInfoTable.add(fullNameLabel).left().padTop(10).row();
        
        Table infoTable = new Table();
        infoTable.add(profileAvatar).size(128, 128).padRight(20);
        infoTable.add(textInfoTable).left();
        contentTable.add(infoTable).padBottom(20).row();
        
        contentTable.add(new Label(gestorIdiomas.setTexto("profile.highscores"), style)).padBottom(10).row();
        
        Table scoresTable = new Table();
        Map<Integer, Integer> highScores = userLogic.getHighScores(main.username);
        if (highScores.isEmpty()) {
            scoresTable.add(new Label("No hay puntuaciones guardadas.", style));
        } else {
            ArrayList<Integer> sortedLevels = new ArrayList<>(highScores.keySet());
            Collections.sort(sortedLevels);
            for (Integer level : sortedLevels) {
                scoresTable.add(new Label("Nivel " + level + ": ", style)).right().padRight(10);
                scoresTable.add(new Label(String.valueOf(highScores.get(level)), style)).left().row();
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(scoresTable);
        contentTable.add(scrollPane).height(150).width(400).padTop(10).row();
        
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = pixelFont;
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.up = new TextureRegionDrawable(new Texture("ui/button1.png"));
        
        TextButton changeAvatarButton = new TextButton(gestorIdiomas.setTexto("profile.change_avatar"), buttonStyle);
        TextButton closeButton = new TextButton(gestorIdiomas.setTexto("profile.close"), buttonStyle);
        
        Table buttonTable = new Table();
        buttonTable.add(changeAvatarButton).width(250).height(50).pad(15);
        buttonTable.add(closeButton).width(250).height(50).pad(15);
        contentTable.add(buttonTable).padTop(20).row();
        
        changeAvatarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showAvatarSelectionDialog(profileAvatar);
            }
        });
        
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                profileDialog.hide();
            }
        });

        profileDialog.getContentTable().add(contentTable);
        profileDialog.show(stage);
    }
    
    private void showAvatarSelectionDialog(final Image profileAvatarImage) {
        Window.WindowStyle windowStyle = new Window.WindowStyle(pixelFont, Color.BLACK, new TextureRegionDrawable(new Texture("ui/field 2.png")));
        final Dialog avatarDialog = new Dialog(gestorIdiomas.setTexto("avatar.title"), windowStyle);
        
        Table avatarTable = new Table();
        avatarTable.pad(20);
        
        final String[] avatarPaths = {
            "avatares/south.png", "avatares/avatar1.png", "avatares/avatar2.png", 
            "avatares/avatar3.png", "avatares/avatar4.png", "avatares/avatar5.png", "avatares/avatar6.png"
        };
        
        int col = 0;
        for (final String path : avatarPaths) {
            final Image img = new Image(new Texture(Gdx.files.internal(path)));
            img.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // --- CORRECCIÓN PARA GUARDAR AVATAR ---
                    userLogic.setAvatar(main.username, path);
                    
                    Texture newAvatarTexture = new Texture(Gdx.files.internal(path));
                    avatarImage.setDrawable(new TextureRegionDrawable(newAvatarTexture));
                    profileAvatarImage.setDrawable(new TextureRegionDrawable(newAvatarTexture));
                    
                    avatarDialog.hide();
                }
            });
            avatarTable.add(img).size(80, 80).pad(10);
            col++;
            if (col % 4 == 0) {
                avatarTable.row();
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(avatarTable);
        avatarDialog.getContentTable().add(scrollPane);
        avatarDialog.show(stage);
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