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

import java.util.*;

public class MenuScreen implements Screen {

    private final Main main;
    private final Stage stage;
    private final LogicaUsuarios userLogic;
    private final GestorIdiomas gestorIdiomas;
    private final Texture backgroundTexture;
    private final BitmapFont pixelFont;
    private final BitmapFont titleFont;
    private Image avatarImage;

    private final String[] AVATARS = new String[]{
            "avatares/south.png",
            "avatares/avatar1.png",
            "avatares/avatar2.png",
            "avatares/avatar3.png",
            "avatares/avatar4.png",
            "avatares/avatar5.png",
            "avatares/avatar6.png"
    };
    private final Map<String, Texture> avatarCache = new HashMap<String, Texture>();

    public MenuScreen(final Main main) {
        this.main = main;
        this.stage = new Stage(new ScreenViewport());
        this.userLogic = new LogicaUsuarios();
        this.gestorIdiomas = GestorIdiomas.obtenerInstancia();
        this.backgroundTexture = new Texture(Gdx.files.internal("background2.png"));

        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("Font/testing.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = 32; p.color = Color.valueOf("F5F5DC");
        pixelFont = gen.generateFont(p);
        p.size = 84; titleFont = gen.generateFont(p);
        gen.dispose();

        for (String path : AVATARS) avatarCache.put(path, new Texture(Gdx.files.internal(path)));

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Table profileBar = new Table();
        profileBar.setFillParent(true);
        profileBar.top().right();
        stage.addActor(profileBar);

        String username = main.username != null ? main.username : "";
        String avatarPath = userLogic.getAvatar(username);
        Texture avatarTex = avatarCache.containsKey(avatarPath) ? avatarCache.get(avatarPath) : new Texture(Gdx.files.internal(avatarPath));
        avatarImage = new Image(new TextureRegionDrawable(new TextureRegion(avatarTex)));

        Label.LabelStyle nameStyle = new Label.LabelStyle(pixelFont, Color.WHITE);
        Label nameLabel = new Label(username, nameStyle);

        profileBar.add(nameLabel).right().pad(20);
        profileBar.add(avatarImage).size(80, 80).pad(20);
        avatarImage.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y){ showProfileDialog(); }});

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label title = new Label(gestorIdiomas.setTexto("app.name"), titleStyle);
        table.add(title).padBottom(40).row();

        TextButton.TextButtonStyle btn = new TextButton.TextButtonStyle();
        btn.font = pixelFont;
        btn.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ui/button1.png"))));

        TextButton play = new TextButton(gestorIdiomas.setTexto("menu.jugar"), btn);
        TextButton amigos = new TextButton(gestorIdiomas.setTexto("menu.amigos"), btn);
        TextButton prefs = new TextButton(gestorIdiomas.setTexto("menu.preferencias"), btn);
        TextButton logout = new TextButton(gestorIdiomas.setTexto("menu.cerrar_sesion"), btn);

        table.add(play).width(380).height(60).pad(10).row();
        table.add(amigos).width(380).height(60).pad(10).row();
        table.add(prefs).width(380).height(60).pad(10).row();
        table.add(logout).width(380).height(60).pad(10).row();

        play.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ transicionSuave.fadeOutAndChangeScreen(main, stage, new LevelSelectScreen(main)); }});
        amigos.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ transicionSuave.fadeOutAndChangeScreen(main, stage, new AmigosScreen(main)); }});
        prefs.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ transicionSuave.fadeOutAndChangeScreen(main, stage, new PreferenciasScreen(main)); }});
        logout.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){
            main.username = null; LogicaUsuarios.usuarioLogged = null; main.resetToDefaults(); gestorIdiomas.resetToDefault();
            transicionSuave.fadeOutAndChangeScreen(main, stage, new LoginScreen(main));
        }});
    }

    // ===================== PERFIL =====================
    private void showProfileDialog() {
        Window.WindowStyle ws = new Window.WindowStyle(
                pixelFont, Color.BLACK,
                new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog dlg = new Dialog("", ws);
        dlg.setModal(true); dlg.setMovable(false);

        final float DW = 540f, DH = 450f;     // field
        final float PAD = 18f;
        final float INNER_W = DW - 2*PAD;

        Label.LabelStyle styleDark  = new Label.LabelStyle(pixelFont, Color.BLACK);
        Label.LabelStyle styleMuted = new Label.LabelStyle(pixelFont, Color.valueOf("2b2b2b"));

        Table root = new Table();
        root.pad(PAD);
        root.defaults().space(6);

        // header
        String[] perfil = userLogic.getPerfil(main.username);
        String avatarPath = userLogic.getAvatar(main.username);
        final Image bigAvatar = new Image(new TextureRegionDrawable(avatarCache.get(avatarPath)));

        Table header = new Table();
        header.add(bigAvatar).size(96, 96).padRight(14).top();

        Table info = new Table();
        Label lUserCaption = new Label("Usuario:", styleMuted);  lUserCaption.setFontScale(0.85f);
        Label lUserValue   = new Label(" " + main.username, styleDark);
        Label lNameCaption = new Label("Nombre:", styleMuted);   lNameCaption.setFontScale(0.85f);
        Label lNameValue   = new Label(" " + (perfil[2] != null ? perfil[2] : ""), styleDark);
        info.add(lUserCaption).left(); info.add(lUserValue).left().row();
        info.add(lNameCaption).left().padTop(4); info.add(lNameValue).left().padTop(4).row();
        header.add(info).left().top().width(INNER_W - 96 - 14);
        root.add(header).width(INNER_W).row();

        // separador
        Image sep1 = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png"))));
        sep1.setColor(0,0,0,0.25f);
        root.add(sep1).height(2).width(INNER_W).padTop(6).padBottom(6).row();

        // título centrado
        Label hsTitle = new Label("Mejores Puntuaciones", styleDark);
        hsTitle.setFontScale(1.05f);
        Table titleWrap = new Table(); titleWrap.add(hsTitle).center();
        root.add(titleWrap).width(INNER_W).row();

        // tabla de puntuaciones
        Table scoresTable = new Table();
        Map<Integer, Integer> highScores = userLogic.getHighScores(main.username);
        if (highScores.isEmpty()) {
            scoresTable.add(new Label("No hay puntuaciones guardadas.", styleDark)).padTop(12).padBottom(12);
        } else {
            ArrayList<Integer> sorted = new ArrayList<Integer>(highScores.keySet());
            Collections.sort(sorted);
            for (Integer lvl : sorted) {
                scoresTable.add(new Label("Nivel " + lvl + ":", styleDark)).right().padRight(8);
                scoresTable.add(new Label(String.valueOf(highScores.get(lvl)), styleDark)).left().row();
            }
        }
        ScrollPane sp = new ScrollPane(scoresTable);
        root.add(sp).width(INNER_W).height(160).padTop(4).row();

        // separador inferior
        Image sep2 = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png"))));
        sep2.setColor(0,0,0,0.25f);
        root.add(sep2).height(2).width(INNER_W).padTop(6).padBottom(6).row();

        // botones centrados (ancho dinámico para caber SIEMPRE)
        TextButton.TextButtonStyle sBtn = new TextButton.TextButtonStyle();
        sBtn.font = pixelFont; sBtn.fontColor = Color.BLACK;
        sBtn.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        final float GAP = 8f;
        final float BW = (INNER_W - 2*GAP) / 3f;  // 3 botones

        TextButton change  = new TextButton(gestorIdiomas.setTexto("profile.change_avatar"), sBtn);
        TextButton history = new TextButton("Ver historial", sBtn);
        TextButton close   = new TextButton(gestorIdiomas.setTexto("profile.close"), sBtn);

        Table actions = new Table();
        actions.defaults().width(BW).height(50).space(GAP);
        actions.add(change); actions.add(history); actions.add(close);
        root.add(actions).center().width(INNER_W).row();

        change.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dlg.hide();
                showAvatarSelectionDialog(new Runnable() { @Override public void run() { showProfileDialog(); }});
            }
        });
        history.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                dlg.hide();
                showHistoryDialog(new Runnable() { @Override public void run() { showProfileDialog(); }});
            }
        });
        close.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){ dlg.hide(); }});

        dlg.getContentTable().add(root).width(DW).height(DH);
        dlg.show(stage);
        dlg.setSize(DW, DH);
        float cx = (stage.getViewport().getWorldWidth() - DW) / 2f;
        float cy = (stage.getViewport().getWorldHeight() - DH) / 2f;
        dlg.setPosition(cx, cy);
    }

    // ===================== SELECTOR DE AVATAR =====================
    private void showAvatarSelectionDialog(final Runnable onReturnToProfile) {
        Window.WindowStyle ws = new Window.WindowStyle(
                pixelFont, Color.BLACK,
                new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog dlg = new Dialog("", ws);
        dlg.setModal(true); dlg.setMovable(false);

        final float DW = 560f, DH = 480f;
        final float PAD = 18f;
        final float INNER_W = DW - 2*PAD;

        Label.LabelStyle dark = new Label.LabelStyle(pixelFont, Color.BLACK);

        Table root = new Table();
        root.pad(PAD); root.defaults().space(6);

        Label title = new Label("Seleccionar avatar", dark);
        Table titleWrap = new Table(); titleWrap.add(title).center();
        root.add(titleWrap).width(INNER_W).padTop(4).row();

        final String[] selected = new String[]{ userLogic.getAvatar(main.username) };
        final Image preview = new Image(new TextureRegionDrawable(avatarCache.get(selected[0])));
        root.add(preview).size(128,128).padBottom(8).row();

        Table grid = new Table();
        int col = 0;
        final ArrayList<Container<Image>> cards = new ArrayList<Container<Image>>();
        for (final String path : AVATARS) {
            Image img = new Image(new TextureRegionDrawable(avatarCache.get(path)));
            final Container<Image> card = new Container<Image>(img);
            card.size(86, 86); card.pad(4);
            card.background(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png"))));
            if (path.equals(selected[0])) card.setColor(1f,1f,0.75f,1f);
            card.addListener(new ClickListener(){
                @Override public void clicked(InputEvent e, float x, float y){
                    selected[0] = path;
                    preview.setDrawable(new TextureRegionDrawable(avatarCache.get(path)));
                    for (Container<Image> c : cards) c.setColor(Color.WHITE);
                    card.setColor(1f,1f,0.75f,1f);
                }
            });
            cards.add(card);
            grid.add(card).pad(8);
            col++; if (col % 4 == 0) grid.row();
        }
        ScrollPane sp = new ScrollPane(grid);
        root.add(sp).width(INNER_W).height(250).row();

        TextButton.TextButtonStyle sBtn = new TextButton.TextButtonStyle();
        sBtn.font = pixelFont; sBtn.fontColor = Color.BLACK;
        sBtn.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));

        final float GAP = 12f;
        final float BW = (INNER_W - GAP) / 2f; // 2 botones

        TextButton save = new TextButton("Guardar", sBtn);
        TextButton cancel = new TextButton("Cancelar", sBtn);

        Table actions = new Table();
        actions.defaults().width(BW).height(50).space(GAP);
        actions.add(save); actions.add(cancel);
        root.add(actions).center().width(INNER_W).row();

        save.addListener(new ClickListener(){
            @Override public void clicked(InputEvent e, float x, float y){
                String path = selected[0];
                userLogic.setAvatar(main.username, path);
                avatarImage.setDrawable(new TextureRegionDrawable(avatarCache.get(path)));
                dlg.hide();
                if (onReturnToProfile != null) onReturnToProfile.run();
            }
        });
        cancel.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){
            dlg.hide(); if (onReturnToProfile != null) onReturnToProfile.run();
        }});

        dlg.getContentTable().add(root).width(DW).height(DH);
        dlg.show(stage);
        dlg.setSize(DW, DH);
        float cx = (stage.getViewport().getWorldWidth() - DW) / 2f;
        float cy = (stage.getViewport().getWorldHeight() - DH) / 2f;
        dlg.setPosition(cx, cy);
    }

    // ===================== HISTORIAL =====================
    private void showHistoryDialog(final Runnable onReturn) {
        Window.WindowStyle ws = new Window.WindowStyle(
                pixelFont, Color.BLACK,
                new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/field 2.png"))));
        final Dialog dlg = new Dialog("", ws);
        dlg.setModal(true); dlg.setMovable(false);

        final float DW = 600f, DH = 520f;
        final float PAD = 22f;            // un poco más de margen arriba
        final float INNER_W = DW - 2*PAD;

        Label.LabelStyle dark = new Label.LabelStyle(pixelFont, Color.BLACK);

        Table root = new Table();
        root.pad(PAD); root.defaults().space(6);

        // Título interno centrado
        Label heading = new Label("Historial de partidas", dark);
        Table headWrap = new Table(); headWrap.add(heading).center();
        root.add(headWrap).width(INNER_W).padBottom(6).row();

        // Cabecera de columnas (todo DENTRO del field)
        Table header = new Table(); header.defaults().pad(2);
        header.add(new Label("Fecha", dark)).left().width(160).padRight(6);
        header.add(new Label("Nivel", dark)).left().width(60).padRight(6);
        header.add(new Label("Score", dark)).left().width(80).padRight(6);
        header.add(new Label("Intentos", dark)).left().width(90).padRight(6);
        header.add(new Label("Duración", dark)).left().width(100).padRight(6);
        header.add(new Label("Resultado", dark)).left().width(90);
        root.add(header).width(INNER_W).row();

        Image sep = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png"))));
        sep.setColor(0,0,0,0.25f);
        root.add(sep).height(2).width(INNER_W).row();

        // Cuerpo con scroll
        Table body = new Table();
        java.util.List<LogicaUsuarios.HistorialRegistro> registros = userLogic.leerHistorial(main.username);
        if (registros.isEmpty()) {
            body.add(new Label("No hay registros.", dark)).padTop(12).padBottom(12);
        } else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (LogicaUsuarios.HistorialRegistro r : registros) {
                body.add(new Label(sdf.format(new java.util.Date(r.fechaMs)), dark)).left().width(160).padRight(6);
                body.add(new Label(String.valueOf(r.nivel), dark)).left().width(60).padRight(6);
                body.add(new Label(String.valueOf(r.score), dark)).left().width(80).padRight(6);
                body.add(new Label(String.valueOf(r.intentos), dark)).left().width(90).padRight(6);
                body.add(new Label(formatDur(r.duracionMs), dark)).left().width(100).padRight(6);
                body.add(new Label(r.exito ? "Éxito" : "Falló/Salió", dark)).left().width(90).row();
            }
        }
        ScrollPane sp = new ScrollPane(body);
        root.add(sp).width(INNER_W).height(DH - 200).row();

        // Botón centrado
        TextButton.TextButtonStyle sBtn = new TextButton.TextButtonStyle();
        sBtn.font = pixelFont; sBtn.fontColor = Color.BLACK;
        sBtn.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("ui/button1.png")));
        TextButton back = new TextButton("Volver", sBtn);

        Table actions = new Table();
        actions.add(back).width(220).height(50).padTop(6);
        root.add(actions).center().width(INNER_W);

        back.addListener(new ClickListener(){ @Override public void clicked(InputEvent e, float x, float y){
            dlg.hide(); if (onReturn != null) onReturn.run();
        }});

        dlg.getContentTable().add(root).width(DW).height(DH);
        dlg.show(stage);
        dlg.setSize(DW, DH);
        float cx = (stage.getViewport().getWorldWidth() - DW) / 2f;
        float cy = (stage.getViewport().getWorldHeight() - DH) / 2f;
        dlg.setPosition(cx, cy);
    }

    private String formatDur(long ms) {
        long totalSec = Math.max(0, ms / 1000);
        long h = totalSec / 3600;
        long m = (totalSec % 3600) / 60;
        long s = totalSec % 60;
        if (h > 0) return String.format("%d:%02d:%02d", h, m, s);
        return String.format("%02d:%02d", m, s);
    }

    // ===================== ciclo pantalla =====================
    @Override public void show() { Gdx.input.setInputProcessor(stage); main.playLobbyMusic(); transicionSuave.fadeIn(stage); }
    @Override public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        pixelFont.dispose();
        titleFont.dispose();
        // Si cierras toda la app, podrías liberar el cache:
        // for (Texture t : avatarCache.values()) t.dispose();
    }
}
